package de.samply.share.client.util.connector;

import com.google.common.base.Stopwatch;
import de.samply.common.http.HttpConnector;
import de.samply.common.ldmclient.LdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorRuntimeException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.*;
import de.samply.share.utils.QueryConverter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLdmConnector<
        T_LDM_CLIENT extends LdmClient<T_RESULT, T_RESULT_STATISTICS, T_ERROR, T_SPECIFIC_VIEW>,
        T_RESULT extends Result & Serializable,
        T_RESULT_STATISTICS extends Serializable,
        T_ERROR extends Serializable,
        T_SPECIFIC_VIEW extends Serializable> implements LdmConnector<T_RESULT> {

    private T_LDM_CLIENT ldmClient;
    private transient HttpConnector httpConnector;
    CloseableHttpClient httpClient;
    String baseUrl;
    HttpHost host;

    AbstractLdmConnector(boolean useCaching) {
        init(useCaching);
    }

    AbstractLdmConnector(boolean useCaching, int maxCacheSize) {
        init(useCaching, maxCacheSize);
    }

    abstract boolean useAuthorizationForLdm();

    private void init(boolean useCaching) {
        initBasic();

        try {
            this.ldmClient = createLdmClient(httpClient, baseUrl, useCaching);
        } catch (LdmClientException e) {
            throw new LdmConnectorRuntimeException(e);
        }
    }


    private void init(boolean useCaching, int maxCacheSize) throws LdmConnectorRuntimeException {
        initBasic();

        try {
            this.ldmClient = createLdmClient(httpClient, baseUrl, useCaching, maxCacheSize);
        } catch (LdmClientException e) {
            throw new LdmConnectorRuntimeException(e);
        }
    }

    private void initBasic() throws LdmConnectorRuntimeException {
        this.baseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));

        if (useAuthorizationForLdm()) {
            this.httpConnector = ApplicationBean.createHttpConnector(TargetType.TT_LDM);
        } else {
            this.httpConnector = ApplicationBean.createHttpConnector();
        }

        try {
            this.host = SamplyShareUtils.getAsHttpHost(baseUrl);
        } catch (MalformedURLException e) {
            throw new LdmConnectorRuntimeException(e);
        }
        this.httpClient = httpConnector.getHttpClient(host);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postQuery(Query query,
                            List<String> removeKeysFromView,
                            boolean completeMdsViewFields,
                            boolean statisticsOnly,
                            boolean includeAdditionalViewfields) throws LDMConnectorException {
        View view = createView(query, removeKeysFromView, completeMdsViewFields, includeAdditionalViewfields);

        try {
            return ldmClient.postView(view, statisticsOnly);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postViewString(String view, boolean statisticsOnly) throws LDMConnectorException {
        //TODO: Use statisticsOnly also for SamplystoreBiobanks
        boolean statisticsOnlyUsed = !isLdmSamplystoreBiobank() && statisticsOnly;

        try {
            return ldmClient.postViewString(view, statisticsOnlyUsed);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postCriteriaString(String criteria, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException {
        try {
            Query query = QueryConverter.xmlToQuery(criteria);
            return postQuery(query, null, completeMdsViewFields, statisticsOnly, includeAdditionalViewfields);
        } catch (JAXBException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T_RESULT getResults(String location) throws LDMConnectorException {
        try {
            return ldmClient.getResult(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T_RESULT getResultsFromPage(String location, int page) throws LDMConnectorException {
        try {
            return ldmClient.getResultPage(location, page);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isQueryPresent(String location) throws LDMConnectorException {
        try {
            return ldmClient.isQueryPresent(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getStatsOrError(String location) throws LDMConnectorException {
        try {
            return ldmClient.getStatsOrError(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResultStatistic getQueryResultStatistic(String location) throws LDMConnectorException {
        try {
            return ldmClient.getQueryResultStatistic(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getResultCount(String location) throws LDMConnectorException {
        try {
            return ldmClient.getResultCount(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getPageCount(String location) throws LDMConnectorException {
        try {
            return ldmClient.getQueryResultStatistic(location).getNumberOfPages();
        } catch (Exception e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstResultPageAvailable(String location) throws LDMConnectorException {
        if (SamplyShareUtils.isNullOrEmpty(location)) {
            throw new LDMConnectorException("Location of query is empty");
        }

        // If the stats are written and the results are empty, return true
        Integer resultCount = getResultCount(location);
        if (resultCount != null && resultCount == 0) {
            return true;
        }

        return ldmClient.isResultPageAvailable(location, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResultDone(String location, QueryResultStatistic queryResultStatistic) throws LDMConnectorException {
        if (SamplyShareUtils.isNullOrEmpty(location)) {
            throw new LDMConnectorException("Location of query is empty");
        }

        if (queryResultStatistic != null) {
            if (queryResultStatistic.getTotalSize() == 0) {
                return true;
            }
            int lastPageIndex = queryResultStatistic.getNumberOfPages() - 1;
            return ldmClient.isResultPageAvailable(location, lastPageIndex);
        } else {
            throw new LDMConnectorException("QueryResultStatistic is null.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeQueryResultPageToDisk(T_RESULT queryResult, int index) throws IOException {
        File dir = (File) ProjectInfo.INSTANCE.getServletContext().getAttribute(TEMPDIR);
        File xmlFile = new File(dir + System.getProperty("file.separator") + extractQueryResultId(queryResult) + "_" + index + "_transformed" + XML_SUFFIX);

        try {
            final JAXBContext context = JAXBContext.newInstance(QueryResult.class);
            final Marshaller marshaller = context.createMarshaller();
            marshalQueryResult(queryResult, xmlFile, marshaller);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentInfo() throws LDMConnectorException {
        try {
            return ldmClient.getUserAgentInfo();
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckResult checkConnection() {
        CheckResult result = new CheckResult();
        result.setExecutionDate(new Date());
        HttpGet httpGet = new HttpGet(baseUrl + "rest/info/");
        result.getMessages().add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));

        try (CloseableHttpResponse response = httpClient.execute(host, httpGet)) {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

            result.getMessages().add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 400) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
                result.getMessages().add(new Message(EntityUtils.toString(entity), "fa-long-arrow-left"));
            }
        } catch (IOException ioe) {
            result.getMessages().add(new Message("IOException: " + ioe.getMessage(), "fa-bolt"));
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPatientCount(boolean dktkFlagged) throws LDMConnectorException, InterruptedException {
        int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
        int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
        int retryNr = 0;

        View view = createViewForMonitoring(dktkFlagged);
        String resultLocation = null;
        try {
            boolean statisticsOnly = isLdmCentraxx();
            resultLocation = ldmClient.postView(view, statisticsOnly);
        } catch (LdmClientException e) {
            handleLdmClientException(e);
        }
        do {
            try {
                Integer resultCount = getResultCount(resultLocation);
                if (resultCount != null) {
                    return resultCount;
                }
            } catch (LDMConnectorException e) {
                // Catch the exception since it might just mean the result is not ready yet
            }
            TimeUnit.SECONDS.sleep(secondsSleep);
        } while (++retryNr < maxAttempts);
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceQueryCheckResult getReferenceQueryCheckResult(Query referenceQuery) throws LDMConnectorException {
        ReferenceQueryCheckResult result = new ReferenceQueryCheckResult();
        try {
            View referenceView = createReferenceViewForMonitoring(referenceQuery);
            Stopwatch stopwatch = Stopwatch.createStarted();
            String resultLocation = ldmClient.postView(referenceView, false);

            int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
            int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
            int retryNr = 0;
            do {
                try {
                    Object statsOrError = ldmClient.getStatsOrError(resultLocation);

                    if (statsOrError.getClass().equals(de.samply.share.model.common.Error.class)) {
                        stopwatch.reset();
                        de.samply.share.model.common.Error error = (Error) statsOrError;

                        switch (error.getErrorCode()) {
                            case LdmClient.ERROR_CODE_DATE_PARSING_ERROR:
                            case LdmClient.ERROR_CODE_UNIMPLEMENTED:
                            case LdmClient.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
                                getLogger().warn("Could not execute reference query correctly. Error: " + error.getErrorCode() + ": " + error.getDescription());
                                return result;
                            case LdmClient.ERROR_CODE_UNKNOWN_MDRKEYS:
                            default:
                                ArrayList<String> unknownKeys = new ArrayList<>(error.getMdrKey());
                                referenceView = QueryConverter.removeAttributesFromView(referenceView, unknownKeys);
                                stopwatch.start();
                                resultLocation = ldmClient.postView(referenceView, false);
                                break;
                        }
                    } else if (statsOrError.getClass().equals(QueryResultStatistic.class)) {
                        QueryResultStatistic qrs = (QueryResultStatistic) statsOrError;
                        result.setCount(qrs.getTotalSize());
                        if (isResultDone(resultLocation, qrs)) {
                            stopwatch.stop();
                            result.setExecutionTimeMilis(stopwatch.elapsed(TimeUnit.MILLISECONDS));
                            return result;
                        }
                    }

                    retryNr += 1;
                    TimeUnit.SECONDS.sleep(secondsSleep);
                } catch (InterruptedException e) {
                    return result;
                }
            } while (retryNr < maxAttempts);
        } catch (LdmClientException e) {
            handleLdmClientException(e);
        }
        return result;
    }

    abstract T_LDM_CLIENT createLdmClient(
            CloseableHttpClient httpClient, String baseUrl, boolean useCaching) throws LdmClientException;

    abstract T_LDM_CLIENT createLdmClient(
            CloseableHttpClient httpClient, String baseUrl, boolean useCaching, int maxCacheSize) throws LdmClientException;

    abstract View createView(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean includeAdditionalViewfields) throws LDMConnectorException;

    abstract View createReferenceViewForMonitoring(Query referenceQuery) throws LDMConnectorException;

    abstract View createViewForMonitoring(boolean dktkFlagged) throws LDMConnectorException;

    abstract void marshalQueryResult(T_RESULT queryResult, File xmlFile, Marshaller marshaller) throws JAXBException;

    abstract String extractQueryResultId(T_RESULT queryResult);

    abstract Logger getLogger();

    private void handleLdmClientException(LdmClientException e) throws LDMConnectorException {
        if (isLdmCentraxx()) {
            throw new LDMConnectorException(e);
        } else if (isLdmSamplystoreBiobank()) {
            e.printStackTrace();
        }
    }
}
