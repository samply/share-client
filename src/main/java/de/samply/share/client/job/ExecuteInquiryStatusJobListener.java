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

import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.util.db.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

/**
 * This listener will be called when an instance of the ExecuteInquiryStatusJob is done
 *
 * It checks if an Exception was thrown and sets the corresponding object state to cancelled/aborted
 */
public class ExecuteInquiryStatusJobListener implements JobListener {

    private static final Logger logger = LogManager.getLogger(ExecuteInquiryStatusJobListener.class);

    private String name;

    public ExecuteInquiryStatusJobListener(String name) {
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
        // Don't do anything if there was no exception
        if (e == null) {
            return;
        }
        logger.debug("Job execution ended with an exception. Context: " + jobExecutionContext + " - exception " + e);

        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

        ExecuteInquiryJobParams jobParams = new ExecuteInquiryJobParams(dataMap);
        Inquiry inquiry = InquiryUtil.fetchInquiryById(jobParams.getInquiryId());

        try {
            Integer uploadId = inquiry.getUploadId();
            if (uploadId != null) {
                UploadUtil.setUploadStatusById(uploadId, UploadStatusType.US_ABANDONED);
            }
        } catch (Exception ex) {
            logger.error("Exception caught while trying to update upload status", ex);
        }
    }
}
