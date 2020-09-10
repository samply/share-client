package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckScheduledInquiriesJob implements Job {

    private static final Logger logger = LogManager.getLogger(CheckScheduledInquiriesJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("checking processing inquiries");
        checkProcessingInquiry();
    }

    /**
     * check the schedule time of processing inquiries and send a error message to the broker if the inquiry took too much time to be executed
     *
     * @throws JobExecutionException
     */
    private void checkProcessingInquiry() throws JobExecutionException {
        try {
            List<InquiryDetails> inquiryDetailsList = InquiryDetailsUtil.getInquiryDetailsByStatus(InquiryStatusType.IS_PROCESSING);
            for (InquiryDetails inquiryDetails : inquiryDetailsList) {
                if (checkScheduledAt(inquiryDetails)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    abandonedInquiryCriteria(inquiryDetails);
                    abandonedInquiryDetails(inquiryDetails);
                    Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
                    stringBuilder.append("Inquiry with ID ").append(inquiry.getId()).append("was abandoned because the execution time was too long. ");
                    logger.warn(stringBuilder.toString());
                }
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    /**
     * Set the status of the inquiry detail to abandoned
     *
     * @param inquiryDetails the inquiry detail which status has to been changed
     */
    private void abandonedInquiryDetails(InquiryDetails inquiryDetails) throws SchedulerException {
        InquiryDetails inquiryDetailsTmp = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryDetails.getId());
        inquiryDetailsTmp.setStatus(InquiryStatusType.IS_ABANDONED);
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetailsTmp);
        unscheduleJob(inquiryDetails.getInquiryId());
    }

    /**
     * unschedule the inquiry execution job
     *
     * @param inquiryId the inquiry whose job should be unscheduled
     * @throws SchedulerException
     */
    private void unscheduleJob(int inquiryId) throws SchedulerException {
        for (JobExecutionContext jobExecutionContext : ApplicationBean.getScheduler().getCurrentlyExecutingJobs()) {
            if (jobExecutionContext.getMergedJobDataMap().get(ExecuteInquiryJobParams.INQUIRY_ID).equals(inquiryId)) {
                ApplicationBean.getScheduler().unscheduleJob(jobExecutionContext.getRecoveringTriggerKey());
            }
        }
    }

    /**
     * Set the status of the inquiry criterias to abandoned
     *
     * @param inquiryDetails the inquiry detail whose inquiry criterias status has to been changed
     */
    private void abandonedInquiryCriteria(InquiryDetails inquiryDetails) {
        List<InquiryCriteria> inquiryCriteriaList = InquiryCriteriaUtil.getInquiryCriteriaForInquiryDetails(inquiryDetails);
        for (InquiryCriteria inquiryCriteria : inquiryCriteriaList) {
            inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_ABANDONED);
        }
        InquiryCriteriaUtil.updateInquiryCriteria(inquiryCriteriaList);
    }

    /**
     * Check if the processing inquiry is running longer than a certain time
     *
     * @param inquiryDetails the inquiry detail which has to been checked
     * @return if the processing inquiry is running to long
     */
    private boolean checkScheduledAt(InquiryDetails inquiryDetails) {
        int minutes = ConfigurationUtil.getConfigurationTimingsElement(EnumConfigurationTimings.JOB_CHECK_SCHEDULED_INQUIRY_INTERVAL_MINUTES).getSetting();
        Timestamp scheduledAt = inquiryDetails.getScheduledAt();
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(minutes);
        Timestamp maxTime = Timestamp.valueOf(fiveMinutesAgo);
        return maxTime.after(scheduledAt);
    }

}
