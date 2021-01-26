package de.samply.share.client.job.params;

import com.google.common.base.Splitter;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.common.utils.SamplyShareUtils;
import java.util.ArrayList;
import java.util.List;
import org.quartz.JobDataMap;

/**
 * The settings for an ExecuteInquiryJob are kept in an instance of this class. Takes the JobDataMap
 * that is associated with the instance of the job.
 */
public class ExecuteInquiryJobParams {

  public static final String JOBGROUP = "InquiryGroup";
  public static final String INQUIRY_ID = "inquiry_id";
  public static final String INQUIRY_DETAILS_ID = "inquiry_details_id";
  public static final String STATS_ONLY = "stats_only";
  public static final String UNKNOWN_KEYS = "unknown_keys";
  public static final String IS_UPLOAD = "is_upload";
  public static final String SEPARATOR_UNKNOWN_KEYS = ", ";
  private static final String JOBNAME_DKTK = "ExecuteInquiryJobCentraxx";
  private static final String JOBNAME_SAMPLY = "ExecuteInquiryJobSamplystoreBiobanks";
  private static final String JOBNAME_CQL = "ExecuteInquiryJobCql";
  private final int inquiryId;
  private final int inquiryDetailsId;
  private final boolean statsOnly;
  private final List<String> unknownKeys;
  private final boolean isUpload;


  /**
   * Set the configuration for the ExecuteInquiryJob.
   *
   * @param dataMap the configuration with the inquiry attributes.
   */
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

  /**
   * Get the job class name depends on the project name.
   *
   * @return the job class name for executing the inquires.
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

  public int getInquiryId() {
    return inquiryId;
  }

  public int getInquiryDetailsId() {
    return inquiryDetailsId;
  }

  public boolean isStatsOnly() {
    return statsOnly;
  }

  public List<String> getUnknownKeys() {
    return unknownKeys;
  }

  public boolean isUpload() {
    return isUpload;
  }

  @Override
  public String toString() {
    return "ExecuteInquiryJobParams{"
        + "inquiryId=" + inquiryId
        + ", inquiryDetailsId=" + inquiryDetailsId
        + ", statsOnly=" + statsOnly
        + ", unknownKeys=" + unknownKeys
        + ", isUpload=" + isUpload
        + '}';
  }
}
