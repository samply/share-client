/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.job;

import com.google.common.base.Joiner;
import de.samply.common.ldmclient.LdmClient;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.*;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.ReplyRuleType;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.*;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.QueryResultStatistic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.List;

/**
 * This Job checks the status of the given inquiry and spawns new jobs if necessary
 *
 * It is defined and scheduled by the ExecuteInquiryJob
 *
 * The performed action depends on the previous state of the Inquiry
 *
 * 1) If the stats were not available earlier, check if they are.
 *  a) If they are not, the job terminates this iteration and wait to be called again at the scheduled time
 *  b) If they are, and it was an error...either quit and delete the trigger if the problem can not be solved or try to
 *     fix it (remove unknown keys for example), remove this trigger and spawn a new ExecuteInquiryJob with a modified
 *     inquiry.
 *  c) If they are, and it were stats...either set everything to done (if only stats were requested or the result is 0)
 *     or reschedule this job, setting the stats done parameter to true
 *
 * 2) If the stats were available, more than 0 were found, not only stats were requested and the first page of the
 *    result was not yet available, check if the first page is accessible
 *  a) If it is not, the job terminates this iteration and wait to be called again at the scheduled time
 *  b) If they are, set the corresponding parameter in the jobdatamap, quit this iteration and wait for the next call
 *
 * 3) If the stats were available, more than 0 were found, not only stats were requested, the first page of the
 *    result was already available, but the last page was not done yet...check if the last page is accessible now
 *  a) If it is not, the job terminates this iteration and wait to be called again at the scheduled time
 *  b) If it is, and it was an upload inquiry...spawn a UploadToCentralMdsDbJob and remove this job from the scheduler
 *  c) If it is, and it was not an upload inquiry...set the status to done and remove this job from the scheduler
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJob implements Job {

    private static final Logger logger = LogManager.getLogger(CheckInquiryStatusJob.class);

    private CheckInquiryStatusJobParams jobParams;
    private JobKey jobKey;
    private LdmConnector ldmConnector;
    private InquiryResult inquiryResult;
    private InquiryDetails inquiryDetails;

    public CheckInquiryStatusJob() {
        this.ldmConnector = ApplicationBean.getLdmConnector();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        jobKey = jobExecutionContext.getJobDetail().getKey();
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        jobParams = new CheckInquiryStatusJobParams(dataMap);
        logger.debug(jobKey.toString() + " " + jobParams);

        inquiryResult = InquiryResultUtil.fetchInquiryResultById(jobParams.getInquiryResultId());
        inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());

        if (!jobParams.isStatsDone()) {
            logger.debug("Stats were not available before. Checking again.");
            try {
                Object statsOrError = ldmConnector.getStatsOrError(inquiryResult.getLocation());
                if (statsOrError != null) {
                    boolean isStats = handleStatsOrError(statsOrError, jobExecutionContext);
                    if (isStats && jobParams.isStatsOnly()) {
                        // TODO: Check if the handling for uploads would be better in the following method
                        processReplyRules();
                    }
                } // else there was no stats yet...just continue with normal execution of the job
            } catch (Exception e) {
                throw new JobExecutionException(e);
            }
        } else if (!jobParams.isResultStarted()) {
            logger.debug("Stats are available, first result file was not available. Checking again.");
            try {
                if(ldmConnector.isFirstResultPageAvailable(inquiryResult.getLocation())) {
                    jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.STATS_DONE, true);
                    jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.RESULT_STARTED, true);
                }
            } catch (LDMConnectorException e) {
                throw new JobExecutionException(e);
            }
        } else if (!jobParams.isResultDone()) {
            logger.debug("First result file available, last one not yet. Checking again.");
            try {
                if (ldmConnector.isResultDone(inquiryResult.getLocation(), ldmConnector.getQueryResultStatistic(inquiryResult.getLocation()))) {
                    jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.RESULT_DONE, true);
                    if (!jobParams.isUpload()) {
                        logger.debug("Spawn generate stats job");
                        spawnGenerateStatsJob();
                    }
                    inquiryDetails.setStatus(InquiryStatusType.IS_READY);
                    InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
                    // If the inquiry belongs to an upload, also update the upload status
                    try {
                        Integer uploadId = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId()).getUploadId();
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
            } catch (LDMConnectorException | SchedulerException e) {
                throw new JobExecutionException(e);
            }
        } else {
            throw new JobExecutionException("This should never be reached");
        }
    }

    /**
     * Write a message, linked with the inquiry, to the event log
     *
     * @param message the message to log
     */
    private void log(String message) {
        if (jobParams.isUpload()) {
            try {
                Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
                EventLogUtil.insertEventLogEntryForUploadId(message, inquiry.getUploadId());
            } catch (NullPointerException npe) {
                logger.debug("Nullpointer exception caught while trying to insert EventLogMessage for upload.");
            }
        } else {
            EventLogUtil.insertEventLogEntryForInquiryId(message, inquiryDetails.getInquiryId());
        }
    }

    /**
     * Write a message, linked with the inquiry, to the event log
     *
     * @param messageType pre-defined event type
     * @param params parameters that will be substituted via resource bundle and messageformat
     */
    private void log(EventMessageType messageType, String... params) {
        if (jobParams.isUpload()) {
            try {
                Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
                EventLogUtil.insertEventLogEntryForUploadId(messageType, inquiry.getUploadId(), params);
            } catch (NullPointerException npe) {
                logger.debug("Nullpointer exception caught while trying to insert EventLogMessage for upload.");
            }
        } else {
            EventLogUtil.insertEventLogEntryForInquiryId(messageType, inquiryDetails.getInquiryId(), params);
        }
    }

    /**
     * Handle the outcome of the call to a /stats resource on the local datamanagement
     *
     * @param object will either be an error or a query result statistics object
     * @param jobExecutionContext the jobExecutionContext of this job instance
     * @return true if stats were received, false if an error was received or something unexpected happened
     */
    private boolean handleStatsOrError(Object object, JobExecutionContext jobExecutionContext) throws SchedulerException {
        // null is returned e.g. if the stats are not yet available
        if (object == null) {
            // Just continue with regular schedule
            return false;
        } else if (object.getClass().equals(Error.class)) {
            Error error = (Error) object;

            inquiryResult.setIsError(Boolean.TRUE);
            inquiryResult.setErrorCode(Integer.toString(error.getErrorCode()));
            InquiryResultUtil.updateInquiryResult(inquiryResult);

            switch (error.getErrorCode()) {
                case LdmClient.ERROR_CODE_DATE_PARSING_ERROR:
                case LdmClient.ERROR_CODE_UNIMPLEMENTED:
                case LdmClient.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
                    log(EventMessageType.E_LDM_ERROR, "code:" + error.getErrorCode(), "description:" + error.getDescription());
                    inquiryDetails.setStatus(InquiryStatusType.IS_LDM_ERROR);
                    InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
                    unscheduleThisJob(jobExecutionContext);
                    break;
                case LdmClient.ERROR_CODE_UNKNOWN_MDRKEYS:
                    String unknownKeys = Joiner.on(ExecuteInquiryJobParams.SEPARATOR_UNKNOWN_KEYS).join(error.getMdrKey());
                    log(EventMessageType.E_LDM_ERROR, "code:" + error.getErrorCode(), "keys:" + unknownKeys);
                    spawnNewInquiryExecutionJob(unknownKeys);
                    jobExecutionContext.setResult(new CheckInquiryStatusJobResult(true, false));
                    unscheduleThisJob(jobExecutionContext);
                default:
                    break;
            }
            return false;
        } else if (object.getClass().equals(QueryResultStatistic.class)){
            QueryResultStatistic queryResultStatistic = (QueryResultStatistic) object;
            log(EventMessageType.E_STATISTICS_READY, Integer.toString(queryResultStatistic.getTotalSize()));
            inquiryResult.setSize(queryResultStatistic.getTotalSize());
            InquiryResultUtil.updateInquiryResult(inquiryResult);
            jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.STATS_DONE, true);
            if (inquiryResult.getStatisticsOnly() || queryResultStatistic.getTotalSize() == 0) {
                if (inquiryResult.getStatisticsOnly()) {
                    logger.debug("Only stats were requested. And they are done. Setting Inquiry Details to done and quitting.");
                } else {
                    logger.debug("No results found. Setting Inquiry Details to done and quitting.");
                }

                // If the inquiry belongs to an upload, also update the upload status
                try {
                    Integer uploadId = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId()).getUploadId();
                    if (jobParams.isUpload() && uploadId != null) {
                        UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_COMPLETED);
                    }
                } catch (Exception e) {
                    logger.error("Exception caught while trying to update upload status", e);
                }

                inquiryDetails.setStatus(InquiryStatusType.IS_READY);
                InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
                try {
                    jobExecutionContext.setResult(new CheckInquiryStatusJobResult(false, true));
                    unscheduleThisJob(jobExecutionContext);
                    return true;
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            } else {
                rescheduleCheckingForResults(jobExecutionContext);
            }
        } else {
            log("Unknown object received");
        }
        return false;
    }

    private void unscheduleThisJob(JobExecutionContext jobExecutionContext) throws SchedulerException {
        Trigger currentTrigger = jobExecutionContext.getTrigger();
        ApplicationBean.getScheduler().unscheduleJob(currentTrigger.getKey());
    }

    /**
     * Replace the current trigger for this job with a new one, since the timings may differ
     *
     * @param jobExecutionContext the jobExecutionContext of this job instance
     */
    private void rescheduleCheckingForResults(JobExecutionContext jobExecutionContext) throws SchedulerException {
        // Replace trigger with differently timed trigger
        int retryAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
        int retryInterval = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
        int initialDelay = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_INITIAL_DELAY_SECONDS);

        Trigger oldTrigger = ApplicationBean.getScheduler().getTrigger(jobExecutionContext.getTrigger().getKey());
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
        jobExecutionContext.setResult(new CheckInquiryStatusJobResult(true, false));
        ApplicationBean.getScheduler().rescheduleJob(oldTrigger.getKey(), newTrigger);
    }

    /**
     * Create and schedule a new ExecuteInquiryJob with keys to ignore
     *
     * @param unknownKeys a joined list of keys, the local datamanagement does not understand. The new ExecuteInquiryJob
     *                    will remove them and re-post it
     */
    private void spawnNewInquiryExecutionJob(String unknownKeys) {
        try {
            JobKey jobKey = JobKey.jobKey(ExecuteInquiryJobParams.JOBNAME, ExecuteInquiryJobParams.JOBGROUP);

            // Fill the JobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_ID, inquiryDetails.getInquiryId());
            jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_DETAILS_ID, inquiryDetails.getId());
            jobDataMap.put(ExecuteInquiryJobParams.UNKNOWN_KEYS, unknownKeys);
            jobDataMap.put(ExecuteInquiryJobParams.STATS_ONLY, inquiryResult.getStatisticsOnly());
            jobDataMap.put(ExecuteInquiryJobParams.IS_UPLOAD, jobParams.isUpload());

            // Fire exactly once - right now
            ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
        } catch (SchedulerException e) {
            logger.error("Error spawning Inquiry Execution Job");
        }
    }

    /**
     * (Re-)spawn an upload job
     *
     * @param uploadId the database id of the upload
     */
    private void spawnUploadToCentralMdsDbJob(int uploadId) {
        try {
            Upload upload = UploadUtil.fetchUploadById(uploadId);
            String jobName = upload.getDktkFlagged() ? UploadJobParams.JOBNAME_DKTK : UploadJobParams.JOBNAME_NO_DKTK;
            JobKey newJobKey = JobKey.jobKey(jobName, UploadJobParams.JOBGROUP);

            // Fill the JobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(UploadJobParams.UPLOAD_ID, uploadId);
            jobDataMap.put(UploadJobParams.STATUS, UploadStatusType.US_QUERY_READY.getLiteral());
            jobDataMap.put(UploadJobParams.DKTK_FLAGGED, upload.getDktkFlagged());

            // Fire exactly once - right now
            ApplicationBean.getScheduler().triggerJob(newJobKey, jobDataMap);
        } catch (SchedulerException e) {
            logger.error("Error spawning Inquiry Execution Job");
        }

    }

    /**
     * Spawn a job to generate the statistics for the query result
     */
    private void spawnGenerateStatsJob() {
        try {
            JobKey jobKey = JobKey.jobKey(GenerateInquiryResultStatsJobParams.JOBNAME, GenerateInquiryResultStatsJobParams.JOBGROUP);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(GenerateInquiryResultStatsJobParams.INQUIRY_RESULT_ID, inquiryResult.getId());

            // Fire exactly once - right now
            ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
        } catch (SchedulerException e) {
            logger.error("Error spawning Generate Result Stats Job", e);
        }
    }

    /**
     * Check if any automated replies should be sent and take care of it
     *
     * TODO: Maybe create a separate job for that?
     */
    @SuppressWarnings("ConstantConditions")
    private void processReplyRules() {
        Inquiry inquiry = null;
        try {
            inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
            Integer brokerId = inquiry.getBrokerId();
            if (brokerId == null) {
                // If the broker Id is null, this is from an upload, not an inquiry. For now, return here. Maybe use this to handle the upload itself?
                return;
            }
            List<InquiryHandlingRule> inquiryHandlingRules = InquiryHandlingRuleUtil.fetchInquiryHandlingRulesForBrokerId(brokerId);
            // TODO: if more reply rules are defined, this has to be smarter. for now just check if any auto reply is defined for the broker
            ReplyRuleType replyRule = null;
            for (InquiryHandlingRule inquiryHandlingRule : inquiryHandlingRules) {
                replyRule = inquiryHandlingRule.getAutomaticReply();
            }

            logger.debug("Automatic reply is set to: " + replyRule);
            switch (replyRule) {
                case RR_DATA:
                    logger.info("Full dataset shall be sent. Not yet implemented.");
                    break;
                case RR_TOTAL_COUNT:
                    logger.info("Reporting the amount of matching datasets to the broker.");
                    BrokerConnector brokerConnector = new BrokerConnector(BrokerUtil.fetchBrokerById(brokerId));
                    if (ApplicationUtils.isDktk()) {
                        brokerConnector.reply(inquiryDetails, inquiryResult.getSize());
                    } else if (ApplicationUtils.isSamply()) {
                        try {
                            BbmriResult queryResult = (BbmriResult) ldmConnector.getResults(InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId()).getLocation());
                            brokerConnector.reply(inquiryDetails, queryResult);
                        } catch (LDMConnectorException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case RR_NO_AUTOMATIC_ACTION:
                default:
                    logger.info("No automatic replies configured for this broker.");
                    break;
            }

        } catch (NullPointerException npe) {
            // Just catch any Null Pointer exceptions for now
            logger.error("Null pointer Exception caught while trying to getPatientIds reply rules", npe);
        } catch (BrokerConnectorException e) {
            if (inquiry == null) {
                EventLogUtil.insertEventLogEntry(EventMessageType.E_BROKER_REPLY_ERROR,e.getMessage());
            } else {
                EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_BROKER_REPLY_ERROR, inquiry.getId(), e.getMessage());
            }
        }
    }
}
