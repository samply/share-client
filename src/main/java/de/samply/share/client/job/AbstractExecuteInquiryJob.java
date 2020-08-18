package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.InquiryUtils;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.db.*;
import de.samply.share.common.model.uiquerybuilder.QueryItem;
import de.samply.share.common.utils.QueryTreeUtil;
import de.samply.share.common.utils.QueryValidator;
import de.samply.share.model.common.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.model.tree.TreeModel;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;

import java.sql.Timestamp;
import java.util.Date;

import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_LDM_ERROR;

/**
 * This Job posts an inquiry to the local datamanagement, stores the location and spawns a CheckInquiryStatusJob
 * <p>
 * It is defined and scheduled by either the CheckInquiryStatusJob, the CollectInquiriesJob, the UploadToCentralMdsDbJob
 * or can be spawned user-triggered from the show_inquiry.xhtml page
 */
public abstract class AbstractExecuteInquiryJob<T_LDM_CONNECTOR extends LdmConnector> implements Job {

    ExecuteInquiryJobParams jobParams;
    T_LDM_CONNECTOR ldmConnector;
    Inquiry inquiry;
    InquiryDetails inquiryDetails;

    private static final Logger logger = LogManager.getLogger(AbstractExecuteInquiryJob.class);

    AbstractExecuteInquiryJob() {
        //noinspection unchecked
        this.ldmConnector = (T_LDM_CONNECTOR) ApplicationBean.getLdmConnector();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (CredentialsUtil.getCredentialsByTarget(TargetType.TT_LDM).isEmpty()) {
            logger.warn("No credentials for target type '" + TargetType.TT_LDM + "' found. " +
                    "Ignore job '" + getClass().getSimpleName() + "'");
            return;
        }

        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

        jobParams = new ExecuteInquiryJobParams(dataMap);
        logger.debug(jobParams);
        inquiry = InquiryUtil.fetchInquiryById(jobParams.getInquiryId());
        inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(jobParams.getInquiryDetailsId());

        execute();
    }

    abstract void execute() throws JobExecutionException;

    /**
     * Change the status of the inquiry
     *
     * @param status the new inquiry status
     */
    void setInquiryDetailsStatusAndUpdateInquiryDetails(InquiryStatusType status) {
        Utils.setStatus(inquiryDetails, status);
        inquiryDetails.setScheduledAt(new Timestamp(new Date().getTime()));
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
        if (status.equals(IS_LDM_ERROR)) {
            new InquiryUtils().changeStatusOfInquiryResultToError(inquiryDetails);
        }
    }

    /**
     * Spawn a new CheckInquiryStatusJob when the inquiry is posted to the local datamanagement
     *
     * @param inquiryResultId the database id of the inquiry result entry
     */
    void spawnNewCheckInquiryStatusJob(int inquiryResultId, String entityType) {
        try {
            JobKey jobKey = JobKey.jobKey(CheckInquiryStatusJobParams.getJobName(), CheckInquiryStatusJobParams.JOBGROUP);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(CheckInquiryStatusJobParams.INQUIRY_RESULT_ID, inquiryResultId);
            jobDataMap.put(CheckInquiryStatusJobParams.IS_UPLOAD, jobParams.isUpload());
            jobDataMap.put(CheckInquiryStatusJobParams.STATS_ONLY, jobParams.isStatsOnly());
            jobDataMap.put(CheckInquiryStatusJobParams.ENTITY_TYPE, entityType);

            /* Define a trigger that starts after the defined amount of seconds, and repeats a defined number of times (configuration done in database)  */
            int retryAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_ATTEMPTS);
            int retryInterval = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_INTERVAL_SECONDS);
            int initialDelay = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_INITIAL_DELAY_SECONDS);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(DateBuilder.futureDate(initialDelay, DateBuilder.IntervalUnit.SECOND))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(retryInterval)
                            .withRepeatCount(retryAttempts)
                    )
                    .forJob(jobKey)
                    .usingJobData(jobDataMap)
                    .build();

            ApplicationBean.getScheduler().scheduleJob(trigger);
            ApplicationBean.getScheduler().getListenerManager().addJobListener(
                    new CheckInquiryStatusJobListener(jobKey.getGroup() + "_listener"), KeyMatcher.keyEquals(jobKey));
        } catch (SchedulerException e) {
            logger.error("Error spawning Check Inquiry Status Job", e);
        }
    }

    /**
     * Create and inquiry result entry in the database
     *
     * @param resultLocation the url where the result can be found
     * @return the database id of the result
     */
    int createNewInquiryResult(String resultLocation, int inquiryCriteriaId) {
        InquiryResult inquiryResult = new InquiryResult();
        inquiryResult.setInquiryDetailsId(inquiryDetails.getId());
        inquiryResult.setStatisticsOnly(jobParams.isStatsOnly());
        inquiryResult.setLocation(resultLocation);
        inquiryResult.setInquiryCriteriaId(inquiryCriteriaId);
        return InquiryResultUtil.insertInquiryResult(inquiryResult);
    }

    void log(EventMessageType messageType, String... params) {
        if (jobParams.isUpload() && inquiry.getUploadId() != null) {
            EventLogUtil.insertEventLogEntryForUploadId(messageType, inquiry.getUploadId(), params);
        } else {
            EventLogUtil.insertEventLogEntryForInquiryId(messageType, jobParams.getInquiryId(), params);
        }
    }

    /**
     * Reformat date entries from the standard mdr-defined format to the format that is written to the JAVA_DATE_FORMAT
     * slot
     *
     * @param sourceQuery the query to check
     * @return the fixed query
     */
    Query fixDateIssues(Query sourceQuery) {
        // Check if the date format entry in the slot differs (TODO: remove when not needed any more. as of now this should just concern dataelement 83:*)
        QueryValidator queryValidator = new QueryValidator(ApplicationBean.getMdrClient());
        TreeModel<QueryItem> queryTree = QueryTreeUtil.queryToTree(sourceQuery);
        queryValidator.reformatDateToSlotFormat(queryTree);
        return QueryTreeUtil.treeToQuery(queryTree);
    }
}
