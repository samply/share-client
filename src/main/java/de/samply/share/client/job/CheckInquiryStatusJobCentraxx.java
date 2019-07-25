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
import de.samply.share.client.model.db.tables.pojos.Upload;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UploadUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.function.Consumer;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJobCentraxx extends AbstractCheckInquiryStatusJob<LdmConnectorCentraxx> {

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

    private void checkForFirstResultPage(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            if (ldmConnector.isFirstResultPageAvailable(inquiryResult.getLocation())) {
                jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.STATS_DONE, true);
                jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.RESULT_STARTED, true);
            }
        } catch (LDMConnectorException e) {
            throw new JobExecutionException(e);
        }
    }

    private void checkForLastResultPage(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            if (ldmConnector.isResultDone(inquiryResult.getLocation(), ldmConnector.getQueryResultStatistic(inquiryResult.getLocation()))) {
                jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.RESULT_DONE, true);
                if (!jobParams.isUpload()) {
                    logger.debug("Spawn generate stats job");
                    spawnGenerateStatsJob();
                }
                Utils.setStatus(inquiryDetails, InquiryStatusType.IS_READY);
                inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_READY);
                InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
                InquiryCriteriaUtil.updateInquiryCriteria(inquiryCriteria);
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
    }

    void handleInquiryStatusReady() {
        Utils.setStatus(inquiryDetails, InquiryStatusType.IS_READY);
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
