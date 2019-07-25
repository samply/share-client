package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.CheckInquiryStatusReadyForMultipleCriteriaJobParams;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

public class CheckInquiryStatusReadyForMultipleCriteriaJob implements Job {

    private static final Logger logger = LogManager.getLogger(CheckInquiryStatusReadyForMultipleCriteriaJob.class);

    private static final int DELAY_RESCHEDULING = 10;
    private static final int DELAY_FIRST = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private CheckInquiryStatusReadyForMultipleCriteriaJobParams jobParams;
    private InquiryDetails inquiryDetails;

    static void spawnNewJob(InquiryDetails inquiryDetails) {
        spawnNewJob(inquiryDetails, DELAY_FIRST);
    }

    private static void spawnNewJob(InquiryDetails inquiryDetails, int initialDelay) {
        try {
            Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());

            JobKey jobKey = JobKey.jobKey(
                    CheckInquiryStatusReadyForMultipleCriteriaJobParams.JOBNAME,
                    CheckInquiryStatusReadyForMultipleCriteriaJobParams.JOBGROUP);

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(CheckInquiryStatusReadyForMultipleCriteriaJobParams.INQUIRY_DETAILS_ID, inquiryDetails.getId());

            logger.info("Give CheckInquiryStatusReadyForMultipleCriteria Job to scheduler for inquiry with id " + inquiry.getId());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(DateBuilder.futureDate(initialDelay, DateBuilder.IntervalUnit.SECOND))
                    .forJob(jobKey)
                    .usingJobData(jobDataMap)
                    .build();

            ApplicationBean.getScheduler().scheduleJob(trigger);
        } catch (SchedulerException e) {
            logger.error("Error spawning CheckInquiryStatusReadyForMultipleCriteria Job", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (inquiryDetails.getStatus() == InquiryStatusType.IS_READY) {
            return;
        }

        prepareExecute(jobExecutionContext);

        for (InquiryCriteria inquiryCriteria : InquiryCriteriaUtil.getInquiryCriteriaForInquiryDetails(inquiryDetails)) {
            if (inquiryCriteria.getStatus() != InquiryCriteriaStatusType.ICS_READY) {
                spawnNewJob(inquiryDetails, DELAY_RESCHEDULING);
                return;
            }
        }

        inquiryDetails.setStatus(InquiryStatusType.IS_READY);
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
    }

    private void prepareExecute(JobExecutionContext jobExecutionContext) {
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        jobParams = new CheckInquiryStatusReadyForMultipleCriteriaJobParams(dataMap);
        logger.debug(jobKey.toString() + " " + jobParams);

        inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(jobParams.getInquiryDetailsId());
    }

}
