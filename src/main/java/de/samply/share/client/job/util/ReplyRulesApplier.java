package de.samply.share.client.job.util;

import de.samply.share.client.model.db.enums.ReplyRuleType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryHandlingRule;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.InquiryHandlingRuleUtil;
import de.samply.share.client.util.db.InquiryUtil;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplyRulesApplier {

  private static final Logger logger = LogManager.getLogger(ReplyRulesApplier.class);

  private final Consumer<BrokerConnector> processReplyMethod;

  public ReplyRulesApplier(Consumer<BrokerConnector> processReplyMethod) {
    this.processReplyMethod = processReplyMethod;
  }

  /**
   * Check the replyRules for the InquiryDetails.
   *
   * @param inquiryDetails the InquiryDetails
   */
  public void processReplyRules(InquiryDetails inquiryDetails) {
    Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
    Integer brokerId = inquiry.getBrokerId();
    if (brokerId == null) {
      // If the broker Id is null, this is from an upload, not an inquiry. For now, return here.
      // Maybe use this to handle the upload itself?
      return;
    }

    List<InquiryHandlingRule> inquiryHandlingRules = InquiryHandlingRuleUtil
        .fetchInquiryHandlingRulesForBrokerId(brokerId);

    // TODO: if more reply rules are defined, this has to be smarter. for now just check if any auto
    //  reply is defined for the broker
    Optional<ReplyRuleType> replyRule = inquiryHandlingRules.stream()
        .map(InquiryHandlingRule::getAutomaticReply).findFirst();

    if (!replyRule.isPresent()) {
      logger.debug("No automatic reply is found. Abort replying.");
      return;
    }

    logger.debug("Automatic reply is set to: " + replyRule.get());

    switch (replyRule.get()) {
      case RR_DATA:
        logger.info("Full dataset shall be sent. Not yet implemented.");
        break;
      case RR_TOTAL_COUNT:
        logger.info("Reporting the amount of matching datasets to the broker.");
        BrokerConnector brokerConnector = new BrokerConnector(BrokerUtil.fetchBrokerById(brokerId));
        processReplyMethod.accept(brokerConnector);

        break;
      case RR_NO_AUTOMATIC_ACTION:
      default:
        logger.info("No automatic replies configured for this broker.");
        break;
    }
  }

}
