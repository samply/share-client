package de.samply.share.client.job.params;

import de.samply.share.client.model.db.enums.UploadStatusType;
import org.quartz.JobDataMap;

/**
 * The settings for an UploadJob are kept in an instance of this class. Takes the JobDataMap that is
 * associated with the instance of the job.
 */
public class UploadJobParams {

  public static final String JOBGROUP = "CentralSearchGroup";
  public static final String JOBNAME_DKTK = "UploadToCentralMdsDbJobDktkFlag";
  public static final String JOBNAME_NO_DKTK = "UploadToCentralMdsDbJobNoDktkFlag";
  public static final String UPLOAD_ID = "upload_id";
  public static final String DKTK_FLAGGED = "dktk_flagged";
  public static final String STATUS = "status";
  private static final String DELETE_BEFORE_UPLOAD = "delete_before_upload";
  private final boolean dktkFlaggedPatients;
  private final boolean deleteBeforeUpload;
  private final UploadStatusType status;
  private int uploadId;

  /**
   * Read the configs from the JobDataMap and set the params for the upload.
   *
   * @param dataMap configs for the upload
   */
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
    return "UploadJobParams{"
        + "uploadId=" + uploadId
        + ", dktkFlaggedPatients=" + dktkFlaggedPatients
        + ", deleteBeforeUpload=" + deleteBeforeUpload
        + ", status=" + status
        + '}';
  }
}
