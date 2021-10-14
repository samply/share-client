package de.samply.share.client.util.connector;

import com.google.common.base.Stopwatch;
import de.samply.common.ldmclient.AbstractLdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.LdmClientView;
import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.quality.report.chainlinks.instances.statistic.StatisticContext;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.Result;
import de.samply.share.model.common.View;
import de.samply.share.utils.QueryConverter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public abstract class AbstractLdmConnectorView<
    T_LDM_CLIENT extends LdmClientView<T_RESULT, ResultStatisticsT, ErrorT, SpecificViewT>,
    T_RESULT extends Result & Serializable,
    ResultStatisticsT extends Serializable,
    ErrorT extends Serializable,
    SpecificViewT extends Serializable> extends
    AbstractLdmConnector<T_LDM_CLIENT, LdmPostQueryParameterView, Query, T_RESULT,
        ResultStatisticsT, ErrorT> {

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
  public String postQuery(Query query, LdmPostQueryParameterView parameter)
      throws LdmConnectorException {
    View view = createView(query, parameter.getRemoveKeysFromView(),
        parameter.isCompleteMdsViewFields(), parameter.isIncludeAdditionalViewfields());

    try {
      return ldmClient.postView(view, parameter.isStatisticsOnly());
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReferenceQueryCheckResult getReferenceQueryCheckResult(Query referenceQuery)
      throws LdmConnectorException {
    ReferenceQueryCheckResult result = new ReferenceQueryCheckResult();
    try {
      View referenceView = createReferenceViewForMonitoring(referenceQuery);
      Stopwatch stopwatch = Stopwatch.createStarted(); //Stop time for Referenzquerry: Ausführzeit
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
                getLogger().warn(
                    "Could not execute reference query correctly. Error: " + error.getErrorCode()
                        + ": " + error.getDescription());
                return result;
              case AbstractLdmClient.ERROR_CODE_UNKNOWN_MDRKEYS:
              default:
                ArrayList<String> unknownKeys = new ArrayList<>(error.getMdrKey());
                referenceView = QueryConverter.removeAttributesFromView(referenceView, unknownKeys);
                stopwatch.start();
                resultLocation = ldmClient.postView(referenceView, true);
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

            return result;
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
  public int getPatientCount(boolean dktkFlagged)
      throws LdmConnectorException, InterruptedException {
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
      } catch (LdmConnectorException e) {
        // Catch the exception since it might just mean the result is not ready yet
      }
      TimeUnit.SECONDS.sleep(secondsSleep);
    } while (++retryNr < maxAttempts);
    return 0;
  }

  private void handleLdmClientException(LdmClientException e) throws LdmConnectorException {
    if (isLdmCentraxx()) {
      throw new LdmConnectorException(e);
    } else if (isLdmSamplystoreBiobank()) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFirstResultPageAvailable(String location) throws LdmConnectorException {
    if (SamplyShareUtils.isNullOrEmpty(location)) {
      throw new LdmConnectorException("Location of query is empty");
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
  public boolean isResultDone(String location, QueryResultStatistic queryResultStatistic)
      throws LdmConnectorException {
    if (SamplyShareUtils.isNullOrEmpty(location)) {
      throw new LdmConnectorException("Location of query is empty");
    }

    if (queryResultStatistic != null) {
      if (queryResultStatistic.getTotalSize() == 0) {
        return true;
      }
      int lastPageIndex = queryResultStatistic.getNumberOfPages() - 1;
      boolean isResultDone = ldmClient.isResultPageAvailable(location, lastPageIndex);
      return isResultDone;
    } else {
      throw new LdmConnectorException("QueryResultStatistic is null.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T_RESULT getResultsFromPage(String location, int page) throws LdmConnectorException {
    try {
      return ldmClient.getResultPage(location, page);
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }

  @Override
  public CheckResult checkConnection() {
    CheckResult result = new CheckResult();
    result.setExecutionDate(new Date());
    HttpGet httpGet = new HttpGet(baseUrl + "rest/info/");
    result.getMessages()
        .add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));

    try (CloseableHttpResponse response = httpClient.execute(host, httpGet)) {
      HttpEntity entity = response.getEntity();
      EntityUtils.consume(entity);

      result.getMessages()
          .add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
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

  abstract View createView(Query query, List<String> removeKeysFromView,
      boolean completeMdsViewFields, boolean includeAdditionalViewfields)
      throws LdmConnectorException;

  abstract View createReferenceViewForMonitoring(Query referenceQuery) throws LdmConnectorException;

  abstract View createViewForMonitoring(boolean dktkFlagged) throws LdmConnectorException;
}
