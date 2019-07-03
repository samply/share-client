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

package de.samply.share.client.job.params;

import org.quartz.JobDataMap;

/**
 * The settings for an CheckInquiryStatusJob are kept in an instance of this class
 *
 * Takes the JobDataMap that is associated with the instance of the job
 */
public class CheckInquiryStatusJobParams {

    public static final String JOBGROUP = "InquiryGroup";
    public static final String JOBNAME = "CheckInquiryStatusJob";
    public static final String TRIGGERNAME = "CheckInquiryStatusTrigger";
    public static final String INQUIRY_RESULT_ID = "inquiry_result_id";
    public static final String STATS_DONE = "stats_done";
    public static final String RESULT_STARTED = "result_started";
    public static final String RESULT_DONE = "result_done";
    public static final String IS_UPLOAD = "is_upload";
    public static final String STATS_ONLY = "stats_only";

    private final int inquiryResultId;
    private final boolean statsDone;
    private final boolean resultStarted;
    private final boolean resultDone;
    private final boolean isUpload;
    private final boolean statsOnly;

    public CheckInquiryStatusJobParams(JobDataMap dataMap) {
        this.inquiryResultId = dataMap.getInt(INQUIRY_RESULT_ID);
        this.statsDone = dataMap.getBoolean(STATS_DONE);
        this.resultStarted = dataMap.getBoolean(RESULT_STARTED);
        this.resultDone = dataMap.getBoolean(RESULT_DONE);
        this.isUpload = dataMap.getBoolean(IS_UPLOAD);
        this.statsOnly = dataMap.getBoolean(STATS_ONLY);
    }

    public int getInquiryResultId() {
        return inquiryResultId;
    }

    public boolean isStatsDone() {
        return statsDone;
    }

    public boolean isResultStarted() {
        return resultStarted;
    }

    public boolean isResultDone() {
        return resultDone;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public boolean isStatsOnly() {
        return statsOnly;
    }

    @Override
    public String toString() {
        return "CheckInquiryStatusJobParams{" +
                "inquiryResultId=" + inquiryResultId +
                ", statsDone=" + statsDone +
                ", resultStarted=" + resultStarted +
                ", resultDone=" + resultDone +
                ", isUpload=" + isUpload +
                ", statsOnly=" + statsOnly +
                '}';
    }
}
