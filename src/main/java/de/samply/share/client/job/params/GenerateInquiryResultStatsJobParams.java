package de.samply.share.client.job.params;

import org.quartz.JobDataMap;

/**
 * The settings for an GenerateInquiryResultStatsJob are kept in an instance of this class. Takes
 * the JobDataMap that is associated with the instance of the job.
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
    return "GenerateInquiryResultStatsJobParams{"
        + "inquiryResultId=" + inquiryResultId
        + '}';
  }
}
