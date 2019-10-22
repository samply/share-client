package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.FhirContext;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FHIRResource;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.connector.exception.CTSConnectorException;
import de.samply.share.client.util.connector.exception.MainzellisteConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;

public class CTSConnector {
    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String ctsBaseUrl;
    private HttpHost ctsHost;
    private static final Logger logger = LogManager.getLogger(CTSConnector.class);

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

    public void postPseudonmToCTS(String bundleString) throws MainzellisteConnectorException, CTSConnectorException,Exception {
        Bundle pseudonymBundle = pseudonymiseBundle(bundleString);
        // @TODO just for test
        FhirContext ctx = FhirContext.forDstu3();
        System.out.println("CTSConnector/postPseudonmToCTS(): pseudonymBundle"+ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(pseudonymBundle));

        System.out.println("CTSConnector/postPseudonmToCTS(): CTS-URL:"+ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL));

        HttpPost httpPost = new HttpPost(SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL)));
        HttpEntity entity = new StringEntity(pseudonymBundle.toString(), Consts.UTF_8);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.getMimeType());
        httpPost.setEntity(entity);
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()!= 200){
                throw new CTSConnectorException("Got an error from CTS server: " + response.getStatusLine());
            }
        } catch (IOException e) {
            if(e instanceof HttpHostConnectException || e.getCause() instanceof ConnectException)
                throw new CTSConnectorException("could not connect to CTS server: " + e.getMessage());
            // @TODO just for test
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    private Bundle pseudonymiseBundle(String bundleString) throws MainzellisteConnectorException, IOException {
        FHIRResource fhirResource = new FHIRResource();
        Bundle bundle = fhirResource.convertToBundleResource(bundleString);
        MainzellisteConnector mainzellisteConnector = new MainzellisteConnector();
        return mainzellisteConnector.getPatientPseudonym(bundle);
    }

    private static ResponseHandler<String> getStringResponseHandler() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
    }

}
