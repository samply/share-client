package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.ReportToMonitoringJobParams;
import de.samply.share.client.model.EnumReportMonitoring;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.common.model.dto.monitoring.StatusReportItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job gathers the amount of patients (total, dktk flagged and for a reference query) and the
 * time it takes to get the results (with an uncertainty of ~15s in order to avoid polling too
 * frequently) for the reference query.
 */
@DisallowConcurrentExecution
public class ReportToMonitoringJobLongFrequence implements Job {

  private static final Logger logger =
          LoggerFactory.getLogger(ReportToMonitoringJobLongFrequence.class);

  private final LdmConnector ldmConnector;
  private final List<BrokerConnector> brokerConnectors;
  private final ReportToMonitoringJobParams jobParams;

  /**
   * Get the ldmConnector, the registered brokers and the params.
   */
  public ReportToMonitoringJobLongFrequence() {
    ldmConnector = ApplicationBean.getLdmConnector();
    brokerConnectors = BrokerUtil.fetchBrokers().stream().map(BrokerConnector::new)
        .collect(Collectors.toList());
    jobParams = new ReportToMonitoringJobParams();

    logger.debug(ReportToMonitoringJobLongFrequence.class.getName() + " created");
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    if (jobParams.anyCheckToPerform()) {
      for (BrokerConnector brokerConnector : brokerConnectors) {
        logger.debug("sending report to broker: " + brokerConnector.getBroker().getAddress());
        List<StatusReportItem> statusReportItems = gatherStatusReportItems(brokerConnector);
        try {
          brokerConnector.sendStatusReportItems(statusReportItems);
        } catch (BrokerConnectorException e) {
          logger.warn("Caught exception while trying to report to monitoring", e);
        }
      }
    }
  }

  /**
   * Delegate to all check methods that are enabled.
   *
   * @param brokerConnector the broker connector used to get the reference query
   * @return a list of items to be reported
   */
  private List<StatusReportItem> gatherStatusReportItems(BrokerConnector brokerConnector) {
    List<StatusReportItem> statusReportItems = new ArrayList<>();

    if (ApplicationUtils.isDktk()) {
      if (jobParams.isCountReferenceQuery() || jobParams.isTimeReferenceQuery()) {
        ReferenceQueryCheckResult referenceQueryCheckResult;
        String errorMessage = "";
        try {
          referenceQueryCheckResult = getReferenceQueryResult(brokerConnector);
        } catch (BrokerConnectorException | LdmConnectorException e) {

          errorMessage = e.getMessage();
          referenceQueryCheckResult = new ReferenceQueryCheckResult();
        }
        if (jobParams.isCountReferenceQuery()) {
          StatusReportItem referenceQueryCount = getReferenceQueryCount(referenceQueryCheckResult,
              errorMessage);
          statusReportItems.add(referenceQueryCount);
          logger.debug("reference query count calculated");
        }
        if (jobParams.isTimeReferenceQuery()) {
          StatusReportItem referenceQueryTime = getReferenceQueryTime(referenceQueryCheckResult,
              errorMessage);
          statusReportItems.add(referenceQueryTime);
          logger.debug("reference query time calculated");
        }
      }
    }
    return statusReportItems;
  }

  /**
   * Execute the reference query and receive the patient count and the execution time.
   *
   * @param brokerConnector the broker connector from which to get the reference query
   * @return patient count and the execution time
   */
  private ReferenceQueryCheckResult getReferenceQueryResult(BrokerConnector brokerConnector)
      throws BrokerConnectorException, LdmConnectorException {
    ReferenceQueryCheckResult referenceQueryCheckResult = null;
    if (ApplicationUtils.isLanguageQuery()) {
      referenceQueryCheckResult = ldmConnector
          .getReferenceQueryCheckResult(brokerConnector.getReferenceQuery());
    } else if (ApplicationUtils.isLanguageCql()) {
      referenceQueryCheckResult = ldmConnector
          .getReferenceQueryCheckResult(brokerConnector.getReferenceQueryCql());
    }
    return referenceQueryCheckResult;
  }


  /**
   * Get the StatusReportItem for the Amount of patients from a ReferenceQueryCheckResult.
   *
   * @param referenceQueryCheckResult the result that was generated while executing the reference
   *                                  query
   * @return a status report item, containing the total amount of patients found in local
   *        datamanagement for the reference query
   */
  private StatusReportItem getReferenceQueryCount(
      ReferenceQueryCheckResult referenceQueryCheckResult, String errorMessage) {
    StatusReportItem referenceQueryCount = new StatusReportItem();
    referenceQueryCount.setParameterName(StatusReportItem.PARAMETER_REFERENCE_QUERY_RESULTCOUNT);

    if (referenceQueryCheckResult != null && referenceQueryCheckResult.getCount() >= 0) {
      referenceQueryCount.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
      referenceQueryCount.setStatusText(Integer.toString(referenceQueryCheckResult.getCount()));
    } else {
      logger.error(errorMessage);
      referenceQueryCount.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      referenceQueryCount.setStatusText(errorMessage);
    }
    return referenceQueryCount;
  }


  /**
   * Get the StatusReportItem for the time it took to execute the reference query from a
   * ReferenceQueryCheckResult.
   *
   * @param referenceQueryCheckResult the result that was generated while executing the reference
   *                                  query
   * @return a status report item, containing the time it took to execute the reference query
   *        (containing a vagueness of 15 seconds)
   */
  private StatusReportItem getReferenceQueryTime(
      ReferenceQueryCheckResult referenceQueryCheckResult, String errorMessage) {
    StatusReportItem referenceQueryTime = new StatusReportItem();
    referenceQueryTime.setParameterName(StatusReportItem.PARAMETER_REFERENCE_QUERY_RUNTIME);

    if (referenceQueryCheckResult != null
        && referenceQueryCheckResult.getExecutionTimeMilis() >= 0) {
      referenceQueryTime.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
      referenceQueryTime
          .setStatusText(Long.toString(referenceQueryCheckResult.getExecutionTimeMilis()));
    } else {
      logger.error(errorMessage);
      referenceQueryTime.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      referenceQueryTime.setStatusText(errorMessage);
    }
    return referenceQueryTime;
  }
}
