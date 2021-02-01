package de.samply.share.client.job.util;

import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryUtil;

public class ReplyRulesApplierUtil {

  /**
   * Handle BrokerConnectorException for a Inquiry.
   *
   * @param e                the BrokerConnectorException
   * @param inquiryDetailsId the inquiryDetailsId of an Inquiry.
   */
  public static void handleBrokerConnectorException(BrokerConnectorException e,
      Integer inquiryDetailsId) {
    Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetailsId);

    if (inquiry == null) {
      EventLogUtil.insertEventLogEntry(EventMessageType.E_BROKER_REPLY_ERROR, e.getMessage());
    } else {
      EventLogUtil
          .insertEventLogEntryForInquiryId(EventMessageType.E_BROKER_REPLY_ERROR, inquiry.getId(),
              e.getMessage());
    }
  }
}
