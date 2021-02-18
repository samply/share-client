package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryHandlingRuleUtil;
import de.samply.share.client.util.db.InquiryUtil;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * This Job checks the database for new jobs and gives them to an execution handler one by one. It
 * is defined and scheduled in the quartz-jobs.xml. The basic steps it performs are: 1) Get the list
 * of new inquiries 2) If there is a new inquiry, and none still processing...spawn an inquiry
 * execution task for the new one.
 */
@DisallowConcurrentExecution
public class ExecuteInquiriesJob implements Job {

  private static final Logger logger = LogManager.getLogger(ExecuteInquiriesJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    if (!InquiryDetailsUtil.getInquiryDetailsByStatus(InquiryStatusType.IS_PROCESSING).isEmpty()) {
      return;
    }

    List<InquiryDetails> inquiryDetailsList = InquiryDetailsUtil
        .getInquiryDetailsByStatus(InquiryStatusType.IS_NEW);
    if (inquiryDetailsList.isEmpty()) {
      return;
    }

    InquiryDetails inquiryDetails = inquiryDetailsList.get(0);
    spawnNewInquiryExecutionJob(inquiryDetails);
  }

  private void spawnNewInquiryExecutionJob(InquiryDetails inquiryDetails) {
    Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
    boolean statsOnly = !InquiryHandlingRuleUtil.requestResultsForInquiry(inquiry);
    spawnNewInquiryExecutionJob(inquiry, inquiryDetails, statsOnly);
  }

  /**
   * Hand over the inquiry to an ExecuteInquiryJob.
   *
   * @param inquiry        the inquiry to delegate to the execute job
   * @param inquiryDetails the corresponding inquiry details object
   * @param statsOnly      set to true if only statistics are requested and no whole result set
   *                       (list of patients)
   */
  private void spawnNewInquiryExecutionJob(
      de.samply.share.client.model.db.tables.pojos.Inquiry inquiry, InquiryDetails inquiryDetails,
      boolean statsOnly) {
    try {

      // Fill the JobDataMap for the trigger
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_ID, inquiry.getId());
      jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_DETAILS_ID, inquiryDetails.getId());
      jobDataMap.put(ExecuteInquiryJobParams.STATS_ONLY, statsOnly);
      jobDataMap.put(ExecuteInquiryJobParams.IS_UPLOAD, (inquiry.getUploadId() != null));

      // Fire exactly once - right now
      JobKey jobKey = JobKey
          .jobKey(ExecuteInquiryJobParams.getJobName(), ExecuteInquiryJobParams.JOBGROUP);
      logger.info("Give Execute Job to scheduler for inquiry with id " + inquiry.getId());
      ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
    } catch (SchedulerException e) {
      logger.error("Error spawning Inquiry Execution Job", e);
    }
  }
}
