package de.samply.share.client.job.params;

import org.quartz.JobDataMap;

public class CheckInquiryStatusReadyForMultipleCriteriaJobParams {

    public static final String JOBGROUP = "InquiryCriteriaGroup";
    public static final String JOBNAME = "CheckInquiryStatusReadyForMultipleCriteria";
    public static final String INQUIRY_DETAILS_ID = "inquiry_details_id";

    private final int inquiryDetailsId;

    public CheckInquiryStatusReadyForMultipleCriteriaJobParams(JobDataMap dataMap) {
        this.inquiryDetailsId = dataMap.getInt(INQUIRY_DETAILS_ID);
    }

    public int getInquiryDetailsId() {
        return inquiryDetailsId;
    }

    @Override
    public String toString() {
        return "ExecuteInquiryJobParams{" +
                "inquiryDetailsId=" + inquiryDetailsId +
                '}';
    }
}
