package de.samply.share.client.job;

import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UploadUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * This listener will be called when an instance of the ExecuteInquiryStatusJob is done. It checks
 * if an Exception was thrown and sets the corresponding object state to cancelled/aborted.
 */
public class ExecuteInquiryStatusJobListener implements JobListener {

  private static final Logger logger = LogManager.getLogger(ExecuteInquiryStatusJobListener.class);

  private final String name;

  public ExecuteInquiryStatusJobListener(String name) {
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
    // Don't do anything if there was no exception
    if (e == null) {
      return;
    }
    logger.debug(
        "Job execution ended with an exception. Context: " + jobExecutionContext + " - exception "
            + e);

    JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

    ExecuteInquiryJobParams jobParams = new ExecuteInquiryJobParams(dataMap);
    Inquiry inquiry = InquiryUtil.fetchInquiryById(jobParams.getInquiryId());

    try {
      Integer uploadId = inquiry.getUploadId();
      if (uploadId != null) {
        UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_ABANDONED);
      }
    } catch (Exception ex) {
      logger.error("Exception caught while trying to update upload status", ex);
    }
  }
}
