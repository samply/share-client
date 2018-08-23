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
import de.samply.share.client.job.params.CheckInquiryStatusJobResult;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.util.db.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.List;

/**
 * This listener will be called when an instance of the CheckInquiryStatusJob is done
 *
 * If the job will NOT fire again, and the status is still processing...set it to abandoned.
 * If the job was connected to an upload, also set the upload to abandoned
 */
public class CheckInquiryStatusJobListener implements JobListener {

    private static final Logger logger = LogManager.getLogger(CheckInquiryStatusJobListener.class);

    private String name;

    public CheckInquiryStatusJobListener(String name) {
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
        if (e != null){
            logger.debug(" - Exception: " + e);
        }
        CheckInquiryStatusJobResult result;
        Object resultObject = jobExecutionContext.getResult();
        if (resultObject != null) {
            try {
                result = (CheckInquiryStatusJobResult)resultObject;
            } catch (ClassCastException cce) {
                logger.error("Error getting CheckInquiryStatusJob Result");
                result = new CheckInquiryStatusJobResult(false, false);
            }
        } else {
            result = new CheckInquiryStatusJobResult(false, false);
        }

        if (result != null && result.isRescheduled()) {
            logger.trace("Job is rescheduled. Don't set anything to abandoned...");
            return;
        } else {
            logger.trace("Job is NOT rescheduled...check if something is abandoned...");
        }

        if (result.isResetStatusFlags()) {
            // In any case, reset the jobDataMap parameters for stats and result done to false
            jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.STATS_DONE, false);
            jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.RESULT_STARTED, false);
            jobExecutionContext.getJobDetail().getJobDataMap().put(CheckInquiryStatusJobParams.RESULT_DONE, false);
        }

        JobKey key = jobExecutionContext.getJobDetail().getKey();
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

        CheckInquiryStatusJobParams jobParams = new CheckInquiryStatusJobParams(dataMap);
        InquiryResult inquiryResult = InquiryResultUtil.fetchInquiryResultById(jobParams.getInquiryResultId());
        InquiryDetails inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());

        // If the status of the inquiry is still "processing" when the check-job is done, set the status to abandoned
        if (inquiryDetails.getStatus() == InquiryStatusType.IS_PROCESSING && ! willFireAgain(key)) {
            logger.info("Setting status to ABANDONED for inquiry details with id " + inquiryDetails.getId());
            inquiryDetails.setStatus(InquiryStatusType.IS_ABANDONED);
            InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
            EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_STATUS_CHECK_ABANDONED, inquiryDetails.getInquiryId());
            // If it was an upload, also set this status to abandoned
            try {
                Integer uploadId = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId()).getUploadId();
                if (uploadId != null) {
                    UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_ABANDONED);
                }
            } catch (Exception ex) {
                logger.error("Exception caught while trying to update upload status", ex);
            }
        }

    }

    /**
     * Check if the trigger for this job will fire again
     *
     * @param jobKey the jobkey of the finished CheckInquiryStatusJob
     * @return true if the connected trigger will fire again, false otherwise
     */
    @SuppressWarnings("unchecked")
    private boolean willFireAgain(JobKey jobKey) {
        try {
            List<Trigger> triggers = (List<Trigger>) ApplicationBean.getScheduler().getTriggersOfJob(jobKey);
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
