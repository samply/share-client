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

import com.google.common.base.Splitter;
import de.samply.share.common.utils.SamplyShareUtils;
import org.quartz.JobDataMap;

import java.util.ArrayList;
import java.util.List;

/**
 * The settings for an ExecuteInquiryJob are kept in an instance of this class
 *
 * Takes the JobDataMap that is associated with the instance of the job
 */
public class ExecuteInquiryJobParams {

    public static final String JOBGROUP = "InquiryGroup";
    public static final String JOBNAME = "ExecuteInquiryJob";
    public static final String INQUIRY_ID = "inquiry_id";
    public static final String INQUIRY_DETAILS_ID = "inquiry_details_id";
    public static final String STATS_ONLY = "stats_only";
    public static final String UNKNOWN_KEYS = "unknown_keys";
    public static final String IS_UPLOAD = "is_upload";

    public static final String SEPARATOR_UNKNOWN_KEYS = ", ";

    private int inquiryId;
    private int inquiryDetailsId;
    private boolean statsOnly;
    private List<String> unknownKeys;
    private boolean isUpload;


    public ExecuteInquiryJobParams(JobDataMap dataMap) {
        this.inquiryId = dataMap.getInt(INQUIRY_ID);
        this.inquiryDetailsId = dataMap.getInt(INQUIRY_DETAILS_ID);
        this.statsOnly = dataMap.getBoolean(STATS_ONLY);
        this.isUpload = dataMap.getBoolean(IS_UPLOAD);

        String unknownKeysConcatenated = dataMap.getString(UNKNOWN_KEYS);
        if (!SamplyShareUtils.isNullOrEmpty(unknownKeysConcatenated)) {
            Splitter splitter = Splitter.on(SEPARATOR_UNKNOWN_KEYS);
            this.unknownKeys = splitter.splitToList(unknownKeysConcatenated);
        } else {
            this.unknownKeys = new ArrayList<>();
        }
    }

    public int getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }

    public int getInquiryDetailsId() {
        return inquiryDetailsId;
    }

    public void setInquiryDetailsId(int inquiryDetailsId) {
        this.inquiryDetailsId = inquiryDetailsId;
    }

    public boolean isStatsOnly() {
        return statsOnly;
    }

    public void setStatsOnly(boolean statsOnly) {
        this.statsOnly = statsOnly;
    }

    public List<String> getUnknownKeys() {
        return unknownKeys;
    }

    public void setUnknownKeys(List<String> unknownKeys) {
        this.unknownKeys = unknownKeys;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    @Override
    public String toString() {
        return "ExecuteInquiryJobParams{" +
                "inquiryId=" + inquiryId +
                ", inquiryDetailsId=" + inquiryDetailsId +
                ", statsOnly=" + statsOnly +
                ", unknownKeys=" + unknownKeys +
                ", isUpload=" + isUpload +
                '}';
    }
}
