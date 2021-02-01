package de.samply.share.client.job;

import com.google.common.base.Joiner;
import de.samply.common.ldmclient.AbstractLdmClient;
import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.CheckInquiryStatusJobResult;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.job.util.ReplyRulesApplier;
import de.samply.share.client.job.util.ReplyRulesApplierUtil;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UploadUtil;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.QueryResultStatistic;
import java.io.IOException;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * This Job checks the status of the given inquiry and spawns new jobs if necessary. It is defined
 * and scheduled by the ExecuteInquiryJob. The performed action depends on the previous state of the
 * Inquiry. 1) If the stats were not available earlier, check if they are. a) If they are not, the
 * job terminates this iteration and wait to be called again at the scheduled time b) If they are,
 * and it was an error...either quit and delete the trigger if the problem can not be solved or try
 * to fix it (remove unknown keys for example), remove this trigger and spawn a new
 * ExecuteInquiryJob with a modified inquiry. c) If they are, and it were stats...either set
 * everything to done (if only stats were requested or the result is 0) or reschedule this job,
 * setting the stats done parameter to true. 2) If the stats were available, more than 0 were found,
 * not only stats were requested and the first page of the result was not yet available, check if
 * the first page is accessible a) If it is not, the job terminates this iteration and wait to be
 * called again at the scheduled time b) If they are, set the corresponding parameter in the
 * jobdatamap, quit this iteration and wait for the next call. 3) If the stats were available, more
 * than 0 were found, not only stats were requested, the first page of the result was already
 * available, but the last page was not done yet...check if the last page is accessible now a) If it
 * is not, the job terminates this iteration and wait to be called again at the scheduled time b) If
 * it is, and it was an upload inquiry...spawn a UploadToCentralMdsDbJob and remove this job from
 * the scheduler c) If it is, and it was not an upload inquiry...set the status to done and remove
 * this job from the scheduler.
 */
abstract class AbstractCheckInquiryStatusJob<ldmConnectorT extends LdmConnector> implements Job {

  private static final Logger logger = LogManager.getLogger(AbstractCheckInquiryStatusJob.class);

  CheckInquiryStatusJobParams jobParams;
  ldmConnectorT ldmConnector;
  InquiryResult inquiryResult;
  InquiryDetails inquiryDetails;
  InquiryCriteria inquiryCriteria;

  AbstractCheckInquiryStatusJob() {
    //noinspection unchecked
    this.ldmConnector = (ldmConnectorT) ApplicationBean.getLdmConnector();
  }

  void prepareExecute(JobExecutionContext jobExecutionContext) {
    JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
    JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
    jobParams = new CheckInquiryStatusJobParams(dataMap);
    logger.debug(jobKey.toString() + " " + jobParams);

    inquiryResult = InquiryResultUtil.fetchInquiryResultById(jobParams.getInquiryResultId());
    inquiryDetails = InquiryDetailsUtil
        .fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
    inquiryCriteria = getInquiryCriteria();
  }

  abstract InquiryCriteria getInquiryCriteria();

  void checkForStatsResult(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    try {
      LdmQueryResult ldmQueryResult = ldmConnector.getStatsOrError(inquiryResult.getLocation());
      if (!ldmQueryResult.isEmpty()) {
        boolean isStats = handleStatsOrError(ldmQueryResult, jobExecutionContext);
        if (applyReplyRulesImmediately(isStats)) {
          // TODO: Check if the handling for uploads would be better in the following method
          processReplyRules();
        }
      }
    } catch (Exception e) {
      throw new JobExecutionException(e);
    }
  }

  abstract boolean applyReplyRulesImmediately(boolean isStats);

  void processReplyRules() {
    new ReplyRulesApplier(getProcessReplyRuleMethod()).processReplyRules(inquiryDetails);
  }

  abstract Consumer<BrokerConnector> getProcessReplyRuleMethod();

  void handleBrokerConnectorException(BrokerConnectorException e) {
    ReplyRulesApplierUtil.handleBrokerConnectorException(e, inquiryDetails.getId());
  }

  /**
   * Handle the outcome of the call to a /stats resource on the local datamanagement.
   *
   * @param ldmQueryResult      will either contain an error or a query result statistics object
   * @param jobExecutionContext the jobExecutionContext of this job instance
   * @return true if stats were received, false if an error was received or something unexpected
   *        happened
   */
  private boolean handleStatsOrError(LdmQueryResult ldmQueryResult,
      JobExecutionContext jobExecutionContext) throws SchedulerException {
    // null is returned e.g. if the stats are not yet available
    if (ldmQueryResult == null) {
      // Just continue with regular schedule
      return false;
    }

    if (ldmQueryResult.hasError()) {
      return handleError(ldmQueryResult, jobExecutionContext);
    }

    if (ldmQueryResult.hasResult()) {
      return handleResult(ldmQueryResult, jobExecutionContext);
    }

    log("Unknown object received");
    return false;
  }

  private boolean handleResult(LdmQueryResult ldmQueryResult,
      JobExecutionContext jobExecutionContext) throws SchedulerException {
    QueryResultStatistic queryResultStatistic = ldmQueryResult.getResult();
    log(EventMessageType.E_STATISTICS_READY, Integer.toString(queryResultStatistic.getTotalSize()));
    inquiryResult.setSize(queryResultStatistic.getTotalSize());
    inquiryResult.setStratifications(createStratificationAsJson(queryResultStatistic));

    InquiryResultUtil.updateInquiryResult(inquiryResult);
    jobExecutionContext.getJobDetail().getJobDataMap()
        .put(CheckInquiryStatusJobParams.STATS_DONE, true);
    if (inquiryResult.getStatisticsOnly() || queryResultStatistic.getTotalSize() == 0) {
      if (inquiryResult.getStatisticsOnly()) {
        logger.debug("Only stats were requested. And they are done. Setting Inquiry Details to"
            + " done and quitting.");
      } else {
        logger.debug("No results found. Setting Inquiry Details to done and quitting.");
      }

      // If the inquiry belongs to an upload, also update the upload status
      try {
        Integer uploadId = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId())
            .getUploadId();
        if (jobParams.isUpload() && uploadId != null) {
          UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_COMPLETED);
        }
      } catch (Exception e) {
        logger.error("Exception caught while trying to update upload status", e);
      }

      handleInquiryStatusReady();

      inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_READY);
      InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
      InquiryCriteriaUtil.updateInquiryCriteria(inquiryCriteria);
      try {
        jobExecutionContext.setResult(new CheckInquiryStatusJobResult(false,
            true));
        unscheduleThisJob(jobExecutionContext);
        return true;
      } catch (SchedulerException e) {
        e.printStackTrace();
      }
    } else {
      rescheduleCheckingForResults(jobExecutionContext);
    }
    return false;
  }

  private String createStratificationAsJson(QueryResultStatistic queryResultStatistic) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(queryResultStatistic.getStratification());
    } catch (IOException e) {
      return "[]";
    }
  }

  abstract void handleInquiryStatusReady();

  private boolean handleError(LdmQueryResult ldmQueryResult,
      JobExecutionContext jobExecutionContext) throws SchedulerException {
    Error error = ldmQueryResult.getError();

    inquiryResult.setIsError(Boolean.TRUE);
    inquiryResult.setErrorCode(Integer.toString(error.getErrorCode()));
    InquiryResultUtil.updateInquiryResult(inquiryResult);

    switch (error.getErrorCode()) {
      case AbstractLdmClient.ERROR_CODE_DATE_PARSING_ERROR:
      case AbstractLdmClient.ERROR_CODE_UNIMPLEMENTED:
      case AbstractLdmClient.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
        log(EventMessageType.E_LDM_ERROR, "code:" + error.getErrorCode(),
            "description:" + error.getDescription());
        Utils.setStatus(inquiryDetails, InquiryStatusType.IS_LDM_ERROR);
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
        unscheduleThisJob(jobExecutionContext);
        break;
      case AbstractLdmClient.ERROR_CODE_UNKNOWN_MDRKEYS:
        String unknownKeys = Joiner.on(ExecuteInquiryJobParams.SEPARATOR_UNKNOWN_KEYS)
            .join(error.getMdrKey());
        log(EventMessageType.E_LDM_ERROR, "code:" + error.getErrorCode(), "keys:"
            + unknownKeys);
        spawnNewInquiryExecutionJob(unknownKeys);
        jobExecutionContext.setResult(new CheckInquiryStatusJobResult(true,
            false));
        unscheduleThisJob(jobExecutionContext);
        break;
      default:
        break;
    }
    return false;
  }

  void unscheduleThisJob(JobExecutionContext jobExecutionContext) throws SchedulerException {
    Trigger currentTrigger = jobExecutionContext.getTrigger();
    ApplicationBean.getScheduler().unscheduleJob(currentTrigger.getKey());
  }

  /**
   * Replace the current trigger for this job with a new one, since the timings may differ.
   *
   * @param jobExecutionContext the jobExecutionContext of this job instance
   */
  private void rescheduleCheckingForResults(JobExecutionContext jobExecutionContext)
      throws SchedulerException {
    // Replace trigger with differently timed trigger
    int retryAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
        EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
    int retryInterval = ConfigurationUtil.getConfigurationTimingsElementValue(
        EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
    int initialDelay = ConfigurationUtil.getConfigurationTimingsElementValue(
        EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_INITIAL_DELAY_SECONDS);

    Trigger oldTrigger = ApplicationBean.getScheduler()
        .getTrigger(jobExecutionContext.getTrigger().getKey());
    Trigger newTrigger = TriggerBuilder.newTrigger()
        .withIdentity(jobExecutionContext.getTrigger().getKey())
        .startAt(DateBuilder.futureDate(initialDelay, DateBuilder.IntervalUnit.SECOND))
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(retryInterval)
            .withRepeatCount(retryAttempts)
        )
        .usingJobData(oldTrigger.getJobDataMap())
        .build();
    // ...and continue with execution
    jobExecutionContext.setResult(new CheckInquiryStatusJobResult(true,
        false));
    ApplicationBean.getScheduler().rescheduleJob(oldTrigger.getKey(), newTrigger);
  }

  /**
   * Create and schedule a new ExecuteInquiryJob with keys to ignore.
   *
   * @param unknownKeys a joined list of keys, the local datamanagement does not understand. The new
   *                    ExecuteInquiryJob will remove them and re-post it
   */
  private void spawnNewInquiryExecutionJob(String unknownKeys) {
    try {
      // Fill the JobDataMap
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_ID, inquiryDetails.getInquiryId());
      jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_DETAILS_ID, inquiryDetails.getId());
      jobDataMap.put(ExecuteInquiryJobParams.UNKNOWN_KEYS, unknownKeys);
      jobDataMap.put(ExecuteInquiryJobParams.STATS_ONLY, inquiryResult.getStatisticsOnly());
      jobDataMap.put(ExecuteInquiryJobParams.IS_UPLOAD, jobParams.isUpload());

      // Fire exactly once - right now
      JobKey jobKey = JobKey
          .jobKey(ExecuteInquiryJobParams.getJobName(), ExecuteInquiryJobParams.JOBGROUP);
      ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
    } catch (SchedulerException e) {
      logger.error("Error spawning Inquiry Execution Job");
    }
  }

  /**
   * Write a message, linked with the inquiry, to the event log.
   *
   * @param message the message to log
   */
  private void log(String message) {
    if (jobParams.isUpload()) {
      try {
        Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
        EventLogUtil.insertEventLogEntryForUploadId(message, inquiry.getUploadId());
      } catch (NullPointerException npe) {
        logger.debug(
            "Nullpointer exception caught while trying to insert EventLogMessage for upload.");
      }
    } else {
      EventLogUtil.insertEventLogEntryForInquiryId(message, inquiryDetails.getInquiryId());
    }
  }

  /**
   * Write a message, linked with the inquiry, to the event log.
   *
   * @param messageType pre-defined event type
   * @param params      parameters that will be substituted via resource bundle and messageformat
   */
  private void log(EventMessageType messageType, String... params) {
    if (jobParams.isUpload()) {
      try {
        Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
        EventLogUtil.insertEventLogEntryForUploadId(messageType, inquiry.getUploadId(), params);
      } catch (NullPointerException npe) {
        logger.debug(
            "Nullpointer exception caught while trying to insert EventLogMessage for upload.");
      }
    } else {
      EventLogUtil
          .insertEventLogEntryForInquiryId(messageType, inquiryDetails.getInquiryId(), params);
    }
  }
}
