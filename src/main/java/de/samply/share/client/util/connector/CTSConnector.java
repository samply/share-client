package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.parser.DataFormatException;
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
import org.hl7.fhir.r4.model.Bundle;

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

    public void postPseudonmToCTS(String bundleString, String mediaType) throws IOException, ConfigurationException, DataFormatException, IllegalArgumentException {
            String pseudonymBundle = pseudonymiseBundle(bundleString, mediaType);
            HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL)));
            HttpEntity entity = new StringEntity(pseudonymBundle, Consts.UTF_8);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/fhir+xml");
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/fhir+xml");
            httpPost.setEntity(entity);
            CloseableHttpResponse response;
            try {
                response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 500 && statusCode < 600) {
                    throw new IOException("CTS server not responding");
                }
                if (statusCode >= 400 && statusCode < 500) {
                    throw new IllegalArgumentException(response.toString());
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
    }

    private String pseudonymiseBundle(String bundleString, String mediaType) throws IOException, ConfigurationException, DataFormatException {
        FHIRResource fhirResource = new FHIRResource();
        Bundle bundle = fhirResource.convertToBundleResource(bundleString, mediaType);
        MainzellisteConnector mainzellisteConnector = new MainzellisteConnector();
        return fhirResource.convertBundleToXml(mainzellisteConnector.getPatientPseudonym(bundle));
    }

}
