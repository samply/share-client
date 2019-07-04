package de.samply.share.client.util.connector;

import com.google.common.base.Stopwatch;
import de.samply.common.ldmclient.LdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.Result;
import de.samply.share.model.common.View;
import de.samply.share.utils.QueryConverter;

import javax.xml.bind.JAXBException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLdmConnectorQuery<
        T_LDM_CLIENT extends LdmClient<T_RESULT, T_RESULT_STATISTICS, T_ERROR, T_SPECIFIC_VIEW>,
        T_RESULT extends Result & Serializable,
        T_RESULT_STATISTICS extends Serializable,
        T_ERROR extends Serializable,
        T_SPECIFIC_VIEW extends Serializable> extends AbstractLdmConnector<T_LDM_CLIENT, Query, T_RESULT, T_RESULT_STATISTICS, T_ERROR, T_SPECIFIC_VIEW> {

    AbstractLdmConnectorQuery(boolean useCaching) {
        super(useCaching);
    }

    AbstractLdmConnectorQuery(boolean useCaching, int maxCacheSize) {
        super(useCaching, maxCacheSize);
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

    private void handleLdmClientException(LdmClientException e) throws LDMConnectorException {
        if (isLdmCentraxx()) {
            throw new LDMConnectorException(e);
        } else if (isLdmSamplystoreBiobank()) {
            e.printStackTrace();
        }
    }
}
