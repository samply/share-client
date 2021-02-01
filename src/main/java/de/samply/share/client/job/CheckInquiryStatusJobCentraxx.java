package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.CheckInquiryStatusJobResult;
import de.samply.share.client.job.params.GenerateInquiryResultStatsJobParams;
import de.samply.share.client.job.params.UploadJobParams;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.Upload;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnectorCcp;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UploadUtil;
import de.samply.share.model.common.QueryResultStatistic;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerException;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJobCentraxx extends AbstractCheckInquiryStatusJob<LdmConnectorCcp> {

  private static final Logger logger = LogManager.getLogger(CheckInquiryStatusJobCentraxx.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    prepareExecute(jobExecutionContext);

    if (!jobParams.isStatsDone()) {
      logger.debug("Stats were not available before. Checking again.");
      checkForStatsResult(jobExecutionContext);
    } else if (!jobParams.isResultStarted()) {
      logger.debug("Stats are available, first result file was not available. Checking again.");
      checkForFirstResultPage(jobExecutionContext);
    } else if (!jobParams.isResultDone()) {
      logger.debug("First result file available, last one not yet. Checking again.");
      checkForLastResultPage(jobExecutionContext);
    }
  }

  boolean applyReplyRulesImmediately(boolean isStats) {
    return isStats && jobParams.isStatsOnly();
  }

  InquiryCriteria getInquiryCriteria() {
    return InquiryCriteriaUtil.getFirstCriteriaOriginal(inquiryDetails, QueryLanguageType.QL_QUERY);
  }

  private void checkForFirstResultPage(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    try {
      if (ldmConnector.isFirstResultPageAvailable(inquiryResult.getLocation())) {
        jobExecutionContext.getJobDetail().getJobDataMap()
            .put(CheckInquiryStatusJobParams.STATS_DONE, true);
        jobExecutionContext.getJobDetail().getJobDataMap()
            .put(CheckInquiryStatusJobParams.RESULT_STARTED, true);
      }
    } catch (LdmConnectorException e) {
      throw new JobExecutionException(e);
    }
  }

  private void checkForLastResultPage(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    try {

      String location = getLocation(inquiryResult);
      QueryResultStatistic queryResultStatistic = getQueryResultStatistic(location);

      if (ldmConnector.isResultDone(location, queryResultStatistic)) {
        jobExecutionContext.getJobDetail().getJobDataMap()
            .put(CheckInquiryStatusJobParams.RESULT_DONE, true);
        if (!jobParams.isUpload()) {
          logger.debug("Spawn generate stats job");
          spawnGenerateStatsJob();
        }

        Utils.setStatus(inquiryDetails, InquiryStatusType.IS_READY);
        inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_READY);
        updateInquiry(queryResultStatistic);

        // If the inquiry belongs to an upload, also update the upload status
        try {
          Integer uploadId = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId())
              .getUploadId();
          if (jobParams.isUpload() && uploadId != null) {
            UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_QUERY_READY);
            spawnUploadToCentralMdsDbJob(uploadId);
          }
        } catch (Exception e) {
          logger.error("Exception caught while trying to update upload status", e);
        }
        jobExecutionContext.setResult(new CheckInquiryStatusJobResult(false, true));
        unscheduleThisJob(jobExecutionContext);
        logger.info("CheckInquiryStatusJob completed for inquiry " + inquiryDetails.getInquiryId());
        // TODO: Check if the handling for uploads would be better in the following method
        processReplyRules();
      }
    } catch (LdmConnectorException | SchedulerException e) {
      throw new JobExecutionException(e);
    }
  }

  private String getLocation(InquiryResult inquiryResult) {

    try {

      return (inquiryResult != null) ? inquiryResult.getLocation() : null;

    } catch (Exception e) {

      logger.debug("Location not found for inquiry result " + inquiryResult.getId());
      return null;

    }

  }

  private QueryResultStatistic getQueryResultStatistic(String location) {

    try {

      return (location != null) ? ldmConnector.getQueryResultStatistic(location) : null;

    } catch (Exception e) {

      logger.debug("Exception getting query result statistic");
      return null;
    }
  }

  private void updateInquiry(QueryResultStatistic queryResultStatistic) {

    InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
    InquiryCriteriaUtil.updateInquiryCriteria(inquiryCriteria);
    updateInquiryResults(queryResultStatistic);

  }

  private void updateInquiryResults(QueryResultStatistic queryResultStatistic) {

    if (queryResultStatistic != null) {
      int totalSize = queryResultStatistic.getTotalSize();
      inquiryResult.setSize(totalSize);
      InquiryResultUtil.updateInquiryResult(inquiryResult);
    }

  }

  void handleInquiryStatusReady() {
    Utils.setStatus(inquiryDetails, InquiryStatusType.IS_READY);
  }

  /**
   * (Re-)spawn an upload job.
   *
   * @param uploadId the database id of the upload
   */
  private void spawnUploadToCentralMdsDbJob(int uploadId) {
    try {
      Upload upload = UploadUtil.fetchUploadById(uploadId);
      String jobName =
          upload.getDktkFlagged() ? UploadJobParams.JOBNAME_DKTK : UploadJobParams.JOBNAME_NO_DKTK;

      // Fill the JobDataMap
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(UploadJobParams.UPLOAD_ID, uploadId);
      jobDataMap.put(UploadJobParams.STATUS, UploadStatusType.US_QUERY_READY.getLiteral());
      jobDataMap.put(UploadJobParams.DKTK_FLAGGED, upload.getDktkFlagged());

      // Fire exactly once - right now
      JobKey newJobKey = JobKey.jobKey(jobName, UploadJobParams.JOBGROUP);
      ApplicationBean.getScheduler().triggerJob(newJobKey, jobDataMap);
    } catch (SchedulerException e) {
      logger.error("Error spawning Inquiry Execution Job");
    }

  }

  /**
   * Spawn a job to generate the statistics for the query result.
   */
  private void spawnGenerateStatsJob() {
    try {
      JobKey jobKey = JobKey.jobKey(GenerateInquiryResultStatsJobParams.JOBNAME,
          GenerateInquiryResultStatsJobParams.JOBGROUP);
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(GenerateInquiryResultStatsJobParams.INQUIRY_RESULT_ID, inquiryResult.getId());

      // Fire exactly once - right now
      ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
    } catch (SchedulerException e) {
      logger.error("Error spawning Generate Result Stats Job", e);
    }
  }

  @Override
  Consumer<BrokerConnector> getProcessReplyRuleMethod() {
    return brokerConnector -> {
      try {
        brokerConnector.reply(inquiryDetails, inquiryResult.getSize());
      } catch (BrokerConnectorException e) {
        handleBrokerConnectorException(e);
      }
    };
  }
}
