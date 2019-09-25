package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FHIRResource;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hl7.fhir.dstu3.model.Bundle;

import java.io.IOException;
import java.net.MalformedURLException;

public class CTSConnector {
    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String ctsBaseUrl;
    private HttpHost ctsHost;


    public CTSConnector() {
        init();
    }

    private void init() {
        try {
            this.ctsBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL));
            httpConnector = ApplicationBean.createHttpConnector();
            this.ctsHost = SamplyShareUtils.getAsHttpHost(ctsBaseUrl);
            httpClient = httpConnector.getHttpClient(ctsHost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String postPseudonmToCTS(String bundleString) throws NullPointerException, IOException {
        FHIRResource fhirResource = new FHIRResource();
        Bundle bundle = fhirResource.convertToBundleResource(bundleString);
        MainzellisteConnector mainzellisteConnector = new MainzellisteConnector();
        String pseudonym = mainzellisteConnector.getPatientPseudonym(bundle);
        return bundleString;
    }

    public void postBundleToCTS(Bundle bundle){
        CTSConnector connector= new CTSConnector();
        connector.postBundleToCTS(bundle);
    }
}
