package de.samply.share.client.util.connector;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import com.mchange.rmi.NotAuthorizedException;
import com.sun.jersey.api.NotFoundException;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.r4.model.*;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainzellisteConnector {
    private static final Logger logger = LogManager.getLogger(MainzellisteConnector.class);
    private static final String FHIR_RESOURCE_PATIENT = "patient";
    private static final String FHIR_RESOURCE_COVERAGE = "coverage";
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
    private static final String CTS_COVERAGE_PROFILE="http://uk-koeln.de/fhir/StructureDefinition/Coverage/nNGM/pseudonymisiert";
    private static final String PATIENT_IDENTIFIER_SYSTEM ="http://uk-koeln.de/fhir/NamingSystem/nNGM/patient-identifier";
    private static final String GET_ENCRYPTID_URL = "/paths/getEncryptId";
    public static final String HEADER_PARAM_API_KEY = "apiKey";

    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String mainzellisteBaseUrl;
    private HttpHost mainzellisteHost;


    public MainzellisteConnector() {
        init();
    }

    private void init() {
        try {
            this.mainzellisteBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_URL));
            httpConnector = ApplicationBean.createHttpConnector();
            this.mainzellisteHost = SamplyShareUtils.getAsHttpHost(mainzellisteBaseUrl);
            httpClient = httpConnector.getHttpClient(mainzellisteHost);
        } catch (MalformedURLException e) {
            logger.error("Init Mainzelliste connection: MalformedURLException: e: " + e);
        }
    }

    /**
     * pseudonymise a patient
     *
     * @param bundle the patient bundle
     * @return the pseudonymized patient
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public Bundle getPatientPseudonym(Bundle bundle) throws IllegalArgumentException, NotFoundException, IOException, NotAuthorizedException {
        Patient patient = null;
        Patient patientPseudonym = null;
        Coverage coverage = null;
        Coverage coveragePseudonym = null;
        JSONObject encryptedID;
        int patientEntryIndex = 0;
        int coverageEntryIndex = 0;

        for (int i = 0; i < bundle.getEntry().size(); i++) {
            Resource resource = bundle.getEntry().get(i).getResource();
            if (resource.fhirType().equalsIgnoreCase(FHIR_RESOURCE_PATIENT)) {
                patient = (Patient) resource;
                patientPseudonym = createPseudonymizedPatient(patient);
                patientEntryIndex = i;
            } else if (resource.fhirType().equalsIgnoreCase(FHIR_RESOURCE_COVERAGE)) {
                coverage = (Coverage) resource;
                coveragePseudonym = pseudonymCoverage(coverage);
                coverageEntryIndex = i;
            }
            if (patient != null && coverage != null) {
                JSONObject jsonIdatObject = createJSONPatient(patient, coverage);
                //Patient original = (Patient) resource;
                encryptedID = getPseudonymFromMainzelliste(jsonIdatObject);
                patientPseudonym = addPseudonymToPatient(patientPseudonym, encryptedID);
                bundle.getEntry().get(patientEntryIndex).setResource(patientPseudonym);
                bundle.getEntry().get(coverageEntryIndex).setResource(coveragePseudonym);
                return bundle;
            }
        }
        checkNonNull(patient, "The required patient resource is empty");
        checkNonNull(coverage, "The required coverage resource is empty");
        return bundle;
    }

    /**
     * pseudonymise the Coverage resource
     *
     * @param originalCoverage
     * @return
     */

    private Coverage pseudonymCoverage(Coverage originalCoverage) {
        Coverage coveragePseudonym=originalCoverage.copy();
        Meta meta = new Meta();
        //@TODO Hard-coded to avoid changing the configuration file and the installation program... but it should be changed in the next versions to follow the default configuration.
        //String pseudonymizedProfile = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PROFILE);
        meta.addProfile(CTS_COVERAGE_PROFILE);
        coveragePseudonym.setMeta(meta);
        coveragePseudonym.getIdentifier().clear();
        return coveragePseudonym;
    }


    /**
     * Create a new patient and add only the necessary attributes for a pseudonymized patient
     *
     * @param originalPatient
     * @return the pseudonymized patient
     */
    private Patient createPseudonymizedPatient(Patient originalPatient) {
        Patient patientNew = new Patient();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, originalPatient.getBirthDateElement().getYear());
        DateType date = new DateType(calendar.getTime(), TemporalPrecisionEnum.YEAR);
        patientNew.setId(originalPatient.getId());
        patientNew.setBirthDate(calendar.getTime());
        patientNew.setBirthDateElement(date);
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
     * add pseudonym to a Patient resouce
     *
     * @param patient
     * @param encryptedID
     * @return the pseudonymized patient
     */
    private Patient addPseudonymToPatient(Patient patient, JSONObject encryptedID) {
        patient.getIdentifierFirstRep().setValue(encryptedID.get(MAINZELLISTE_IDTYPE_ENC_ID).toString());
        return patient;
    }


    /**
     * Extract from the patient only the necessary attributes for the Mainzelliste
     *
     * @param patient
     * @return A patient with the attributes from the original patient
     * @throws NullPointerException
     */
    private JSONObject createJSONPatient(Patient patient, Coverage coverage) throws NullPointerException {
        JSONObject jsonIdatObject = new JSONObject();
        try {
            jsonIdatObject.put(IDAT_VORNAME, checkIfAttributeExist(patient.getNameFirstRep().getGivenAsSingleString(), IDAT_VORNAME));
            jsonIdatObject.put(IDAT_NACHNAME, checkIfAttributeExist(patient.getNameFirstRep().getFamily(), IDAT_NACHNAME));
            DateType birthDateElement = patient.getBirthDateElement();
            checkIfAttributeExist(birthDateElement.asStringValue(), IDAT_GEBURTSDATUM);
            int birthDay = birthDateElement.getDay();
            int birthMonth = birthDateElement.getMonth();
            birthMonth += 1;// +1 because Hapi returns the month with 0-index, e.g. 0=January
            String day = String.valueOf(birthDay);
            String month = String.valueOf(birthMonth);
            if (birthDay < 10) {
                day = String.format("%02d", birthDay);
            }
            if (birthMonth < 10) {
                month = String.format("%02d", birthMonth);
            }
            jsonIdatObject.put(IDAT_GEBURTSTAG, checkIfAttributeExist(day, IDAT_GEBURTSTAG));
            jsonIdatObject.put(IDAT_GEBURTSMONAT, checkIfAttributeExist(month, IDAT_GEBURTSMONAT));
            jsonIdatObject.put(IDAT_GEBURTSJAHR, checkIfAttributeExist(birthDateElement.getYear().toString(), IDAT_GEBURTSJAHR));
            if (!patient.getAddressFirstRep().isEmpty()) {
                if (patient.getAddressFirstRep().hasCity())
                    jsonIdatObject.put(IDAT_ADRESSE_STADT, patient.getAddressFirstRep().getCity());
                if (patient.getAddressFirstRep().hasPostalCode())
                    jsonIdatObject.put(IDAT_ADRESSE_PLZ, patient.getAddressFirstRep().getPostalCode());
                if (patient.getAddressFirstRep().hasLine())
                    jsonIdatObject.put(IDAT_ADRESSE_STRASSE, patient.getAddressFirstRep().getLine().get(0).getValue());
            }
            jsonIdatObject.put(IDAT_REQUESTED_ID_TYPE, IDAT_CTSID);
            if(coverage!=null && coverage.hasIdentifier()){
                jsonIdatObject.put(IDAT_VERSICHERUNGSNUMMER, checkIfAttributeExist(coverage.getIdentifierFirstRep().getValue(), IDAT_VERSICHERUNGSNUMMER));
            }
        } catch (NullPointerException e) {
            logger.error("Error at identifying patient data"  + e.getMessage());
            throw new NullPointerException("Error at identifying patient data"  + e.getMessage());
        }
        logger.debug("jsonIdatObject for Mainzelliste: "+jsonIdatObject.toString());
        return jsonIdatObject;
    }

    /**
     * Check if an attribute is empty
     *
     * @param attribute
     * @param attributeName
     * @return the attribute if its not empty
     * @throws NullPointerException
     */
    private String checkIfAttributeExist(String attribute, String attributeName) throws NullPointerException {
        if (attribute == null || attribute.isEmpty()) {
            logger.error("The mandatory attribute " + attributeName + " was empty");
            throw new NullPointerException("The mandatory attribute " + attributeName + " was empty");
        } else {
            return attribute;
        }
    }

    /**
     * Check if a resource is empty
     *
     * @param object
     * @param message
     */
    private void checkNonNull(Object object, String message){
        Objects.requireNonNull(object, message);
    }
    /**
     * Post the original patient to the Mainzelliste and get an encrypted ID
     *
     * @param patient
     * @return an encrypted ID
     * @throws IOException
     */
    private JSONObject getPseudonymFromMainzelliste(JSONObject patient) throws IOException, IllegalArgumentException, NotFoundException, NotAuthorizedException {
        HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_URL) + GET_ENCRYPTID_URL));
        HttpEntity entity = new StringEntity(patient.toString(), Consts.UTF_8);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HEADER_PARAM_API_KEY, ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_API_KEY));
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        JSONObject encryptedID = new JSONObject();
        try {
            response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            String reasonPhrase = statusLine.getReasonPhrase();

            if (statusCode >= 500 && statusCode < 600) {
                String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
                String message = getMessage("Mainzelliste server not responding", statusCode, reasonPhrase, bodyResponse);
                logger.error(message);
                throw new IOException(message);
            }
            if (statusCode == 401) {
                String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
                logger.error(getMessage("Mainzelliste permission problem", statusCode, reasonPhrase, bodyResponse));
                throw new NotAuthorizedException(getMessage("Mainzelliste permission problem", statusCode, reasonPhrase, bodyResponse));
            }
            if (statusCode == 404) {
                String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
                logger.error(getMessage("Mainzelliste Url not found", statusCode, reasonPhrase, bodyResponse));
                throw new NotFoundException(getMessage("Mainzelliste Url not found", statusCode, reasonPhrase, bodyResponse));
            }
            if (statusCode >= 400 && statusCode < 500) {
                String bodyResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
                logger.error(getMessage("Invalid patient bundle posted to Mainzelliste", statusCode, reasonPhrase, bodyResponse));
                throw new IllegalArgumentException(getMessage("Invalid patient bundle posted to Mainzelliste", statusCode, reasonPhrase, bodyResponse));
            }
            String encryptedIDString = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            encryptedID = (JSONObject) parser.parse(encryptedIDString);
        } catch (IOException e) {
            logger.error("Get Pseudonym from Mainzelliste: IOException: e: " + e);
            throw new IOException(e);
        } catch (ParseException e) {
            logger.error("Get Pseudonym from Mainzelliste: ParseException: e: " + e);
            e.printStackTrace();
        } finally {
            closeResponse(response);
        }
        return encryptedID;
    }

    /**
     * print the message from the extern service
     * @param message
     * @param statusCode
     * @param reasonPhrase
     * @param bodyResponse
     * @return
     */
    private String getMessage(String message, int statusCode, String reasonPhrase, String bodyResponse) {
        return message + "; statusCode: " + statusCode +"; reason: "+ reasonPhrase+ ";body: "+ bodyResponse;
    }

    /**
     * close a response
     * @param response
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
}