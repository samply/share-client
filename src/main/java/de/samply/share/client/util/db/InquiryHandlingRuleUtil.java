package de.samply.share.client.util.db;

import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.InquiryHandlingRuleDao;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryHandlingRule;
import de.samply.share.client.model.db.tables.records.InquiryHandlingRuleRecord;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with inquiry handling rule objects.
 */
public class InquiryHandlingRuleUtil {

  private static final Logger logger = LogManager.getLogger(InquiryHandlingRuleUtil.class);

  private static final InquiryHandlingRuleDao inquiryHandlingRuleDao;

  static {
    inquiryHandlingRuleDao = new InquiryHandlingRuleDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryHandlingRuleUtil() {
  }

  /**
   * Get the inquiry handling rule DAO.
   *
   * @return the inquiry handling rule DAO
   */
  public static InquiryHandlingRuleDao getInquiryHandlingRuleDao() {
    return inquiryHandlingRuleDao;
  }

  /**
   * Get the list of all inquiry handling rules.
   *
   * @return the list of all inquiry handling rules
   */
  public static List<InquiryHandlingRule> fetchInquiryHandlingRules() {
    return inquiryHandlingRuleDao.findAll();
  }

  /**
   * Get the list of inquiry handling rules for a certain broker.
   *
   * @param brokerId the id of the broker for which the handling rules are wanted
   * @return the list of handling rules for that broker
   */
  public static List<InquiryHandlingRule> fetchInquiryHandlingRulesForBrokerId(int brokerId) {
    return inquiryHandlingRuleDao.fetchByBrokerId(brokerId);
  }

  /**
   * Insert a new inquiry handling rule into the database.
   *
   * @param inquiryHandlingRule the new inquiry handling rule to insert
   * @return the assigned database id of the newly inserted inquiry handling rule
   */
  public static int insertInquiryHandlingRule(InquiryHandlingRule inquiryHandlingRule) {
    DSLContext dslContext = ResourceManager.getDslContext();
    InquiryHandlingRuleRecord inquiryHandlingRuleRecord = dslContext
        .newRecord(Tables.INQUIRY_HANDLING_RULE, inquiryHandlingRule);
    inquiryHandlingRuleRecord.store();
    inquiryHandlingRuleRecord.refresh();
    return inquiryHandlingRuleRecord.getId();
  }

  /**
   * Update a list of inquiry handling rules in the database.
   *
   * @param inquiryHandlingRules the list of inquiry handling rules to update
   */
  public static void updateInquiryHandlingRules(List<InquiryHandlingRule> inquiryHandlingRules) {
    inquiryHandlingRuleDao.update(inquiryHandlingRules);
  }

  /**
   * Check if results shall be requested for a given inquiry. Checks inquiry handling rules for the
   * broker it was received from.
   *
   * @param inquiry the inquiry for which to check
   * @return if the results shall be generated
   */
  public static boolean requestResultsForInquiry(Inquiry inquiry) {
    if (ApplicationUtils.isSamply()) {
      return false;
    }

    try {
      // TODO: when there are more inquiry handling rules, a more sophisticated approach is needed
      for (InquiryHandlingRule inquiryHandlingRule : InquiryHandlingRuleUtil
          .fetchInquiryHandlingRulesForBrokerId(inquiry.getBrokerId())) {
        if (inquiryHandlingRule.getFullResult()) {
          return true;
        }
      }
    } catch (NullPointerException npe) {
      // in case anything is null, reply false
      return false;
    }
    return false;
  }
}
