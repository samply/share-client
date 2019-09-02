package de.samply.share.client.util.connector;

import com.google.common.base.Stopwatch;
import de.samply.common.ldmclient.AbstractLdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.LdmClientView;
import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.Result;
import de.samply.share.model.common.View;
import de.samply.share.utils.QueryConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLdmConnectorView<
        T_LDM_CLIENT extends LdmClientView<T_RESULT, T_RESULT_STATISTICS, T_ERROR, T_SPECIFIC_VIEW>,
        T_RESULT extends Result & Serializable,
        T_RESULT_STATISTICS extends Serializable,
        T_ERROR extends Serializable,
        T_SPECIFIC_VIEW extends Serializable> extends AbstractLdmConnector<T_LDM_CLIENT, Query, T_RESULT, T_RESULT_STATISTICS, T_ERROR> {

    AbstractLdmConnectorView(boolean useCaching) {
        super(useCaching);
    }

    AbstractLdmConnectorView(boolean useCaching, int maxCacheSize) {
        super(useCaching, maxCacheSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postQuery(Query query,
                            String entityType, List<String> removeKeysFromView,
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
                    LdmQueryResult ldmQueryResult = ldmClient.getStatsOrError(resultLocation);

                    if (ldmQueryResult.hasError()) {
                        stopwatch.reset();
                        de.samply.share.model.common.Error error = ldmQueryResult.getError();

                        switch (error.getErrorCode()) {
                            case AbstractLdmClient.ERROR_CODE_DATE_PARSING_ERROR:
                            case AbstractLdmClient.ERROR_CODE_UNIMPLEMENTED:
                            case AbstractLdmClient.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
                                getLogger().warn("Could not execute reference query correctly. Error: " + error.getErrorCode() + ": " + error.getDescription());
                                return result;
                            case AbstractLdmClient.ERROR_CODE_UNKNOWN_MDRKEYS:
                            default:
                                ArrayList<String> unknownKeys = new ArrayList<>(error.getMdrKey());
                                referenceView = QueryConverter.removeAttributesFromView(referenceView, unknownKeys);
                                stopwatch.start();
                                resultLocation = ldmClient.postView(referenceView, false);
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
            handleLdmClientException(e);
        }
        return result;
    }

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

    private void handleLdmClientException(LdmClientException e) throws LDMConnectorException {
        if (isLdmCentraxx()) {
            throw new LDMConnectorException(e);
        } else if (isLdmSamplystoreBiobank()) {
            e.printStackTrace();
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
    public T_RESULT getResultsFromPage(String location, int page) throws LDMConnectorException {
        try {
            return ldmClient.getResultPage(location, page);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    abstract View createView(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean includeAdditionalViewfields) throws LDMConnectorException;

    abstract View createReferenceViewForMonitoring(Query referenceQuery) throws LDMConnectorException;

    abstract View createViewForMonitoring(boolean dktkFlagged) throws LDMConnectorException;
}
