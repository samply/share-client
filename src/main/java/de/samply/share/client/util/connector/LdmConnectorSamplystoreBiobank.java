package de.samply.share.client.util.connector;

import com.google.common.base.Stopwatch;
import de.samply.common.ldmclient.LdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.samplystoreBiobank.LdmClientSamplystoreBiobank;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.MdrUtils;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.*;
import de.samply.share.utils.QueryConverter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the LdmConnector interface for samply store rest backends
 */
public class LdmConnectorSamplystoreBiobank extends AbstractLdmConnector<LdmClientSamplystoreBiobank, BbmriResult, de.samply.share.model.osse.QueryResultStatistic, de.samply.share.model.osse.Error, de.samply.share.model.osse.View, Patient> {

    private static final Logger logger = LogManager.getLogger(LdmConnectorSamplystoreBiobank.class);

    public LdmConnectorSamplystoreBiobank(boolean useCaching) {
        init(useCaching);
    }

    public LdmConnectorSamplystoreBiobank(boolean useCaching, int maxCacheSize) {
        init(useCaching, maxCacheSize);
    }

    @Override
    public boolean isLdmSamplystoreBiobank() {
        return true;
    }

    protected LdmClientSamplystoreBiobank createLdmClient(CloseableHttpClient httpClient, String baseUrl, boolean useCaching) throws LdmClientException {
        return new LdmClientSamplystoreBiobank(httpClient, baseUrl, useCaching);
    }

    protected LdmClientSamplystoreBiobank createLdmClient(CloseableHttpClient httpClient, String baseUrl, boolean useCaching, int maxCacheSize) throws LdmClientException {
        return new LdmClientSamplystoreBiobank(httpClient, baseUrl, useCaching, maxCacheSize);
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
        View view = new View();
        view.setQuery(query);
        // TODO: How to get viewfields for samply store to use?
        ViewFields viewFields = new ViewFields();

        view.setViewFields(viewFields);

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
        try {
            //TODO: Add missing parameter statisticsOnly
            return ldmClient.postViewString(view);
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
    public BbmriResult getResults(String location) throws LDMConnectorException {
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
    public BbmriResult getResultsFromPage(String location, int page) throws LDMConnectorException {
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
    public void writeQueryResultPageToDisk(BbmriResult queryResult, int index) throws IOException {
        File dir = (File) ProjectInfo.INSTANCE.getServletContext().getAttribute(TEMPDIR);
        File xmlFile = new File(dir + System.getProperty("file.separator") + extractQueryResultId(queryResult) + "_" + index + "_transformed" + XML_SUFFIX);

        try {
            final JAXBContext context = JAXBContext.newInstance(BbmriResult.class);
            final Marshaller marshaller = context.createMarshaller();
            marshalQueryResult(queryResult, xmlFile, marshaller);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    protected String extractQueryResultId(BbmriResult queryResult) {
        return Integer.toString(queryResult.getQueryId());
    }

    protected void marshalQueryResult(BbmriResult queryResult, File xmlFile, Marshaller marshaller) throws JAXBException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(queryResult, xmlFile);
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

        View view = createViewForMonitoring();
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

                    if (statsOrError.getClass().equals(Error.class)) {
                        stopwatch.reset();
                        Error error = (Error) statsOrError;

                        switch (error.getErrorCode()) {
                            case LdmClient.ERROR_CODE_DATE_PARSING_ERROR:
                            case LdmClient.ERROR_CODE_UNIMPLEMENTED:
                            case LdmClient.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
                                logger.warn("Could not execute reference query correctly. Error: " + error.getErrorCode() + ": " + error.getDescription());
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

    /**
     * Create a basic view that is used to get the amount of patients in centraxx
     *
     * @return the constructed view object that can be posted to centraxx
     */
    private View createViewForMonitoring() throws LDMConnectorException {
        MdrIdDatatype mdrKeyDktkConsent =
                new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CONSENT_DKTK));
        View view = new View();
        Query query = new Query();
        Where where = new Where();
        query.setWhere(where);
        view.setQuery(query);
        try {
            view.setViewFields(MdrUtils.getViewFields(false));
        } catch (MdrConnectionException | ExecutionException e) {
            throw new LDMConnectorException(e);
        }
        return view;
    }

    /**
     * Create the reference view that is used to get the amount of patients for that query
     *
     * @param referenceQuery the reference query, as received from the broker
     * @return the constructed view object that can be posted to centraxx
     */
    private View createReferenceViewForMonitoring(Query referenceQuery) {
        View view = new View();
        view.setQuery(referenceQuery);
        return view;
    }
}
