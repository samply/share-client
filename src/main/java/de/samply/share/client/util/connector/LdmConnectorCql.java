package de.samply.share.client.util.connector;

import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.cql.LdmClientCql;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.model.common.*;
import de.samply.share.model.common.Error;
import de.samply.share.model.cql.CqlResult;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.List;

public class LdmConnectorCql extends AbstractLdmConnector<LdmClientCql, String, CqlResult, QueryResultStatistic, Error> {

    private static final Logger logger = LogManager.getLogger(LdmConnectorCql.class);

    LdmConnectorCql(boolean useCaching) {
        super(useCaching);
    }

    @Override
    boolean useAuthorizationForLdm() {
        return false;
    }

    @Override
    LdmClientCql createLdmClient(CloseableHttpClient httpClient, String baseUrl, boolean useCaching) throws LdmClientException {
        return new LdmClientCql(httpClient,baseUrl);
    }

    @Override
    LdmClientCql createLdmClient(CloseableHttpClient httpClient, String baseUrl, boolean useCaching, int maxCacheSize) throws LdmClientException {
        return createLdmClient(httpClient,baseUrl,useCaching);
    }

    @Override
    void marshalQueryResult(CqlResult queryResult, File xmlFile, Marshaller marshaller) throws JAXBException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(queryResult, xmlFile);
    }

    @Override
    String extractQueryResultId(CqlResult queryResult) {
        return Integer.toString(queryResult.getQueryId());
    }

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public String postQuery(String query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException {
        return null;
    }

    @Override
    public CqlResult getResultsFromPage(String location, int page) throws LDMConnectorException {
        return null;
    }

    @Override
    public boolean isFirstResultPageAvailable(String location) throws LDMConnectorException {
        return false;
    }

    @Override
    public boolean isResultDone(String location, QueryResultStatistic qrs) throws LDMConnectorException {
        return false;
    }

    @Override
    public int getPatientCount(boolean dktkFlagged) throws LDMConnectorException, InterruptedException {
        return 0;
    }

    @Override
    public ReferenceQueryCheckResult getReferenceQueryCheckResult(String referenceQuery) throws LDMConnectorException {
        return null;
    }
}
