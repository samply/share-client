package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.common.ldmclient.LdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorRuntimeException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Result;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;

public abstract class AbstractLdmConnector<
        T_LDM_CLIENT extends LdmClient<T_RESULT, T_RESULT_STATISTICS, T_ERROR, T_SPECIFIC_VIEW>,
        T_RESULT extends Result & Serializable,
        T_RESULT_STATISTICS extends Serializable,
        T_ERROR extends Serializable,
        T_SPECIFIC_VIEW extends Serializable,
        T_PATIENT> implements LdmConnector<T_RESULT> {

    protected transient HttpConnector httpConnector;
    protected T_LDM_CLIENT ldmClient;
    protected CloseableHttpClient httpClient;
    protected String baseUrl;
    protected HttpHost host;

    protected void init(boolean useCaching) {
        initBasic();

        try {
            this.ldmClient = createLdmClient(httpClient, baseUrl, useCaching);
        } catch (LdmClientException e) {
            throw new LdmConnectorRuntimeException(e);
        }
    }


    protected void init(boolean useCaching, int maxCacheSize) throws LdmConnectorRuntimeException {
        initBasic();

        try {
            this.ldmClient = createLdmClient(httpClient, baseUrl, useCaching, maxCacheSize);
        } catch (LdmClientException e) {
            throw new LdmConnectorRuntimeException(e);
        }
    }

    protected abstract T_LDM_CLIENT createLdmClient(
            CloseableHttpClient httpClient, String baseUrl, boolean useCaching) throws LdmClientException;

    protected abstract T_LDM_CLIENT createLdmClient(
            CloseableHttpClient httpClient, String baseUrl, boolean useCaching, int maxCacheSize) throws LdmClientException;


    private void initBasic() throws LdmConnectorRuntimeException {
        this.baseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
        httpConnector = ApplicationBean.getHttpConnector();
        if (isLdmSamplystoreBiobank()) {
            httpConnector.addCustomHeader("Authorization", "Basic " + StoreConnector.getBase64Credentials(StoreConnector.authorizedUsername, StoreConnector.authorizedPassword));
        }
        httpClient = httpConnector.getHttpClient(host);
        try {
            this.host = SamplyShareUtils.getAsHttpHost(baseUrl);
        } catch (MalformedURLException e) {
            throw new LdmConnectorRuntimeException(e);
        }
    }

    protected abstract void marshalQueryResult(T_RESULT queryResult, File xmlFile, Marshaller marshaller) throws JAXBException;

    protected abstract String extractQueryResultId(T_RESULT queryResult);

    protected void handleLdmClientException(LdmClientException e) throws LDMConnectorException {
        if (isLdmCentraxx()) {
            throw new LDMConnectorException(e);
        } else if (isLdmSamplystoreBiobank()) {
            e.printStackTrace();
        }
    }
}
