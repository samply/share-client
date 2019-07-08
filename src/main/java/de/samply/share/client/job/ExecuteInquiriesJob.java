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
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryHandlingRuleUtil;
import de.samply.share.client.util.db.InquiryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.List;

/**
 * This Job checks the database for new jobs and gives them to an execution handler one by one
 * <p>
 * It is defined and scheduled in the quartz-jobs.xml
 * <p>
 * The basic steps it performs are:
 * <p>
 * 1) Get the list of new inquiries
 * 2) If there is a new inquiry, and none still processing...spawn an inquiry execution task for the new one
 */
@DisallowConcurrentExecution
public class ExecuteInquiriesJob implements Job {

    private static final Logger logger = LogManager.getLogger(ExecuteInquiriesJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!InquiryDetailsUtil.getInquiryDetailsByStatus(InquiryStatusType.IS_PROCESSING).isEmpty()) {
            return;
        }

        List<InquiryDetails> inquiryDetailsList = InquiryDetailsUtil.getInquiryDetailsByStatus(InquiryStatusType.IS_NEW);
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
     * Hand over the inquiry to an ExecuteInquiryJob
     *
     * @param inquiry        the inquiry to delegate to the execute job
     * @param inquiryDetails the corresponding inquiry details object
     * @param statsOnly      set to true if only statistics are requested and no whole result set (list of patients)
     */
    private void spawnNewInquiryExecutionJob(de.samply.share.client.model.db.tables.pojos.Inquiry inquiry, InquiryDetails inquiryDetails, boolean statsOnly) {
        try {
            JobKey jobKey = JobKey.jobKey(ExecuteInquiryJobParams.JOBNAME, ExecuteInquiryJobParams.JOBGROUP);

            // Fill the JobDataMap for the trigger
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_ID, inquiry.getId());
            jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_DETAILS_ID, inquiryDetails.getId());
            jobDataMap.put(ExecuteInquiryJobParams.STATS_ONLY, statsOnly);
            jobDataMap.put(ExecuteInquiryJobParams.IS_UPLOAD, (inquiry.getUploadId() != null));

            // Fire exactly once - right now
            logger.info("Give Execute Job to scheduler for inquiry with id " + inquiry.getId());
            ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
        } catch (SchedulerException e) {
            logger.error("Error spawning Inquiry Execution Job", e);
        }
    }
}
