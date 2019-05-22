package de.samply.share.client.util.connector;


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
import org.hl7.fhir.dstu3.model.*;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
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
            this.mainzellisteBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MAINZELLISTE_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.mainzellisteHost = SamplyShareUtils.getAsHttpHost(mainzellisteBaseUrl);
            httpClient = httpConnector.getHttpClient(mainzellisteHost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getPatientPseudonym(Bundle bundle) throws IllegalArgumentException, IOException {
        for (int i = 0; i < bundle.getEntry().size(); i++) {
            Resource resource = bundle.getEntry().get(i).getResource();
            if (resource.fhirType().equals("Patient")) {
                JSONObject patient = createJSONPatient((Patient) resource);
                Patient orginal = (Patient) resource;
                JSONObject encryptedID = getPseudonymFromMainzelliste(patient);
                JSONObject encryptedPatient = new JSONObject();
                Patient patient1 = new Patient();
                DateType date = new DateType();
                date.setValueAsString(orginal.getBirthDateElement().getYear()+"-01-01");
                patient1.setBirthDateElement(date);
                patient1.setGender(orginal.getGender());
                patient1.setId((String) encryptedID.get("EncID"));
                patient1.setDeceased(orginal.getDeceased());
                Meta meta = new Meta();
                meta.addProfile(
                        "http://uk-koeln.de/fhir/StructureDefinition/Patient/nNGM/pseudonymisiert/0.1"
                );
                patient1.setMeta(meta);
                System.out.println(patient1.toString());
                bundle.getEntry().get(i).setResource(patient1);
            }
        }
        return "";
    }

    private JSONObject createJSONPatient(Patient patient) throws NullPointerException {
        JSONObject patientPs = new JSONObject();
        try {
            patientPs.put("vorname", checkIfAttributeExist(patient.getNameFirstRep().getGivenAsSingleString(), "vorname"));
            patientPs.put("nachname", checkIfAttributeExist(patient.getNameFirstRep().getFamily(), "nachname"));
            patientPs.put("geburtsname", ""); //TODO: Resource erweitern
            patientPs.put("geburtstag", checkIfAttributeExist(patient.getBirthDateElement().getDay().toString(), "geburtstag"));
            patientPs.put("geburtsmonat", checkIfAttributeExist(patient.getBirthDateElement().getMonth().toString(), "geburtsmonat"));
            patientPs.put("geburtsjahr", checkIfAttributeExist(patient.getBirthDateElement().getYear().toString(), "geburtsjahr"));
            patientPs.put("plz", checkIfAttributeExist(patient.getAddressFirstRep().getPostalCode(), "plz"));
            patientPs.put("ort", checkIfAttributeExist(patient.getAddressFirstRep().getCity(), "ort"));
            patientPs.put("requestedIdType", "ctsid");
        } catch (NullPointerException e) {
            throw new NullPointerException("Error at patient (ID: " + patient.getId() + "). " + e.getMessage());
        }
        return patientPs;
    }

    private String checkIfAttributeExist(String attribute, String attributeName) throws NullPointerException {
        if (attribute == null) {
            throw new NullPointerException("The attribute " + attributeName + " was empty");
        } else {
            return attribute;
        }
    }

    private JSONObject getPseudonymFromMainzelliste(JSONObject patient) throws IOException {
        HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MAINZELLISTE_URL) + GET_ENCRYPTID_URL));
        HttpEntity entity = new StringEntity(patient.toString(), Consts.UTF_8);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader("apiKey", "test1234");
        httpPost.setEntity(entity);
        CloseableHttpResponse response;
        JSONObject encryptedID = new JSONObject();
        try {
            response = httpClient.execute(httpPost);
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
