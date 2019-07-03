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
 * The settings for an GenerateInquiryResultStatsJob are kept in an instance of this class
 *
 * Takes the JobDataMap that is associated with the instance of the job
 */
public class GenerateInquiryResultStatsJobParams {

    public static final String JOBGROUP = "InquiryGroup";
    public static final String JOBNAME = "GenerateInquiryResultStatsJob";
    public static final String INQUIRY_RESULT_ID = "inquiry_result_id";

    private final int inquiryResultId;


    public GenerateInquiryResultStatsJobParams(JobDataMap dataMap) {
        this.inquiryResultId = dataMap.getInt(INQUIRY_RESULT_ID);
    }

    public int getInquiryResultId() {
        return inquiryResultId;
    }

    @Override
    public String toString() {
        return "GenerateInquiryResultStatsJobParams{" +
                "inquiryResultId=" + inquiryResultId +
                '}';
    }
}
