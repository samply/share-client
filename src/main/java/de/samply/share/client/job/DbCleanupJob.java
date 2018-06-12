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
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Do some housekeeping in the database (e.g. mark inquiries as archived after a certain amount of time)
 */
@DisallowConcurrentExecution
public class DbCleanupJob implements Job {

    private static final Logger logger = LogManager.getLogger(DbCleanupJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        moveOldInquiries();
        checkResultAvailability();
    }

    /**
     * Mark all inquiries, that are older than the configured threshold, as archived
     */
    private void moveOldInquiries() {
        int daysThreshold = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.JOB_MOVE_INQUIRIES_TO_ARCHIVE_AFTER_DAYS);
        logger.debug("Archiving all inquiries older than " + daysThreshold + " days.");
        List<InquiryDetails> inquiryDetailsList = InquiryDetailsUtil.getInquiryDetailsOlderThanDays(daysThreshold);

        for (InquiryDetails inquiryDetails : inquiryDetailsList) {
            EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_ARCHIVE_INQUIRY_AFTER_THRESHOLD, inquiryDetails.getInquiryId(), Integer.toString(daysThreshold));
            Inquiry inquiry= InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
            inquiry.setArchivedAt(SamplyShareUtils.getCurrentSqlTimestamp());
            inquiryDetails.setStatus(InquiryStatusType.IS_ARCHIVED);
            InquiryUtil.updateInquiry(inquiry);
        }
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetailsList);
    }

    /**
     * Iterate through all inquiry results and check if they can still be accessed
     */
    private void checkResultAvailability() {
        LdmConnector ldmConnector = ApplicationBean.getLdmConnector();

        // First, check if local datamanagement is reachable. If not - don't fiddle with the results
        try {
            ldmConnector.getUserAgentInfo();
        } catch (LDMConnectorException e) {
            logger.debug("Local Datamangagement not reachable at this moment. Skip checking result availability.");
            return;
        }

        List<InquiryResult> inquiryResults = InquiryResultUtil.fetchInquiryResults();

        for (InquiryResult inquiryResult : inquiryResults) {
            InquiryDetails inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
            if (!(inquiryDetails.getStatus().equals(InquiryStatusType.IS_LDM_ERROR))) {
                try {
                    ldmConnector.getPageCount(inquiryResult.getLocation());
                } catch (LDMConnectorException e) {
                    inquiryResult.setIsError(true);
                    if (!(checkInquiryID(inquiryResults, inquiryResult))) {
                        removeResult(inquiryResult);
                    }
                }
            }

        }

    }

    /**
     * Remove the location of an inquiry result and mark the inquiry (details) as archived
     *
     * @param inquiryResult the inquiry result to "remove"
     */
    private void removeResult(InquiryResult inquiryResult) {
        InquiryDetails inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
        EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_ARCHIVE_INQUIRY_RESULT_UNAVAILABLE, inquiryDetails.getInquiryId());
        inquiryResult.setValidUntil(SamplyShareUtils.getCurrentSqlTimestamp());
        inquiryResult.setLocation("");
        InquiryResultUtil.updateInquiryResult(inquiryResult);
        inquiryDetails.setStatus(InquiryStatusType.IS_ARCHIVED);
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
    }

    /**
     * Check if there is an other inquiry with the same id and with no error result
     *
     * @param inquiryResults the list of the inquiries
     * @param inquiryResult  the current inquiry
     * @return if there is an other inquiry with the same id and with no error result
     */

    private boolean checkInquiryID(List<InquiryResult> inquiryResults, InquiryResult inquiryResult) {
        for (InquiryResult inquiryResultTmp : inquiryResults) {
            if (inquiryResultTmp.getInquiryDetailsId().equals(inquiryResult.getInquiryDetailsId()) && !inquiryResultTmp.getIsError()) {
                return true;
            }
        }
        return false;
    }
}
