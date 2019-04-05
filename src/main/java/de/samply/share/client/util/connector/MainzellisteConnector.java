package de.samply.share.client.util.connector;


import com.google.gson.JsonObject;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.jooq.tools.json.JSONObject;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

public class MainzellisteConnector {
    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String mainzellisteBaseUrl;
    private HttpHost mainzellisteHost;

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

    public String getPatientPseudonym(Bundle bundle) {
        for (int i = 0; i < bundle.getEntry().size(); i++) {
            Resource resource = bundle.getEntry().get(i).getResource();
            if (resource.fhirType().equals("Patient")) {
                JSONObject jsonObject = createJSONPatient((Patient) resource);
            }
        }
        return "";
    }

    private JSONObject createJSONPatient(Patient patient) {
        return null;
    }

    private JSONObject getPseudonymFromMainzelliste(JSONObject patient) {

        return null;
    }
}
