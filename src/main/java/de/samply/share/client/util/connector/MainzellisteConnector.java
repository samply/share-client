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
import de.samply.share.client.util.connector.exception.XmlPareException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.EventLogMainzellisteUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.xml.CtsPatient;
import de.samply.share.client.util.xml.XmlUtils;
import de.samply.share.common.utils.SamplyShareUtils;
import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
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
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Mainzelliste Connector.
 */
public class MainzellisteConnector {

  private static final Logger logger = LoggerFactory.getLogger(MainzellisteConnector.class);

  private static final String MAINZELLISTE_IDTYPE_ENC_ID = "EncID";
  private static final String IDAT_VORNAME = "vorname";
  private static final String IDAT_NACHNAME = "nachname";
  private static final String IDAT_GEBURTSNAME = "geburtsname";
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
      "http://uk-koeln.de/fhir/sid/nNGM/patient-pseudonym";
  private static final String GET_ENCRYPT_ID_URL = "/paths/getEncryptId";
  private static final String GET_ENCRYPT_ID_WITH_PATIENT_ID_URL = "/paths/getEncryptIdWithId";
  private static final String HEADER_PARAM_API_KEY = "apiKey";
  private static final String SEARCH_ID_TYPE = "searchIdType";
  private static final String SEARCH_ID_STRING = "searchIdString";
  private static final String PAT_ID = "patid";
  private static final String MAINZELLISTE_API_KEY = "mainzellisteApiKey";
  private static final String MAINZELLISTE_API_VERSION = "mainzellisteApiVersion";
  private static final String MAINZELLISTE_VERSION_VALUE = "2.0";


  private final CloseableHttpClient httpClient;
  private final FhirUtil fhirUtil;
  private final XmlUtils xmlUtils;


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
    xmlUtils = new XmlUtils();
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
   * @throws IOException                    IOException
   * @throws NotFoundException              Not Found Exception
   * @throws NotAuthorizedException         Not Authorized Exception
   * @throws MainzellisteConnectorException MainzellisteConnectorException
   */

  public Bundle getPatientFhirPseudonym(Bundle bundle)
      throws NotFoundException, NotAuthorizedException, IOException,
      MainzellisteConnectorException {
    Patient patient = null;
    Patient patientPseudonym = null;
    Coverage coverage = null;
    Coverage coveragePseudonym = null;
    JsonObject encryptedId;
    int patientEntryIndex = 0;
    int coverageEntryIndex = 0;
    try {
      for (int i = 0; i < bundle.getEntry().size(); i++) {
        Resource resource = bundle.getEntry().get(i).getResource();
        if (resource.fhirType().equalsIgnoreCase(FhirUtil.FHIR_RESOURCE_PATIENT)) {
          patient = (Patient) resource;
          patientPseudonym = createPseudonymizedPatient(patient);
          patientEntryIndex = i;
        } else if (resource.fhirType().equalsIgnoreCase(FhirUtil.FHIR_RESOURCE_COVERAGE)) {
          coverage = (Coverage) resource;
          coveragePseudonym = pseudonymCoverage(coverage);
          coverageEntryIndex = i;
        }
        if (patient != null && coverage != null) {
          JsonObject jsonIdatObject = createJsonPatient(patient, coverage);
          encryptedId = requestPseudonymFromMainzelliste(jsonIdatObject);
          patientPseudonym = addPseudonymToPatient(patientPseudonym, encryptedId);
          bundle.getEntry().get(patientEntryIndex).setResource(patientPseudonym);
          bundle.getEntry().get(coverageEntryIndex).setResource(coveragePseudonym);
          return bundle;
        }
      }
      checkNonNull(patient, "A required resource is empty: " + FhirUtil.FHIR_RESOURCE_PATIENT);
      checkNonNull(coverage, "A required resource is empty: " + FhirUtil.FHIR_RESOURCE_COVERAGE);
    } catch (ConflictException | MandatoryAttributeException | IllegalArgumentException e) {
      throw new MainzellisteConnectorException(e);
    }
    return bundle;
  }


  /**
   * prepare xml transaction to be sent to the edc system.
   *
   * @param xmlDoc xml tree.
   * @return xmlDoc
   * @throws NotFoundException              Not Found Exception
   * @throws NotAuthorizedException         Not Authorized Exception
   * @throws IOException                    IO Exception
   * @throws MainzellisteConnectorException MainzellisteConnectorException
   */
  public Document getPatientXmlPseudonym(Document xmlDoc)
      throws NotFoundException, NotAuthorizedException, IOException,
      MainzellisteConnectorException {
    JsonObject encryptedId;
    try {
      Node identifyingDataNode =
          xmlDoc.getElementsByTagName(XmlUtils.IDENTIFYING_DATA_ELEMENT).item(0);
      checkNonNull(identifyingDataNode, "A required element is empty: "
          + XmlUtils.IDENTIFYING_DATA_ELEMENT);

      Element identifyingDataElement = (Element) identifyingDataNode;
      Node nodePatient = identifyingDataElement.getElementsByTagName(
          XmlUtils.PATIENT_ELEMENT).item(0);
      checkNonNull(nodePatient,
          "A required element is empty: " + XmlUtils.PATIENT_ELEMENT);
      CtsPatient patient = getCtsPatientFromXml(xmlDoc);

      Node medicalDataNode = xmlDoc.getElementsByTagName(XmlUtils.MEDICAL_DATA_ELEMENT).item(0);
      checkNonNull(medicalDataNode, "A required element is empty: "
          + XmlUtils.MEDICAL_DATA_ELEMENT);
      Node nodeMedicalDataPatient = null;
      Element medicalDataNodeElement = (Element) medicalDataNode;
      Node nodeBirthdate =
          medicalDataNodeElement.getElementsByTagName(XmlUtils.BIRTHDATE_ELEMENT).item(0);
      checkNonNull(nodeBirthdate, "A required element is empty: " + XmlUtils.BIRTHDATE_ELEMENT);
      nodeBirthdate.setTextContent(patient.getGeburtsjahr() + "-" + "01" + "-" + "01");
      nodeBirthdate.setTextContent(concatenateDate(String.valueOf(
          patient.getGeburtsjahr()), "01", "01"));
      JsonObject jsonIdatObject = createJsonPatient(patient);
      encryptedId = requestPseudonymFromMainzelliste(jsonIdatObject);
      identifyingDataNode.removeChild(nodePatient);
      Element tokenNode = xmlDoc.createElement(XmlUtils.TOKEN_ELEMENT);
      tokenNode.setTextContent(encryptedId.get(MAINZELLISTE_IDTYPE_ENC_ID).getAsString());
      identifyingDataNode.appendChild(tokenNode);
    } catch (ConflictException | MandatoryAttributeException
        | IllegalArgumentException | XmlPareException e) {
      throw new MainzellisteConnectorException(e);
    }
    return xmlDoc;
  }

  private String concatenateDate(String year, String month, String day) {
    return year + "-" + month + "-" + day;
  }


  /**
   * Get CtsPatient object from XML content.
   *
   * @param xmlDoc xml document
   * @return CtsPatient
   * @throws XmlPareException Xml Pare Exception
   */
  private CtsPatient getCtsPatientFromXml(Document xmlDoc) throws XmlPareException {
    CtsPatient patient = new CtsPatient();
    final String nachnameElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.NACHNAME_ELEMENT, XmlUtils.EMPTY_NULL_CHECK);
    final String vornameElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.VORNAME_ELEMENT, XmlUtils.EMPTY_NULL_CHECK);
    final String geburtsnameElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.GEBURTSNAME_ELEMENT, XmlUtils.NO_EMPTY_NULL_CHECK);
    final String geburtstagElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.GEBURTSTAG_ELEMENT, XmlUtils.EMPTY_NULL_CHECK);
    final String geburtsmonatElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.GEBURTSMONAT_ELEMENT, XmlUtils.EMPTY_NULL_CHECK);
    final String geburtsjahrElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.GEBURTSJAHR_ELEMENT, XmlUtils.EMPTY_NULL_CHECK);
    final String versicherungsnummerElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.VERSICHERUNGSNUMMER_ELEMENT, XmlUtils.EMPTY_NULL_CHECK);
    final String adresseplzElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.ADRESSEPLZ_ELEMENT, XmlUtils.NO_EMPTY_NULL_CHECK);
    final String adressestadtElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.ADRESSESTADT_ELEMENT, XmlUtils.NO_EMPTY_NULL_CHECK);
    final String adressestrasseElement = xmlUtils.readValueFromXml(xmlDoc,
        XmlUtils.ADRESSESTRASSE_ELEMENT, XmlUtils.NO_EMPTY_NULL_CHECK);

    patient.setNachname(nachnameElement);
    patient.setVorname(vornameElement);
    patient.setGeburtsname(geburtsnameElement);
    if (isValidDate(concatenateDate(geburtsjahrElement,
        geburtsmonatElement, geburtstagElement), getDateTimeFormatter())) {
      patient.setGeburtstag(Integer.valueOf(geburtstagElement));
      patient.setGeburtsmonat(Integer.valueOf(geburtsmonatElement));
      patient.setGeburtsjahr(Integer.valueOf(geburtsjahrElement));
    } else {
      throw new XmlPareException("The birth date imported is invalid");
    }
    patient.setAdresseStadt(adressestadtElement);
    patient.setAdresseStrasse(adressestrasseElement);
    patient.setAdressePlz(adresseplzElement);
    patient.setVersicherungsnummer(versicherungsnummerElement);
    return patient;
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
    meta.addProfile(CTS_COVERAGE_PROFILE);
    coveragePseudonym.setMeta(meta);
    coveragePseudonym.getIdentifier().clear();
    coveragePseudonym.getContract().clear();
    coveragePseudonym.getCostToBeneficiary().clear();
    coveragePseudonym.setDependent(null);
    coveragePseudonym.setSubrogationElement(null);
    return coveragePseudonym;
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
    if (!originalPatient.getBirthDateElement().isEmpty()) {
      calendar.set(Calendar.YEAR, originalPatient.getBirthDateElement().getYear());
      DateType date = new DateType(calendar.getTime(), TemporalPrecisionEnum.YEAR);
      patientNew.setBirthDate(calendar.getTime());
      patientNew.setBirthDateElement(date);
    }
    patientNew.setId(originalPatient.getId());
    patientNew.setGenderElement(originalPatient.getGenderElement());
    patientNew.setDeceased(originalPatient.getDeceased());
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
   * Extract the necessary attributes for the Mainzelliste from the patient.
   *
   * @param patient  the patient FHIR-resource
   * @param coverage the coverage FHIR-resource
   * @return A patient with the attributes from the original patient
   * @throws MandatoryAttributeException if mandatory attributes from patient or coverage are
   *                                     missing
   */
  private JsonObject createJsonPatient(Patient patient, Coverage coverage)
      throws MandatoryAttributeException {
    Objects.requireNonNull(patient);
    Objects.requireNonNull(coverage);
    JsonObject jsonIdatObject = new JsonObject();
    String nachname = "";
    String vorname = "";
    String geburtsname = "";

    try {
      if (patient.getName().size() == 1) {
        vorname = checkIfAttributeExist(patient.getNameFirstRep().getGivenAsSingleString(),
            IDAT_VORNAME);
        nachname = checkIfAttributeExist(patient.getNameFirstRep().getFamily(), IDAT_NACHNAME);
      } else {
        checkIfListEmpty(patient.getName(), IDAT_NACHNAME);
        HumanName officialHumanName = getPatientName(patient.getName(), HumanName.NameUse.OFFICIAL);
        checkNonNull(officialHumanName, getMissingErrorMessage("official patient name"));
        nachname = checkIfAttributeExist(officialHumanName.getFamily(), IDAT_NACHNAME);
        vorname = checkIfAttributeExist(officialHumanName.getGivenAsSingleString(), IDAT_VORNAME);
        HumanName maidenHumanName = getPatientName(patient.getName(), HumanName.NameUse.MAIDEN);
        if (maidenHumanName != null && maidenHumanName.getFamily() != null) {
          geburtsname = maidenHumanName.getFamily();
        }
      }
      jsonIdatObject.addProperty(IDAT_VORNAME, vorname);
      jsonIdatObject.addProperty(IDAT_NACHNAME, nachname);
      jsonIdatObject.addProperty(IDAT_GEBURTSNAME, geburtsname);
      DateType birthDateElement = patient.getBirthDateElement();
      if (!birthDateElement.getPrecision().equals(TemporalPrecisionEnum.DAY)) {
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
   * Extract the necessary attributes for the Mainzelliste from the patient.
   *
   * @param patient the patient object
   * @return A patient with the attributes from the original patient
   * @throws MandatoryAttributeException if mandatory attributes from patient or coverage are
   *                                     missing
   */
  private JsonObject createJsonPatient(CtsPatient patient)
      throws MandatoryAttributeException {
    Objects.requireNonNull(patient);
    JsonObject jsonIdatObject = new JsonObject();

    try {
      String vornameElement = checkIfAttributeExist(patient.getVorname(),
          XmlUtils.VORNAME_ELEMENT);
      jsonIdatObject.addProperty(IDAT_VORNAME, vornameElement);
      String nachnameElement = checkIfAttributeExist(patient.getNachname(),
          XmlUtils.NACHNAME_ELEMENT);
      jsonIdatObject.addProperty(IDAT_NACHNAME, nachnameElement);
      String geburtsnameElement = patient.getGeburtsname();
      jsonIdatObject.addProperty(IDAT_GEBURTSNAME, geburtsnameElement);
      String geburtstagElement = checkIfAttributeExist(patient.getGeburtstag().toString(),
          XmlUtils.GEBURTSTAG_ELEMENT);
      jsonIdatObject.addProperty(IDAT_GEBURTSTAG, geburtstagElement);
      String geburtsmonatElement = checkIfAttributeExist(patient.getGeburtsmonat().toString(),
          XmlUtils.GEBURTSMONAT_ELEMENT);
      jsonIdatObject.addProperty(IDAT_GEBURTSMONAT, geburtsmonatElement);
      String geburtsjahrElement = checkIfAttributeExist(patient.getGeburtsjahr().toString(),
          XmlUtils.GEBURTSJAHR_ELEMENT);
      jsonIdatObject.addProperty(IDAT_GEBURTSJAHR, geburtsjahrElement);

      if (patient.hasAdresseStadt()) {
        jsonIdatObject.addProperty(IDAT_ADRESSE_STADT, patient.getAdresseStadt());
      }
      if (patient.hasAdressePlz()) {
        jsonIdatObject.addProperty(IDAT_ADRESSE_PLZ, patient.getAdressePlz());
      }
      if (patient.hasAdresseStrasse()) {
        jsonIdatObject.addProperty(IDAT_ADRESSE_STRASSE, patient.getAdresseStrasse());
      }
      jsonIdatObject.addProperty(IDAT_REQUESTED_ID_TYPE, IDAT_CTSID);
      String versicherungsnummerElement = checkIfAttributeExist(patient.getVersicherungsnummer(),
          XmlUtils.VERSICHERUNGSNUMMER_ELEMENT);
      jsonIdatObject.addProperty(IDAT_VERSICHERUNGSNUMMER, versicherungsnummerElement);
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
   * @return the attribute if it's not empty
   * @throws MandatoryAttributeException if attribute is empty
   */
  private String checkIfAttributeExist(String attribute, String attributeName)
      throws MandatoryAttributeException {
    if (attribute == null || attribute.isEmpty()) {
      logger.error(getMissingErrorMessage(attributeName));
      throw new MandatoryAttributeException(getMissingErrorMessage(attributeName));
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
      logger.error(message);
      throw new MandatoryAttributeException(message);
    }
  }

  /**
   * Check if an attribute is empty.
   *
   * @param list     list to be checked
   * @param listName name of the list to be checked
   * @throws MandatoryAttributeException if the list is empty
   */
  private <T> void checkIfListEmpty(List<T> list, String listName)
      throws MandatoryAttributeException {
    if (list == null || list.isEmpty()) {
      logger.error(getMissingErrorMessage(listName));
      throw new MandatoryAttributeException(getMissingErrorMessage(listName));
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
    jsonEntity.addProperty(SEARCH_ID_TYPE,
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_SEARCH_ID_TYPE));
    jsonEntity.addProperty(SEARCH_ID_STRING, patientAsJson.get(PAT_ID).getAsString());
    jsonEntity.addProperty(IDAT_REQUESTED_ID_TYPE, IDAT_CTSID);
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
    jsonEntity.addProperty(SEARCH_ID_TYPE,
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_SEARCH_ID_TYPE));
    jsonEntity.addProperty(SEARCH_ID_STRING, id);
    jsonEntity.addProperty(IDAT_REQUESTED_ID_TYPE, IDAT_CTSID);
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
      return encryptedId.get(MAINZELLISTE_IDTYPE_ENC_ID).getAsString();
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
    httpPost.setHeader(MAINZELLISTE_API_KEY,
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
    httpPost.setHeader(MAINZELLISTE_API_KEY,
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PATIENT_LIST_API_KEY));
    HttpEntity entity = new StringEntity(createJsonObjectForReadPatients(ctsIds), Consts.UTF_8);
    httpPost.setEntity(entity);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    httpPost.setHeader(MAINZELLISTE_API_VERSION, MAINZELLISTE_VERSION_VALUE);
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
      idReadPatient.setIdType(IDAT_CTSID);
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
    patientAsJson.addProperty(PAT_ID, encryptedId.get(MAINZELLISTE_IDTYPE_ENC_ID).getAsString());
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
    AuditEvent auditEvent = EventLogMainzellisteUtil.createAuditEvent(statusCode);
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
  String getMissingErrorMessage(String attributeName) {
    return "Mandatory element is missing: " + attributeName;
  }


  /**
   * return a human name.
   *
   * @param nameList human name list
   * @param nameUse  nameUse
   * @return humanName
   */
  HumanName getPatientName(List<HumanName> nameList, HumanName.NameUse nameUse) {
    for (HumanName humanName : nameList) {
      if (humanName.hasUse() && humanName.getUse() == nameUse) {
        return humanName;
      }
    }
    return null;
  }

  /**
   * validate a date.
   *
   * @param date       date as String
   * @param dateFormatter Date Time Formatter
   * @return boolean
   */

  public boolean isValidDate(String date, DateTimeFormatter dateFormatter) {
    try {
      LocalDate.parse(date, dateFormatter);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }


  /**
   * Check if a date is valid.
   *
   * @return boolean
   */
  private DateTimeFormatter getDateTimeFormatter() {
    return DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US)
        .withResolverStyle(ResolverStyle.STRICT);
  }

}
