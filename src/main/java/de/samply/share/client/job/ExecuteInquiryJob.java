package de.samply.share.client.job;

import de.samply.common.ldmclient.centraxx.LdmClientCentraxx;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.*;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.Replace;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.common.model.uiquerybuilder.QueryItem;
import de.samply.share.common.utils.QueryTreeUtil;
import de.samply.share.common.utils.QueryValidator;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Query;
import de.samply.share.utils.QueryConverter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.model.tree.TreeModel;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;

import javax.xml.bind.JAXBException;
import java.util.List;

import static de.samply.share.client.model.db.enums.InquiryStatusType.*;

/**
 * This Job posts an inquiry to the local datamanagement, stores the location and spawns a CheckInquiryStatusJob
 * <p>
 * It is defined and scheduled by either the CheckInquiryStatusJob, the CollectInquiriesJob, the UploadToCentralMdsDbJob
 * or can be spawned user-triggered from the show_inquiry.xhtml page
 */
public class ExecuteInquiryJob implements Job {

    private ExecuteInquiryJobParams jobParams;
    private LdmConnector ldmConnector;
    private Inquiry inquiry;
    private InquiryDetails inquiryDetails;
    private InquiryCriteria inquiryCriteria;

    private static final Logger logger = LogManager.getLogger(ExecuteInquiryJob.class);

    public ExecuteInquiryJob() {
        this.ldmConnector = ApplicationBean.getLdmConnector();
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
        inquiryCriteria = InquiryCriteriaUtil.getFirstCriteriaOriginal(inquiryDetails, QueryLanguageType.QUERY);
        List<String> unknownKeys = jobParams.getUnknownKeys();

        String resultLocation;

        try {
            setInquiryDetailsStatus(IS_PROCESSING);
            Query modifiedQuery = null;
            Query originalQuery = QueryConverter.xmlToQuery(inquiryCriteria.getCriteriaOriginal());

            // TODO remove this "temporary" workaround as soon as possible! This is linked with the age-old issue of different java date formats in some mdr elements!
            originalQuery = fixDateIssues(originalQuery);

            if (!SamplyShareUtils.isNullOrEmpty(unknownKeys)) {
                log(EventMessageType.E_REPEAT_EXECUTE_INQUIRY_JOB_WITHOUT_UNKNOWN_KEYS, unknownKeys.toArray(new String[0]));
                modifiedQuery = QueryConverter.removeAttributesFromQuery(originalQuery, unknownKeys);
                inquiryCriteria.setCriteriaModified(QueryConverter.queryToXml(modifiedQuery));
                InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
            }

            // to search the aggregated field
            if (ApplicationUtils.isDktk()) {
                inquiryCriteria.setCriteriaOriginal(Replace.replaceMDRKey(inquiryCriteria.getCriteriaOriginal()));
                originalQuery = QueryConverter.xmlToQuery(inquiryCriteria.getCriteriaOriginal());
                // TODO remove this "temporary" workaround as soon as possible! This is linked with the age-old issue of different java date formats in some mdr elements!
                originalQuery = fixDateIssues(originalQuery);
                if (!SamplyShareUtils.isNullOrEmpty(unknownKeys)) {
                    log(EventMessageType.E_REPEAT_EXECUTE_INQUIRY_JOB_WITHOUT_UNKNOWN_KEYS, unknownKeys.toArray(new String[0]));
                    modifiedQuery = QueryConverter.removeAttributesFromQuery(originalQuery, unknownKeys);
                    inquiryCriteria.setCriteriaModified(QueryConverter.queryToXml(modifiedQuery));
                }
            }

            log(EventMessageType.E_START_EXECUTE_INQUIRY_JOB);
            Query query = ObjectUtils.defaultIfNull(modifiedQuery, originalQuery);
            resultLocation = ldmConnector.postQuery(query, unknownKeys, true, jobParams.isStatsOnly(), !jobParams.isUpload());

            if (resultLocation != null && resultLocation.length() > 0) {
                log(EventMessageType.E_INQUIRY_RESULT_AT, resultLocation);
                int inquiryResultId = createNewInquiryResult(resultLocation);
                spawnNewCheckInquiryStatusJob(inquiryResultId);
            } else {
                log(EventMessageType.E_RESULT_NOT_SET_ABORTING);
                setInquiryDetailsStatus(IS_LDM_ERROR);
            }
        } catch (JAXBException e) {
            log(EventMessageType.E_FAILED_JAXB_ERROR, e.getMessage());
            setInquiryDetailsStatus(IS_ABANDONED);
            throw new JobExecutionException(e);
        } catch (LDMConnectorException e) {
            log(EventMessageType.E_RESULT_NOT_SET_ABORTING);
            setInquiryDetailsStatus(IS_LDM_ERROR);
            throw new JobExecutionException(e);
        }
    }

    /**
     * Spawn a new CheckInquiryStatusJob when the inquiry is posted to the local datamanagement
     *
     * @param inquiryResultId the database id of the inquiry result entry
     */
    private void spawnNewCheckInquiryStatusJob(int inquiryResultId) {
        try {
            JobKey jobKey = JobKey.jobKey(CheckInquiryStatusJobParams.JOBNAME, CheckInquiryStatusJobParams.JOBGROUP);
            TriggerKey triggerKey = TriggerKey.triggerKey(CheckInquiryStatusJobParams.TRIGGERNAME, CheckInquiryStatusJobParams.JOBGROUP);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(CheckInquiryStatusJobParams.INQUIRY_RESULT_ID, inquiryResultId);
            jobDataMap.put(CheckInquiryStatusJobParams.IS_UPLOAD, jobParams.isUpload());
            jobDataMap.put(CheckInquiryStatusJobParams.STATS_ONLY, jobParams.isStatsOnly());

            /* Define a trigger that starts after the defined amount of seconds, and repeats a defined number of times (configuration done in database)  */
            int retryAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_ATTEMPTS);
            int retryInterval = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_INTERVAL_SECONDS);
            int initialDelay = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_INITIAL_DELAY_SECONDS);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
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
     * Change the status of the inquiry
     *
     * @param status the new inquiry status
     */
    private void setInquiryDetailsStatus(InquiryStatusType status) {
        inquiryDetails.setStatus(status);
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);

        inquiryCriteria.setStatus(getCriteriaStatus(status));
        InquiryCriteriaUtil.updateInquiryCriteria(inquiryCriteria);

        if (status.equals(IS_LDM_ERROR)) {
            changeStatusOfInquiryResultToError();
        }
    }

    private InquiryCriteriaStatusType getCriteriaStatus(InquiryStatusType status) {
        switch (status) {
            case IS_NEW:
                return InquiryCriteriaStatusType.IS_NEW;
            case IS_PROCESSING:
                return InquiryCriteriaStatusType.IS_PROCESSING;
            case IS_READY:
                return InquiryCriteriaStatusType.IS_READY;
            case IS_LDM_ERROR:
                return InquiryCriteriaStatusType.IS_LDM_ERROR;
            case IS_ABANDONED:
                return InquiryCriteriaStatusType.IS_ABANDONED;

            default:
                return InquiryCriteriaStatusType.IS_LDM_ERROR;
        }
    }

    private void changeStatusOfInquiryResultToError() {
        InquiryResult inquiryResult = new InquiryResult();
        inquiryResult.setErrorCode(Integer.toString(LdmClientCentraxx.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE));
        inquiryResult.setExecutedAt(SamplyShareUtils.getCurrentSqlTimestamp());
        inquiryResult.setInquiryDetailsId(inquiryDetails.getId());
        inquiryResult.setIsError(true);
        InquiryResultUtil.insertInquiryResult(inquiryResult);
    }

    /**
     * Create and inquiry result entry in the database
     *
     * @param resultLocation the url where the result can be found
     * @return the database id of the result
     */
    private int createNewInquiryResult(String resultLocation) {
        InquiryResult inquiryResult = new InquiryResult();
        inquiryResult.setInquiryDetailsId(inquiryDetails.getId());
        inquiryResult.setStatisticsOnly(jobParams.isStatsOnly());
        inquiryResult.setLocation(resultLocation);
        return InquiryResultUtil.insertInquiryResult(inquiryResult);
    }

    /**
     * Reformat date entries from the standard mdr-defined format to the format that is written to the JAVA_DATE_FORMAT
     * slot
     *
     * @param sourceQuery the query to check
     * @return the fixed query
     */
    private Query fixDateIssues(Query sourceQuery) {
        // Check if the date format entry in the slot differs (TODO: remove when not needed any more. as of now this should just concern dataelement 83:*)
        QueryValidator queryValidator = new QueryValidator(ApplicationBean.getMdrClient());
        TreeModel<QueryItem> queryTree = QueryTreeUtil.queryToTree(sourceQuery);
        queryValidator.reformatDateToSlotFormat(queryTree);
        return QueryTreeUtil.treeToQuery(queryTree);
    }

    private void log(EventMessageType messageType, String... params) {
        if (jobParams.isUpload() && inquiry.getUploadId() != null) {
            EventLogUtil.insertEventLogEntryForUploadId(messageType, inquiry.getUploadId(), params);
        } else {
            EventLogUtil.insertEventLogEntryForInquiryId(messageType, jobParams.getInquiryId(), params);
        }
    }
}
