package de.samply.share.client.util.connector;


import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hl7.fhir.r4.model.*;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainzellisteConnector {
    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String mainzellisteBaseUrl;
    private HttpHost mainzellisteHost;
    private final String GET_ENCRYPTID_URL = "/paths/getEncryptId";

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
            e.printStackTrace();
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
    public Bundle getPatientPseudonym(Bundle bundle) throws IllegalArgumentException, IOException {
        for (int i = 0; i < bundle.getEntry().size(); i++) {
            Resource resource = bundle.getEntry().get(i).getResource();
            if (resource.fhirType().equals("Patient")) {
                JSONObject patient = createJSONPatient((Patient) resource);
                Patient original = (Patient) resource;
                JSONObject encryptedID = getPseudonymFromMainzelliste(patient);
                Patient patientNew = createPseudonymziedPatient(original, encryptedID);
                bundle.getEntry().get(i).setResource(patientNew);
            }
        }
        return bundle;
    }


    /**
     * Create a new patient and add only the necessary attributes for a pseudonymized patient
     *
     * @param orginal
     * @param encryptedID
     * @return the pseudonymized patient
     */
    private Patient createPseudonymziedPatient(Patient orginal, JSONObject encryptedID) {
        Patient patientNew = new Patient();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, orginal.getBirthDateElement().getYear());
        DateType date = new DateType(calendar.getTime(), TemporalPrecisionEnum.YEAR);
        patientNew.setBirthDate(calendar.getTime());
        patientNew.setBirthDateElement(date);
        patientNew.setGender(orginal.getGender());
        List<Identifier> identifierList = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setValue(encryptedID.get("EncID").toString());
        identifierList.add(identifier);
        patientNew.setIdentifier(identifierList);
        patientNew.setDeceased(orginal.getDeceased());
        patientNew.setId(orginal.getId());
        String profile = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PROFILE);
        Meta meta = new Meta();
        meta.addProfile(profile);
        patientNew.setMeta(meta);
        return patientNew;
    }


    /**
     * Extract from the patient only the necessary attributes for the Mainzelliste
     *
     * @param patient
     * @return A patient with the attributes from the original patient
     * @throws NullPointerException
     */
    private JSONObject createJSONPatient(Patient patient) throws NullPointerException {
        JSONObject patientPs = new JSONObject();
        try {
            patientPs.put("vorname", checkIfAttributeExist(patient.getNameFirstRep().getGivenAsSingleString(), "vorname"));
            patientPs.put("nachname", checkIfAttributeExist(patient.getNameFirstRep().getFamily(), "nachname"));
            patientPs.put("geburtsname", ""); //TODO: Resource erweitern
            int birthDay = patient.getBirthDateElement().getDay();
            int birthMonth = patient.getBirthDateElement().getMonth();
            String day = String.valueOf(birthDay);
            String month = String.valueOf(birthMonth);
            if (birthDay < 10) {
                day = String.format("%02d", birthDay);
            }
            if (birthMonth < 10) {
                month = String.format("%02d", birthMonth);
            }
            patientPs.put("geburtstag", checkIfAttributeExist(day, "geburtstag"));
            patientPs.put("geburtsmonat", checkIfAttributeExist(month, "geburtsmonat"));
            patientPs.put("geburtsjahr", checkIfAttributeExist(patient.getBirthDateElement().getYear().toString(), "geburtsjahr"));
            patientPs.put("plz", checkIfAttributeExist(patient.getAddressFirstRep().getPostalCode(), "plz"));
            patientPs.put("ort", checkIfAttributeExist(patient.getAddressFirstRep().getCity(), "ort"));
            patientPs.put("requestedIdType", "ctsid");
        } catch (NullPointerException e) {
            throw new NullPointerException("Error at patient (ID: " + patient.getId() + "). " + e.getMessage());
        }
        return patientPs;
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
        if (attribute == null) {
            throw new NullPointerException("The attribute " + attributeName + " was empty");
        } else {
            return attribute;
        }
    }

    /**
     * Post the original patient to the Mainzelliste and get an encrypted ID
     *
     * @param patient
     * @return an encrypted ID
     * @throws IOException
     */
    private JSONObject getPseudonymFromMainzelliste(JSONObject patient) throws IOException,IllegalArgumentException {
        HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_MAINZELLISTE_URL) + GET_ENCRYPTID_URL));
        HttpEntity entity = new StringEntity(patient.toString(), Consts.UTF_8);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader("apiKey", "nngmTestKey?[8574]");
        httpPost.setEntity(entity);
        CloseableHttpResponse response;
        JSONObject encryptedID = new JSONObject();
        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 500 && statusCode < 600) {
                throw new IOException("Mainzelliste server not responding");
            }
            if (statusCode >= 400 && statusCode < 500) {
                throw new IllegalArgumentException("Invalid patient bundle posted to Mainzelliste");
            }
            String encryptedIDString = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            encryptedID = (JSONObject) parser.parse(encryptedIDString);
            response.close();
        } catch (IOException e) {
            throw new IOException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return encryptedID;
    }
}
