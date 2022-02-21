package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import com.google.crypto.tink.subtle.Hex;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mchange.rmi.NotAuthorizedException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FhirUtil;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.ReadPatientsToken;
import de.samply.share.client.model.ReadPatientsToken.ID;
import de.samply.share.client.model.ReadPatientsToken.TokenData;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.util.connector.exception.ConflictException;
import de.samply.share.client.util.connector.exception.MainzellisteConnectorException;
import de.samply.share.client.util.connector.exception.MandatoryAttributeException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.EventLogMainzellisteUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.MediaType;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mainzelliste Connector.
 */
public class MainzellisteConnector {

  private static final Logger logger = LoggerFactory.getLogger(MainzellisteConnector.class);
  private static final String FHIR_RESOURCE_PATIENT = "patient";
  private static final String FHIR_RESOURCE_COVERAGE = "coverage";
  private static final String FHIR_RESOURCE_COMPOSITION = "composition";
  private static final String MAINZELLISTE_IDTYPE_ENC_ID = "EncID";
  private static final String IDAT_VORNAME = "vorname";
  private static final String IDAT_NACHNAME = "nachname";
  private static final String IDAT_GEBURTSDATUM = "Geburtsdatum";
  private static final String IDAT_GEBURTSTAG = "geburtstag";
  private static final String IDAT_GEBURTSMONAT = "geburtsmonat";
  private static final String IDAT_GEBURTSJAHR = "geburtsjahr";
  private static final String IDAT_ADRESSE_STADT = "adresse.stadt";
  private static final String IDAT_ADRESSE_PLZ = "adresse.plz";
  private static final String IDAT_ADRESSE_STRASSE = "adresse.strasse";
  private static final String IDAT_REQUESTED_ID_TYPE = "requestedIdType";
  private static final String IDAT_CTSID = "ctsid";
  private static final String IDAT_VERSICHERUNGSNUMMER = "versicherungsnummer";
  private static final String CTS_COVERAGE_PROFILE =
      "http://uk-koeln.de/fhir/StructureDefinition/Coverage/nNGM/pseudonymisiert";
  private static final String PATIENT_IDENTIFIER_SYSTEM =
      "http://uk-koeln.de/fhir/NamingSystem/nNGM/patient-identifier";
  private static final String GET_ENCRYPT_ID_URL = "/paths/getEncryptId";
  private static final String GET_ENCRYPT_ID_WITH_PATIENT_ID_URL = "/paths/getEncryptIdWithId";
  private static final String HEADER_PARAM_API_KEY = "apiKey";
  private static final String STAMMDATEN_PSEUDONYMISIERT_PROFILE = "http://uk-koeln.de/fhir/StructureDefinition/Composition/nNGM/Stammdaten-pseudonymisiert";
  private static final String ANTRAG_PSEUDONYMISIERT_PROFILE = "http://uk-koeln.de/fhir/StructureDefinition/Composition/nNGM/Antrag-pseudonymisiert";
  private static final String TNM_PSEUDONYMISIERT_PROFILE = "http://uk-koeln.de/fhir/StructureDefinition/Composition/nNGM/TNM-pseudonymisiert";
  private static final String BEFUND_PSEUDONYMISIERT_PROFILE = "http://uk-koeln.de/fhir/StructureDefinition/Composition/nNGM/molpatho-befund-pseudonymisiert";
  private static final String THERAPIE_PSEUDONYMISIERT_PROFILE = "http://uk-koeln.de/fhir/StructureDefinition/Composition/nNGM/Therapie-pseudonymisiert";


  private final CloseableHttpClient httpClient;
  private final FhirUtil fhirUtil;


  /**
   * Creates a new MainzellisteConnector.
   *
   * @throws MalformedURLException if the mainzelliste url is not valid
   */
  public MainzellisteConnector() throws MalformedURLException {
    try {
      String mainzellisteBaseUrl = SamplyShareUtils.addTrailingSlash(
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_URL));
      HttpHost mainzellisteHost = SamplyShareUtils.getAsHttpHost(mainzellisteBaseUrl);
      httpClient = ApplicationBean.createHttpConnector().getHttpClient(mainzellisteHost);
    } catch (MalformedURLException e) {
      logger.error("Init Mainzelliste connection: MalformedURLException: e: " + e);
      throw e;
    }
    fhirUtil = new FhirUtil(FhirContext.forR4());
  }

  private EventLog createAuditEventLog(AuditEvent auditEvent) {
    EventLog eventLog = new EventLog();
    eventLog.setEventType(EventMessageType.E_PATIENT_CTS_UPLOAD);
    eventLog.setEntry(fhirUtil.encodeResourceToJson(auditEvent));
    return eventLog;
  }

  /**
   * pseudonymise a patient.
   *
   * @param bundle the patient bundle
   * @return the pseudonymized patient
   * @throws IOException                  IOException
   * @throws NotFoundException            NotFoundException
   * @throws NotAuthorizedException       NotFoundException
   * @throws MainzellisteConnectorException  MainzellisteConnectorException
   */

  public Bundle getPatientPseudonym(Bundle bundle)
      throws NotFoundException, NotAuthorizedException, IOException,
      MainzellisteConnectorException {
    Patient patient = null;
    Patient patientPseudonym = null;
    Coverage coverage = null;
    Coverage coveragePseudonym = null;
    Composition composition = null;
    Composition compositionPseudonym = null;
    JsonObject encryptedId;
    int patientEntryIndex = 0;
    int coverageEntryIndex = 0;
    int compositionEntryIndex = 0;
    try {
      for (int i = 0; i < bundle.getEntry().size(); i++) {
        Resource resource = bundle.getEntry().get(i).getResource();
        if (resource.fhirType().equalsIgnoreCase(FHIR_RESOURCE_COMPOSITION)) {
          composition = (Composition) resource;
          compositionPseudonym = pseudonymComposition(composition);
          compositionEntryIndex = i;
        } else if (resource.fhirType().equalsIgnoreCase(FHIR_RESOURCE_PATIENT)) {
          patient = (Patient) resource;
          patientPseudonym = createPseudonymizedPatient(patient);
          patientEntryIndex = i;
        } else if (resource.fhirType().equalsIgnoreCase(FHIR_RESOURCE_COVERAGE)) {
          coverage = (Coverage) resource;
          coveragePseudonym = pseudonymCoverage(coverage);
          coverageEntryIndex = i;
        }
        if (patient != null && coverage != null && composition != null) {
          JsonObject jsonIdatObject = createJsonPatient(patient, coverage);
          encryptedId = requestPseudonymFromMainzelliste(jsonIdatObject);
          patientPseudonym = addPseudonymToPatient(patientPseudonym, encryptedId);
          compositionPseudonym = addPseudonymToComposition(compositionPseudonym, encryptedId);
          bundle.getEntry().get(compositionEntryIndex).setResource(compositionPseudonym);
          bundle.getEntry().get(patientEntryIndex).setResource(patientPseudonym);
          bundle.getEntry().get(coverageEntryIndex).setResource(coveragePseudonym);
          return bundle;
        }
      }
      checkNonNull(patient, "The required patient resource is empty");
      checkNonNull(coverage, "The required coverage resource is empty");
      checkNonNull(composition, "The required composition resource is empty");
    } catch (ConflictException | MandatoryAttributeException | IllegalArgumentException e) {
      throw new MainzellisteConnectorException(e);
    }
    return bundle;
  }

  /**
   * Pseudonymise the Coverage resource.
   *
   * @param originalCoverage originalCoverage
   * @return Coverage
   */

  private Coverage pseudonymCoverage(Coverage originalCoverage) {
    Coverage coveragePseudonym = originalCoverage.copy();
    Meta meta = new Meta();
    //@TODO Hard-coded to avoid changing the configuration file and the installation program...
    // but it should be changed in the next versions to follow the default configuration.
    //String pseudonymizedProfile =
    // ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PROFILE);
    meta.addProfile(CTS_COVERAGE_PROFILE);
    coveragePseudonym.setMeta(meta);
    coveragePseudonym.getIdentifier().clear();
    return coveragePseudonym;
  }

  /**
   * Pseudonymise the Composition resource.
   *
   * @param originalComposition originalComposition
   * @return Coverage
   */

  private Composition pseudonymComposition(Composition originalComposition) {
    Composition compositionPseudonym = originalComposition.copy();

    Meta meta = new Meta();
    String canoicalPseudonymizedProfile = "";
    if (originalComposition.hasMeta()) {
      canoicalPseudonymizedProfile = getCanonicalPseudonymProfile(
          originalComposition.getMeta().getProfile().get(0).getValue());
    }
    if (compositionPseudonym.hasTitle()) {
      compositionPseudonym.setTitle("");
    }
    meta.addProfile(canoicalPseudonymizedProfile);
    compositionPseudonym.setMeta(meta);
    if (compositionPseudonym.hasCategory()) {
      compositionPseudonym.getCategoryFirstRep().getCoding().get(0).setCode("psn");
    }
    return compositionPseudonym;
  }

  /**
   * Get the profile to each composition.
   *
   * @param idatCompositionProfile path of the composition
   * @return the profile of the composition
   */
  private String getCanonicalPseudonymProfile(String idatCompositionProfile) {
    if (idatCompositionProfile.contains("Stammdaten")) {
      return STAMMDATEN_PSEUDONYMISIERT_PROFILE;
    } else if (idatCompositionProfile.contains("Antrag")) {
      return ANTRAG_PSEUDONYMISIERT_PROFILE;
    } else if (idatCompositionProfile.contains("TNM")) {
      return TNM_PSEUDONYMISIERT_PROFILE;
    } else if (idatCompositionProfile.contains("molpatho")) {
      return BEFUND_PSEUDONYMISIERT_PROFILE;
    } else if (idatCompositionProfile.contains("Therapie")) {
      return THERAPIE_PSEUDONYMISIERT_PROFILE;
    }
    return idatCompositionProfile;
  }


  /**
   * Create a new patient and add only the necessary attributes for a pseudonymized patient.
   *
   * @param originalPatient originalPatient
   * @return the pseudonymized patient
   */
  private Patient createPseudonymizedPatient(Patient originalPatient) {
    Patient patientNew = new Patient();
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    if (! originalPatient.getBirthDateElement().isEmpty()) {
      calendar.set(Calendar.YEAR, originalPatient.getBirthDateElement().getYear());
      DateType date = new DateType(calendar.getTime(), TemporalPrecisionEnum.YEAR);
      patientNew.setBirthDate(calendar.getTime());
      patientNew.setBirthDateElement(date);
    }
    patientNew.setId(originalPatient.getId());
    patientNew.setGender(originalPatient.getGender());
    patientNew.setDeceased(originalPatient.getDeceased());
    patientNew.setId(originalPatient.getId());
    String profile = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PROFILE);
    Meta meta = new Meta();
    meta.addProfile(profile);
    patientNew.setMeta(meta);
    List<Identifier> identifierList = new ArrayList<>();
    Identifier identifier = new Identifier();
    String identifierSystem = originalPatient.getIdentifierFirstRep().getSystem();
    if (identifierSystem != null) {
      identifier.setSystem(identifierSystem);
    } else {
      identifier.setSystem(PATIENT_IDENTIFIER_SYSTEM);
    }
    identifierList.add(identifier);
    patientNew.setIdentifier(identifierList);
    return patientNew;
  }

  /**
   * Add pseudonym to a Patient resource.
   *
   * @param patient     the patient
   * @param encryptedId encryptedID
   * @return the pseudonymized patient
   */
  private Patient addPseudonymToPatient(Patient patient, JsonObject encryptedId) {
    patient.getIdentifierFirstRep()
        .setValue(encryptedId.get(MAINZELLISTE_IDTYPE_ENC_ID).getAsString());
    return patient;
  }

  /**
   * Add the identifier to the composition.
   *
   * @param composition the composition
   * @param encryptedId encryptedID
   * @return composition with subject identifier
   */
  private Composition addPseudonymToComposition(Composition composition, JsonObject encryptedId) {
    Identifier identifier = new Identifier();
    identifier.setSystem("http://uk-koeln.de/fhir/NamingSystem/nNGM/patient-identifier");
    composition.getSubject().setIdentifier(identifier);
    composition.getSubject().getIdentifier()
        .setValue(encryptedId.get(MAINZELLISTE_IDTYPE_ENC_ID).getAsString());
    return composition;
  }

  /**
   * Extract the necessary attributes for the Mainzelliste from the patient.
   *
   * @param patient  the patient FHIR-resource
   * @param coverage the coverage FHIR-resource
   * @return A patient with the attributes from the original patient
   * @throws MandatoryAttributeException if mandatory attributes from patient or coverage are
   *        missing
   */
  private JsonObject createJsonPatient(Patient patient, Coverage coverage)
          throws MandatoryAttributeException {
    Objects.requireNonNull(patient);
    Objects.requireNonNull(coverage);
    JsonObject jsonIdatObject = new JsonObject();
    try {
      jsonIdatObject.addProperty(IDAT_VORNAME,
          checkIfAttributeExist(patient.getNameFirstRep().getGivenAsSingleString(), IDAT_VORNAME));
      jsonIdatObject.addProperty(IDAT_NACHNAME,
          checkIfAttributeExist(patient.getNameFirstRep().getFamily(), IDAT_NACHNAME));
      DateType birthDateElement = patient.getBirthDateElement();
      if (! birthDateElement.getPrecision().equals(TemporalPrecisionEnum.DAY)) {
        birthDateElement.setValue(null);
      }
      checkIfAttributeExist(birthDateElement.asStringValue(), IDAT_GEBURTSDATUM);
      int birthDay = birthDateElement.getDay();
      int birthMonth = birthDateElement.getMonth();
      birthMonth += 1; // +1 because Hapi returns the month with 0-index, e.g. 0=January
      String day = String.valueOf(birthDay);
      String month = String.valueOf(birthMonth);
      if (birthDay < 10) {
        day = String.format("%02d", birthDay);
      }
      if (birthMonth < 10) {
        month = String.format("%02d", birthMonth);
      }
      jsonIdatObject.addProperty(IDAT_GEBURTSTAG, checkIfAttributeExist(day, IDAT_GEBURTSTAG));
      jsonIdatObject
          .addProperty(IDAT_GEBURTSMONAT, checkIfAttributeExist(month, IDAT_GEBURTSMONAT));
      jsonIdatObject.addProperty(IDAT_GEBURTSJAHR,
          checkIfAttributeExist(birthDateElement.getYear().toString(), IDAT_GEBURTSJAHR));
      if (!patient.getAddressFirstRep().isEmpty()) {
        if (patient.getAddressFirstRep().hasCity()) {
          jsonIdatObject.addProperty(IDAT_ADRESSE_STADT, patient.getAddressFirstRep().getCity());
        }
        if (patient.getAddressFirstRep().hasPostalCode()) {
          jsonIdatObject
              .addProperty(IDAT_ADRESSE_PLZ, patient.getAddressFirstRep().getPostalCode());
        }
        if (patient.getAddressFirstRep().hasLine()) {
          jsonIdatObject
              .addProperty(IDAT_ADRESSE_STRASSE,
                  patient.getAddressFirstRep().getLine().get(0).getValue());
        }
      }
      jsonIdatObject.addProperty(IDAT_REQUESTED_ID_TYPE, IDAT_CTSID);
      if (coverage.hasIdentifier()) {
        jsonIdatObject.addProperty(IDAT_VERSICHERUNGSNUMMER,
            checkIfAttributeExist(coverage.getIdentifierFirstRep().getValue(),
                IDAT_VERSICHERUNGSNUMMER));
      }
    } catch (MandatoryAttributeException e) {
      logger.warn("Error at identifying patient data: " + e.getMessage());
      throw new MandatoryAttributeException(e.getMessage());
    }
    return jsonIdatObject;
  }

  /**
   * Check if an attribute is empty.
   *
   * @param attribute     attribute
   * @param attributeName attributeName
   * @return the attribute if its not empty
   * @throws MandatoryAttributeException if attribute is empty
   */
  private String checkIfAttributeExist(String attribute, String attributeName)
      throws MandatoryAttributeException {
    if (attribute == null || attribute.isEmpty()) {
      logger.error(getMissigErrorMessage(attributeName));
      throw new MandatoryAttributeException(getMissigErrorMessage(attributeName));
    } else {
      return attribute;
    }
  }

  /**
   * Check if a resource is empty.
   *
   * @param object  object
   * @param message message
   */
  private void checkNonNull(Object object, String message) throws MandatoryAttributeException {
    if (object == null) {
      throw new MandatoryAttributeException(message);
    }
  }

  /**
   * Post the original patient to the Mainzelliste and get an encrypted ID.
   *
   * @param patient the patient
   * @return an encrypted ID
   * @throws IOException IOException
   */
  private JsonObject requestPseudonymFromMainzelliste(JsonObject patient)
          throws IOException, IllegalArgumentException, NotFoundException,
          NotAuthorizedException, ConflictException {
    HttpPost httpPost = createHttpPost(GET_ENCRYPT_ID_URL);
    HttpEntity entity = new StringEntity(patient.toString(), Consts.UTF_8);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    JsonObject encryptedId;
    try {
      response = httpClient.execute(httpPost);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      String reasonPhrase = statusLine.getReasonPhrase();
      insertEventLog(statusCode);
      checkStatusCode(response, statusCode, reasonPhrase);
      String encryptedIdString = EntityUtils.toString(response.getEntity());
      encryptedId = JsonParser.parseString(encryptedIdString).getAsJsonObject();
    } catch (IOException e) {
      logger.error("Get Pseudonym from Mainzelliste: IOException: e: " + e);
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
    return encryptedId;
  }

  /**
   * Post the patient id to the Mainzelliste to get the encryptedId and replace it with the patient
   * id.
   *
   * @param patient the local cts patient
   * @return the patient with the replaced id
   */
  public JsonObject requestEncryptedIdForPatient(String patient)
      throws IOException,
      NotFoundException, NotAuthorizedException, MainzellisteConnectorException {
    JsonObject patientAsJson = JsonParser.parseString(patient).getAsJsonObject();
    JsonObject jsonEntity = new JsonObject();
    jsonEntity.addProperty("searchIdType",
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_SEARCH_ID_TYPE));
    jsonEntity.addProperty("searchIdString", patientAsJson.get("patid").getAsString());
    jsonEntity.addProperty("requestedIdType", "ctsid");
    HttpPost httpPost = createHttpPost(GET_ENCRYPT_ID_WITH_PATIENT_ID_URL);
    HttpEntity entity = new StringEntity(jsonEntity.toString(), Consts.UTF_8);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(httpPost);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      String reasonPhrase = statusLine.getReasonPhrase();
      insertEventLog(statusCode);
      checkStatusCode(response, statusCode, reasonPhrase);
      String encryptedIdString = EntityUtils.toString(response.getEntity());
      addEncryptedIdToPatient(patientAsJson, encryptedIdString);
    } catch (IOException e) {
      logger.error("Get Pseudonym from Mainzelliste: IOException: e: " + e);
      throw new IOException(e);
    } catch (ConflictException | IllegalArgumentException e) {
      throw new MainzellisteConnectorException(e);
    } finally {
      closeResponse(response);
    }
    return patientAsJson;
  }

  /**
   * Post the patient id to the Mainzelliste to get the encryptedId.
   *
   * @param id patientId
   * @return the requested encrypted id
   */
  public String requestEncryptedIdWithPatientId(String id)
          throws IOException, IllegalArgumentException,
          NotFoundException, NotAuthorizedException, ConflictException {
    JsonObject jsonEntity = new JsonObject();
    jsonEntity.addProperty("searchIdType",
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_SEARCH_ID_TYPE));
    jsonEntity.addProperty("searchIdString", id);
    jsonEntity.addProperty("requestedIdType", "ctsid");
    HttpPost httpPost = createHttpPost(GET_ENCRYPT_ID_WITH_PATIENT_ID_URL);
    HttpEntity entity = new StringEntity(jsonEntity.toString(), Consts.UTF_8);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(httpPost);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      String reasonPhrase = statusLine.getReasonPhrase();
      insertEventLog(statusCode);
      checkStatusCode(response, statusCode, reasonPhrase);
      String encryptedIdString = EntityUtils.toString(response.getEntity());
      JsonObject encryptedId = JsonParser.parseString(encryptedIdString).getAsJsonObject();
      return encryptedId.get("EncID").getAsString();
    } catch (IOException e) {
      logger.error("Get Pseudonym from Mainzelliste: IOException: e: " + e);
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
  }

  /**
   * Post the patient id to the Mainzelliste to get the localId.
   *
   * @param ctsIds ctsIds
   * @return the patient with the replaced id
   */
  public JsonArray getLocalId(List<String> ctsIds)
          throws IOException, IllegalArgumentException,
          NotFoundException, NotAuthorizedException, ConflictException {
    String id = getMainzellisteSessionId();
    String tokenId = getMainzellisteReadToken(id, ctsIds);
    HttpGet httpGet = new HttpGet(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PATIENT_LIST_URL)
            + "/patients/tokenId/" + tokenId);
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      String reasonPhrase = statusLine.getReasonPhrase();
      insertEventLog(statusCode);
      checkStatusCode(response, statusCode, reasonPhrase);
      String responseBody = EntityUtils.toString(response.getEntity());
      return JsonParser.parseString(responseBody).getAsJsonArray();
    } catch (IOException e) {
      logger.error("Get local id from Mainzelliste: IOException: e: " + e);
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
  }

  private String getMainzellisteSessionId() throws IOException, NotAuthorizedException,
          ConflictException {
    HttpPost httpPost = new HttpPost(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PATIENT_LIST_URL)
            + "/sessions");
    httpPost.setHeader("mainzellisteApiKey",
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PATIENT_LIST_API_KEY));
    CloseableHttpResponse response;
    try {
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      String reasonPhrase = response.getStatusLine().getReasonPhrase();
      insertEventLog(statusCode);
      checkStatusCode(response, statusCode, reasonPhrase);
      String stringSessionId = EntityUtils.toString(response.getEntity());
      JsonObject jsonObject = JsonParser.parseString(stringSessionId).getAsJsonObject();
      return jsonObject.get("sessionId").getAsString();
    } catch (IOException e) {
      logger.error("Get session uri from Mainzelliste: IOException: e: " + e);
      throw new IOException(e);
    }
  }

  private String getMainzellisteReadToken(String sessionId, List<String> ctsIds)
          throws IOException, NotAuthorizedException, ConflictException {
    HttpPost httpPost = new HttpPost(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PATIENT_LIST_URL)
            + "/sessions/" + sessionId + "/tokens");
    httpPost.setHeader("mainzellisteApiKey",
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PATIENT_LIST_API_KEY));
    HttpEntity entity = new StringEntity(createJsonObjectForReadPatients(ctsIds), Consts.UTF_8);
    httpPost.setEntity(entity);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    httpPost.setHeader("mainzellisteApiVersion", "2.0");
    CloseableHttpResponse response;
    try {
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      String reasonPhrase = response.getStatusLine().getReasonPhrase();
      insertEventLog(statusCode);
      checkStatusCode(response, statusCode, reasonPhrase);
      String stringToken = EntityUtils.toString(response.getEntity());
      JsonObject jsonObject = JsonParser.parseString(stringToken).getAsJsonObject();
      return jsonObject.get("id").getAsString();
    } catch (IOException e) {
      logger.error("Get read token from Mainzelliste: IOException: e: " + e);
      throw new IOException(e);
    }
  }

  private String createJsonObjectForReadPatients(List<String> ctsIds) {
    ReadPatientsToken readPatientsToken = new ReadPatientsToken();
    TokenData tokenData = new TokenData();
    for (String id : ctsIds) {
      ID idReadPatient = new ID();
      idReadPatient.setIdType("ctsid");
      idReadPatient.setIdString(new String(Base64.getUrlEncoder().encode(Hex.decode(id))));
      tokenData.getSearchIds().add(idReadPatient);
    }
    tokenData.getResultIds()
        .add(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_SEARCH_ID_TYPE));
    readPatientsToken.setData(tokenData);
    readPatientsToken.setType("readPatients");
    return new Gson().toJson(readPatientsToken);
  }

  private void addEncryptedIdToPatient(JsonObject patientAsJson,
      String encryptedIdString) {
    JsonObject encryptedId = JsonParser.parseString(encryptedIdString).getAsJsonObject();
    patientAsJson.addProperty("patid", encryptedId.get("EncID").getAsString());
  }

  private HttpPost createHttpPost(String path) {
    HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_URL)
            + path));
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    httpPost.setHeader(HEADER_PARAM_API_KEY,
        ConfigurationUtil
            .getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_API_KEY));
    return httpPost;
  }

  private void insertEventLog(int statusCode) {
    String ctsUser = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.CTS_USERNAME);
    AuditEvent auditEvent = EventLogMainzellisteUtil.createAuditEvent(ctsUser, statusCode);
    EventLogUtil.insertEventLog(createAuditEventLog(auditEvent));
  }

  private void checkStatusCode(CloseableHttpResponse response, int statusCode, String reasonPhrase)
      throws IOException, NotAuthorizedException, ConflictException {
    if (statusCode >= 500 && statusCode < 600) {
      String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      String message = getMessage("Mainzelliste server not responding", statusCode,
          reasonPhrase, bodyResponse);
      logger.error(message);
      throw new IOException(message);
    }
    if (statusCode == 401) {
      String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      logger.error(getMessage("Mainzelliste credentials not correct", statusCode,
          reasonPhrase, bodyResponse));
      throw new NotAuthorizedException(
          getMessage("Mainzelliste credentials not correct", statusCode, reasonPhrase,
              bodyResponse));
    }
    if (statusCode == 404) {
      String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      logger.error(
          getMessage("Mainzelliste Url not found", statusCode, reasonPhrase,
              bodyResponse));
      throw new NotFoundException(
          getMessage("Mainzelliste Url not found", statusCode, reasonPhrase,
              bodyResponse));
    }
    if (statusCode >= 400 && statusCode < 500) {
      String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      logger.error(
          getMessage("Invalid patient data posted to Mainzelliste", statusCode,
              reasonPhrase, bodyResponse));
      throw new ConflictException(
          getMessage("Invalid patient data posted to Mainzelliste", statusCode,
              reasonPhrase, bodyResponse));
    }
  }

  /**
   * print the message from the extern service.
   *
   * @param message      message.
   * @param statusCode   statusCode.
   * @param reasonPhrase reasonPhrase.
   * @param bodyResponse bodyResponse.
   * @return message from the extern service.
   */
  private String getMessage(String message, int statusCode, String reasonPhrase,
      String bodyResponse) {
    return message + "; statusCode: " + statusCode + "; reason: " + reasonPhrase + ";body: "
        + bodyResponse;
  }

  /**
   * close a response.
   *
   * @param response CloseableHttpResponse
   */
  public void closeResponse(CloseableHttpResponse response) {
    if (response != null) {
      try {
        EntityUtils.consumeQuietly(response.getEntity());
        response.close();
      } catch (IOException e) {
        logger.error("Get Pseudonym from Mainzelliste: Exception when closing response", e);
      }
    }
  }

  /**
   * return a standard error message.
   *
   * @param attributeName attributeName
   * @return error message
   */
  String getMissigErrorMessage(String attributeName) {
    return "Mandatory attribute is missing: " + attributeName;
  }
}
