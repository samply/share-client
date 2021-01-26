package de.samply.share.client.job;

import com.google.gson.JsonObject;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.ReportToMonitoringJobParams;
import de.samply.share.client.model.EnumReportMonitoring;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.JobSchedule;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxxExtension;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.JobScheduleUtil;
import de.samply.share.common.model.dto.monitoring.StatusReportItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * This job gathers the amount of patients (total, dktk flagged and for a reference query) and the
 * time it takes to get the results (with an uncertainty of ~15s in order to avoid polling too
 * frequently) for the reference query.
 */
@DisallowConcurrentExecution
public class ReportToMonitoringJob implements Job {

  private static final Logger LOGGER = LogManager.getLogger(ReportToMonitoringJob.class);

  private final LdmConnector ldmConnector;
  private final List<BrokerConnector> brokerConnectors;
  private final ReportToMonitoringJobParams jobParams;

  /**
   * Get the ldmConnector, the registered brokers and the params.
   */
  public ReportToMonitoringJob() {
    ldmConnector = ApplicationBean.getLdmConnector();
    brokerConnectors = BrokerUtil.fetchBrokers().stream().map(BrokerConnector::new)
        .collect(Collectors.toList());
    jobParams = new ReportToMonitoringJobParams();

    LOGGER.debug(ReportToMonitoringJob.class.getName() + " created");
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    if (jobParams.anyCheckToPerform()) {
      for (BrokerConnector brokerConnector : brokerConnectors) {
        LOGGER.debug("sending report to broker: " + brokerConnector.getBroker().getAddress());
        List<StatusReportItem> statusReportItems = gatherStatusReportItems(brokerConnector);
        try {
          brokerConnector.sendStatusReportItems(statusReportItems);
        } catch (BrokerConnectorException e) {
          LOGGER.warn("Caught exception while trying to report to monitoring", e);
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
      if (jobParams.isCountTotal()) {
        StatusReportItem totalCount = getTotalCount();
        statusReportItems.add(totalCount);
        LOGGER.debug("total count calculated");
      }

      if (jobParams.isCountDktkFlagged()) {
        StatusReportItem dktkCount = getDktkCount();
        statusReportItems.add(dktkCount);
        LOGGER.debug("dktk count calculated");
      }

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
          LOGGER.debug("reference query count calculated");
        }
        if (jobParams.isTimeReferenceQuery()) {
          StatusReportItem referenceQueryTime = getReferenceQueryTime(referenceQueryCheckResult,
              errorMessage);
          statusReportItems.add(referenceQueryTime);
          LOGGER.debug("reference query time calculated");
        }
      }

      if (jobParams.isCentraxxMappingInformation()) {
        StatusReportItem centraxxMappingVersion = getCentraxxMappingVersion();
        statusReportItems.add(centraxxMappingVersion);
        StatusReportItem centraxxMappingDate = getCentraxxMappingDate();
        statusReportItems.add(centraxxMappingDate);
        LOGGER.debug("centraxx mapping version calculated");
      }
    }

    if (ApplicationUtils.isSamply()) {
      if (jobParams.isCountTotal()) {
        StatusReportItem totalCount = getTotalCount();
        statusReportItems.add(totalCount);
        LOGGER.debug("total count calculated");
      }
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
          LOGGER.debug("reference query count calculated");
        }
        if (jobParams.isTimeReferenceQuery()) {
          StatusReportItem referenceQueryTime = getReferenceQueryTime(referenceQueryCheckResult,
              errorMessage);
          statusReportItems.add(referenceQueryTime);
          LOGGER.debug("reference query time calculated");
        }
      }
    }
    statusReportItems.add(getJobConfig());
    statusReportItems.add(getInquiryStats());
    return statusReportItems;
  }

  /**
   * Read the job configs.
   *
   * @return the job configs and the exit status
   */
  private StatusReportItem getJobConfig() {
    List<Map<String, String>> records = new ArrayList<>();
    StatusReportItem jobConfig = new StatusReportItem();
    jobConfig.setParameterName(StatusReportItem.PARAMETER_JOB_CONFIG);
    try {
      List<JobSchedule> jobScheduleList = JobScheduleUtil.getJobSchedules();
      for (JobSchedule jobSchedule : jobScheduleList) {
        Map<String, String> map = new HashMap<>();
        map.put("JobName", jobSchedule.getJobKey());
        map.put("CronExpression", jobSchedule.getCronExpression());
        map.put("Paused", jobSchedule.getPaused().toString());
        records.add(map);
      }
      jobConfig.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
      jobConfig.setStatusText(records.toString());
    } catch (Exception e) {
      jobConfig.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      jobConfig.setStatusText(e.getMessage());
    }
    return jobConfig;
  }

  /**
   * Read the inquiry counts.
   *
   * @return the inquiry count and the exit status
   */
  private StatusReportItem getInquiryStats() {
    JsonObject jsonObject = new JsonObject();
    StatusReportItem inquiryStats = new StatusReportItem();
    inquiryStats.setParameterName(StatusReportItem.PARAMETER_INQUIRY_INFO);
    try {
      jsonObject.addProperty("NEW", InquiryUtil.countInquiries(InquiryStatusType.IS_NEW));
      jsonObject.addProperty("PROCESSING",
          InquiryUtil.countInquiries(InquiryStatusType.IS_PROCESSING));
      jsonObject.addProperty("READY",
          InquiryUtil.countInquiries(InquiryStatusType.IS_READY));
      jsonObject.addProperty("ABANDONED",
          InquiryUtil.countInquiries(InquiryStatusType.IS_ABANDONED));
      jsonObject.addProperty("LDM_ERROR",
          InquiryUtil.countInquiries(InquiryStatusType.IS_LDM_ERROR));
      jsonObject.addProperty("Last query execution time",
          InquiryDetailsUtil.getLastScheduledInquiry().getScheduledAt().toString());
      inquiryStats.setStatusText(jsonObject.toString());
      inquiryStats.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
    } catch (Exception e) {
      inquiryStats.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      inquiryStats.setStatusText(e.getMessage());
    }
    return inquiryStats;

  }

  /**
   * Get the amount of patients that have the DKTK consent flag set from local datamanagement.
   *
   * @return the amount of patients and the exit status
   */
  private StatusReportItem getDktkCount() {
    StatusReportItem dktkCount = new StatusReportItem();
    dktkCount.setParameterName(StatusReportItem.PARAMETER_PATIENTS_DKTKFLAGGED_COUNT);
    try {
      int count = ldmConnector.getPatientCount(true);
      dktkCount.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
      dktkCount.setStatusText(Integer.toString(count));
    } catch (Exception e) {
      LOGGER.error(e);
      dktkCount.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      dktkCount.setStatusText(e.getMessage());
    }
    return dktkCount;
  }

  /**
   * Get the total amount of patients from local datamanagement. This will only count patients with
   * a DKTK site pseudonym that fit the ldm-defined criteria. As of now, this includes patients with
   * a C.* or certain D.* diagnoses.
   *
   * @return the amount of patients and the exit status
   */
  private StatusReportItem getTotalCount() {
    StatusReportItem totalCount = new StatusReportItem();
    totalCount.setParameterName(StatusReportItem.PARAMETER_PATIENTS_TOTAL_COUNT);
    try {
      int count = ldmConnector.getPatientCount(false);
      totalCount.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
      totalCount.setStatusText(Integer.toString(count));
    } catch (Exception e) {
      LOGGER.error(e);
      totalCount.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      totalCount.setStatusText(e.getMessage());
    }
    return totalCount;
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
      LOGGER.error(errorMessage);
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
      LOGGER.error(errorMessage);
      referenceQueryTime.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
      referenceQueryTime.setStatusText(errorMessage);
    }
    return referenceQueryTime;
  }

  /**
   * Get the version string of the centraxx mdr mapping script as StatusReportItem.
   *
   * @return the version string of mdr mapping script
   */
  private StatusReportItem getCentraxxMappingVersion() {
    StatusReportItem centraxxMappingVersion = new StatusReportItem();
    centraxxMappingVersion.setParameterName(StatusReportItem.PARAMETER_CENTRAXX_MAPPING_VERSION);
    if (!(ldmConnector instanceof LdmConnectorCentraxxExtension)) {
      centraxxMappingVersion.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_WARNING.getValue());
      centraxxMappingVersion.setStatusText("Does not apply");
    } else {
      try {
        String mappingVersion = ((LdmConnectorCentraxxExtension) ldmConnector).getMappingVersion();
        centraxxMappingVersion.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
        centraxxMappingVersion.setStatusText(mappingVersion);
      } catch (Exception e) {
        LOGGER.error(e);
        centraxxMappingVersion.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
        centraxxMappingVersion.setStatusText(e.getMessage());
      }
    }
    return centraxxMappingVersion;
  }

  /**
   * Get the date string of the centraxx mdr mapping script as StatusReportItem.
   *
   * @return the date string of mdr mapping script
   */
  private StatusReportItem getCentraxxMappingDate() {
    StatusReportItem centraxxMappingDate = new StatusReportItem();
    centraxxMappingDate.setParameterName(StatusReportItem.PARAMETER_CENTRAXX_MAPPING_DATE);
    if (!(ldmConnector instanceof LdmConnectorCentraxxExtension)) {
      centraxxMappingDate.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_WARNING.getValue());
      centraxxMappingDate.setStatusText("Does not apply");
    } else {
      try {
        String mappingDate = ((LdmConnectorCentraxxExtension) ldmConnector).getMappingDate();
        centraxxMappingDate.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_OK.getValue());
        centraxxMappingDate.setStatusText(mappingDate);
      } catch (Exception e) {
        LOGGER.error(e);
        centraxxMappingDate.setExitStatus(EnumReportMonitoring.ICINGA_STATUS_ERROR.getValue());
        centraxxMappingDate.setStatusText(e.getMessage());
      }
    }
    return centraxxMappingDate;
  }
}
