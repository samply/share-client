package de.samply.share.client.util.connector;

import com.google.common.base.Stopwatch;
import de.samply.common.ldmclient.AbstractLdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.cql.LdmClientCql;
import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.job.util.InquiryCriteriaEntityType;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.cql.CqlResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class LdmConnectorCql extends AbstractLdmConnector<LdmClientCql, LdmPostQueryParameterCql, String, CqlResult, CqlResult, Error> {

    private static final Logger logger = LogManager.getLogger(LdmConnectorCql.class);

    public LdmConnectorCql(boolean useCaching) {
        super(useCaching);
    }

    public LdmConnectorCql(boolean useCaching, int maxCacheSize) {
        super(useCaching, maxCacheSize);
    }

    @Override
    boolean useAuthorizationForLdm() {
        return false;
    }

    @Override
    LdmClientCql createLdmClient(CloseableHttpClient httpClient, String baseUrl, boolean useCaching) throws LdmClientException {
        return new LdmClientCql(httpClient, baseUrl);
    }

    @Override
    LdmClientCql createLdmClient(CloseableHttpClient httpClient, String baseUrl, boolean useCaching, int maxCacheSize) throws LdmClientException {
        return createLdmClient(httpClient, baseUrl, useCaching);
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
    public String postQuery(String query, LdmPostQueryParameterCql parameter) throws LDMConnectorException {
        try {
            return ldmClient.postQuery(query, parameter.getEntityType(), parameter.isStatisticsOnly());
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    @Override
    public CqlResult getResultsFromPage(String location, int page) throws LDMConnectorException {
        // TODO: Check if we can work without pages
        return null;
    }

    @Override
    public boolean isFirstResultPageAvailable(String location) throws LDMConnectorException {
        // TODO: Check if we can work without pages
        return false;
    }


    public boolean isResultDone(String location, QueryResultStatistic queryResultStatistic) throws LDMConnectorException {
        if (StringUtils.isEmpty(location)) {
            throw new LDMConnectorException("Location of query is empty");
        }
        if (queryResultStatistic != null) {
            return queryResultStatistic.getTotalSize() >= 0;
        } else {
            throw new LDMConnectorException("QueryResultStatistic is null.");
        }
    }

    @Override
    public int getPatientCount(boolean dktkFlagged) throws LDMConnectorException, InterruptedException {
        int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
        int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
        int retryNr = 0;

        String query = createQueryForMonitoring();
        String resultLocation;
        try {
            resultLocation = ldmClient.postQuery(query, InquiryCriteriaEntityType.PATIENT.getName(), true);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
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

    @Override
    public ReferenceQueryCheckResult getReferenceQueryCheckResult(String referenceQuery) throws LDMConnectorException {
        ReferenceQueryCheckResult result = new ReferenceQueryCheckResult();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            String resultLocation = ldmClient.postQuery(referenceQuery, InquiryCriteriaEntityType.PATIENT.getName(), true);

            int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
            int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
            int retryNr = 0;
            do {
                try {
                    LdmQueryResult ldmQueryResult = ldmClient.getStatsOrError(resultLocation);

                    if (ldmQueryResult.hasError()) {
                        stopwatch.reset();
                        Error error = ldmQueryResult.getError();

                        switch (error.getErrorCode()) {
                            case AbstractLdmClient.ERROR_CODE_DATE_PARSING_ERROR:
                            case AbstractLdmClient.ERROR_CODE_UNIMPLEMENTED:
                            case AbstractLdmClient.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
                                getLogger().warn("Could not execute reference query correctly. Error: " + error.getErrorCode() + ": " + error.getDescription());
                                return result;
                            case AbstractLdmClient.ERROR_CODE_UNKNOWN_MDRKEYS:
                            default:
                                stopwatch.start();
                                resultLocation = ldmClient.postQuery(referenceQuery, InquiryCriteriaEntityType.PATIENT.getName(), false);
                                break;
                        }
                    } else if (ldmQueryResult.hasResult()) {
                        QueryResultStatistic qrs = ldmQueryResult.getResult();
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
            throw new LDMConnectorException(e);
        }
        return result;
    }

    private String createQueryForMonitoring() {
        return "library Retrieve\n" +
                "using FHIR version '4.0.0'\n" +
                "\n" +
                "define InInitialPopulation:\n" +
                "  true";
    }

}
