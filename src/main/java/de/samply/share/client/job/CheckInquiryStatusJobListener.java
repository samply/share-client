package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.CheckInquiryStatusJobResult;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UploadUtil;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * This listener will be called when an instance of the CheckInquiryStatusJob is done. If the job
 * will NOT fire again, and the status is still processing...set it to abandoned. If the job was
 * connected to an upload, also set the upload to abandoned.
 */
public class CheckInquiryStatusJobListener implements JobListener {

  private static final Logger logger = LogManager.getLogger(CheckInquiryStatusJobListener.class);

  private final String name;

  CheckInquiryStatusJobListener(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
    // No need for something to be done here
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
    logger.info("job execution was vetoed");
  }

  @Override
  public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
    logger.debug("Job was executed. Context: " + jobExecutionContext);
    if (e != null) {
      logger.debug(" - Exception: " + e);
    }
    CheckInquiryStatusJobResult result;
    Object resultObject = jobExecutionContext.getResult();
    if (resultObject != null) {
      try {
        result = (CheckInquiryStatusJobResult) resultObject;
      } catch (ClassCastException cce) {
        logger.error("Error getting CheckInquiryStatusJob Result");
        result = new CheckInquiryStatusJobResult(false, false);
      }
    } else {
      result = new CheckInquiryStatusJobResult(false, false);
    }

    if (result.isRescheduled()) {
      logger.trace("Job is rescheduled. Don't set anything to abandoned...");
      return;
    } else {
      logger.trace("Job is NOT rescheduled...check if something is abandoned...");
    }

    if (result.isResetStatusFlags()) {
      // In any case, reset the jobDataMap parameters for stats and result done to false
      jobExecutionContext.getJobDetail().getJobDataMap()
          .put(CheckInquiryStatusJobParams.STATS_DONE, false);
      jobExecutionContext.getJobDetail().getJobDataMap()
          .put(CheckInquiryStatusJobParams.RESULT_STARTED, false);
      jobExecutionContext.getJobDetail().getJobDataMap()
          .put(CheckInquiryStatusJobParams.RESULT_DONE, false);
    }

    JobKey key = jobExecutionContext.getJobDetail().getKey();
    JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

    CheckInquiryStatusJobParams jobParams = new CheckInquiryStatusJobParams(dataMap);
    InquiryResult inquiryResult = InquiryResultUtil
        .fetchInquiryResultById(jobParams.getInquiryResultId());
    InquiryDetails inquiryDetails = InquiryDetailsUtil
        .fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());

    // If the status of the inquiry is still "processing" when the check-job is done, set the status
    // to abandoned
    if (inquiryDetails.getStatus() == InquiryStatusType.IS_PROCESSING && !willFireAgain(key)) {
      logger.info(
          "Setting status to ABANDONED for inquiry details with id " + inquiryDetails.getId());
      Utils.setStatus(inquiryDetails, InquiryStatusType.IS_ABANDONED);
      InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
      EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_STATUS_CHECK_ABANDONED,
          inquiryDetails.getInquiryId());
      // If it was an upload, also set this status to abandoned
      try {
        Integer uploadId = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId())
            .getUploadId();
        if (uploadId != null) {
          UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_ABANDONED);
        }
      } catch (Exception ex) {
        logger.error("Exception caught while trying to update upload status", ex);
      }
    }

  }


  /**
   * Check if the trigger for this job will fire again.
   *
   * @param jobKey the jobkey of the finished CheckInquiryStatusJob
   * @return true if the connected trigger will fire again, false otherwise
   */
  @SuppressWarnings("unchecked")
  private boolean willFireAgain(JobKey jobKey) {
    try {
      List<Trigger> triggers = (List<Trigger>) ApplicationBean.getScheduler()
          .getTriggersOfJob(jobKey);
      for (Trigger trigger : triggers) {
        if (trigger.getNextFireTime() != null) {
          return true;
        }
      }
    } catch (SchedulerException e) {
      logger.warn("Scheduler Exception caught", e);
    }
    return false;
  }
}
