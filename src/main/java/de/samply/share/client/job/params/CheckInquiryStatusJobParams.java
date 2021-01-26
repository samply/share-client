package de.samply.share.client.job.params;

import de.samply.share.client.control.ApplicationUtils;
import org.quartz.JobDataMap;

/**
 * The settings for an CheckInquiryStatusJob are kept in an instance of this class. Takes the
 * JobDataMap that is associated with the instance of the job.
 */
public class CheckInquiryStatusJobParams {

  public static final String JOBGROUP = "InquiryGroup";
  public static final String INQUIRY_RESULT_ID = "inquiry_result_id";
  public static final String STATS_DONE = "stats_done";
  public static final String RESULT_STARTED = "result_started";
  public static final String RESULT_DONE = "result_done";
  public static final String IS_UPLOAD = "is_upload";
  public static final String STATS_ONLY = "stats_only";
  public static final String ENTITY_TYPE = "entity_type";
  private static final String JOBNAME_DKTK = "CheckInquiryStatusJobCentraxx";
  private static final String JOBNAME_SAMPLY = "CheckInquiryStatusJobSamplystoreBiobanks";
  private static final String JOBNAME_CQL = "CheckInquiryStatusJobCql";
  private final int inquiryResultId;
  private final boolean statsDone;
  private final boolean resultStarted;
  private final boolean resultDone;
  private final boolean isUpload;
  private final boolean statsOnly;
  private final String entityType;

  /**
   * Set the configuration for the CheckInquiryJob.
   *
   * @param dataMap the configuration with the inquiry attributes.
   */
  public CheckInquiryStatusJobParams(JobDataMap dataMap) {
    this.inquiryResultId = dataMap.getInt(INQUIRY_RESULT_ID);
    this.statsDone = dataMap.getBoolean(STATS_DONE);
    this.resultStarted = dataMap.getBoolean(RESULT_STARTED);
    this.resultDone = dataMap.getBoolean(RESULT_DONE);
    this.isUpload = dataMap.getBoolean(IS_UPLOAD);
    this.statsOnly = dataMap.getBoolean(STATS_ONLY);
    this.entityType = dataMap.getString(ENTITY_TYPE);
  }

  /**
   * Get the job class name depends on the project name.
   *
   * @return the job class name for checking the inquires.
   */
  public static String getJobName() {
    if (ApplicationUtils.isDktk()) {
      return JOBNAME_DKTK;
    } else if (ApplicationUtils.isLanguageQuery()) {
      return JOBNAME_SAMPLY;
    } else {
      return JOBNAME_CQL;
    }
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

  public String getEntityType() {
    return entityType;
  }

  @Override
  public String toString() {
    return "CheckInquiryStatusJobParams{"
        + "inquiryResultId=" + inquiryResultId
        + ", statsDone=" + statsDone
        + ", resultStarted=" + resultStarted
        + ", resultDone=" + resultDone
        + ", isUpload=" + isUpload
        + ", statsOnly=" + statsOnly
        + '}';
  }
}
