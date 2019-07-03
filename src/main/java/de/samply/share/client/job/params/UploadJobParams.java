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

import de.samply.share.client.model.db.enums.UploadStatusType;
import org.quartz.JobDataMap;

/**
 * The settings for an UploadJob are kept in an instance of this class
 *
 * Takes the JobDataMap that is associated with the instance of the job
 */
public class UploadJobParams {

    public static final String JOBGROUP = "CentralSearchGroup";
    public static final String JOBNAME_DKTK = "UploadToCentralMdsDbJobDktkFlag";
    public static final String JOBNAME_NO_DKTK = "UploadToCentralMdsDbJobNoDktkFlag";
    public static final String UPLOAD_ID = "upload_id";
    public static final String DKTK_FLAGGED = "dktk_flagged";
    private static final String DELETE_BEFORE_UPLOAD = "delete_before_upload";
    public static final String STATUS = "status";

    private int uploadId;
    private final boolean dktkFlaggedPatients;
    private final boolean deleteBeforeUpload;
    private final UploadStatusType status;

    public UploadJobParams(JobDataMap dataMap) {
        try {
            this.uploadId = dataMap.getInt(UPLOAD_ID);
        } catch (Exception e) {
            this.uploadId = 0;
        }

        this.dktkFlaggedPatients = dataMap.getBoolean(DKTK_FLAGGED);
        this.deleteBeforeUpload = dataMap.getBoolean(DELETE_BEFORE_UPLOAD);
        this.status = UploadStatusType.valueOf(dataMap.getString(STATUS));
    }

    public int getUploadId() {
        return uploadId;
    }

    public void setUploadId(int uploadId) {
        this.uploadId = uploadId;
    }

    public boolean isDktkFlaggedPatients() {
        return dktkFlaggedPatients;
    }

    public boolean isDeleteBeforeUpload() {
        return deleteBeforeUpload;
    }

    public UploadStatusType getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "UploadJobParams{" +
                "uploadId=" + uploadId +
                ", dktkFlaggedPatients=" + dktkFlaggedPatients +
                ", deleteBeforeUpload=" + deleteBeforeUpload +
                ", status=" + status +
                '}';
    }
}
