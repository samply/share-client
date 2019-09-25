package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FHIRResource;
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

    public void postPseudonmToCTS(String bundleString) throws Exception {
        Bundle pseudonymBundle = pseudonymiseBundle(bundleString);
        HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL)));
        HttpEntity entity = new StringEntity(pseudonymBundle.toString(), Consts.UTF_8);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.getMimeType());
        httpPost.setEntity(entity);
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()!= 200){
                throw new Exception();
            }

        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private Bundle pseudonymiseBundle(String bundleString) throws IOException {
        FHIRResource fhirResource = new FHIRResource();
        Bundle bundle = fhirResource.convertToBundleResource(bundleString);
        MainzellisteConnector mainzellisteConnector = new MainzellisteConnector();
        return mainzellisteConnector.getPatientPseudonym(bundle);
    }

}
