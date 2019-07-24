package de.samply.share.client.util.connector;

import de.samply.common.ldmclient.centraxx.LdmClientCentraxx;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.common.utils.SamplyShareUtils;

public class InquiryUtils {

    public void changeStatusOfInquiryResultToError(InquiryDetails inquiryDetails) {
        InquiryResult inquiryResult = new InquiryResult();
        inquiryResult.setErrorCode(Integer.toString(LdmClientCentraxx.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE));
        inquiryResult.setExecutedAt(SamplyShareUtils.getCurrentSqlTimestamp());
        inquiryResult.setInquiryDetailsId(inquiryDetails.getId());
        inquiryResult.setIsError(true);
        InquiryResultUtil.insertInquiryResult(inquiryResult);
    }

}
