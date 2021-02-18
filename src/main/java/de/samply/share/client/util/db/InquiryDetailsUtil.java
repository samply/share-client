package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.daos.InquiryDetailsDao;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.records.InquiryDetailsRecord;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.impl.DSL;

/**
 * Helper Class for CRUD operations with inquiry details objects.
 */
public class InquiryDetailsUtil {

  private static final Logger logger = LogManager.getLogger(InquiryDetailsUtil.class);

  private static final InquiryDetailsDao inquiryDetailsDao;

  static {
    inquiryDetailsDao = new InquiryDetailsDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryDetailsUtil() {
  }

  /**
   * Get the inquiry details DAO.
   *
   * @return the inquiry details DAO
   */
  public static InquiryDetailsDao getInquiryDetailsDao() {
    return inquiryDetailsDao;
  }

  /**
   * Get one set of inquiry details.
   *
   * @param id id of the set of inquiry details
   * @return the set of inquiry details
   */
  public static InquiryDetails fetchInquiryDetailsById(int id) {
    return inquiryDetailsDao.fetchOneById(id);
  }

  /**
   * Update a set of inquiry details in the database.
   *
   * @param inquiryDetails the set of inquiry details to update
   */
  public static void updateInquiryDetails(InquiryDetails inquiryDetails) {
    inquiryDetailsDao.update(inquiryDetails);
  }

  /**
   * Update a list of inquiry details in the database.
   *
   * @param inquiryDetailsList the list of inquiry details to update
   */
  public static void updateInquiryDetails(List<InquiryDetails> inquiryDetailsList) {
    inquiryDetailsDao.update(inquiryDetailsList);
  }

  /**
   * Get all sets of inquiry details for one inquiry.
   *
   * @param inquiry the inquiry for which the details are wanted
   * @return the list of inquiry details associated with the given inquiry
   */
  public static List<InquiryDetails> getInquiryDetailsForInquiry(Inquiry inquiry) {
    return inquiryDetailsDao.fetchByInquiryId(inquiry.getId());
  }

  /**
   * Get all inquiry details with a certain status.
   *
   * @param status the wanted status
   * @return the list of inquiry details with the given status
   */
  public static List<InquiryDetails> getInquiryDetailsByStatus(InquiryStatusType status) {
    return inquiryDetailsDao.fetchByStatus(status);
  }

  /**
   * Insert a new set of inquiry details into the database.
   *
   * @param inquiryDetails the new set of inquiry details to insert
   * @return the assigned database id of the newly inserted set of inquiry details
   */
  public static int insertInquiryDetails(InquiryDetails inquiryDetails) {
    DSLContext dslContext = ResourceManager.getDslContext();
    InquiryDetailsRecord inquiryDetailsRecord = dslContext
        .newRecord(Tables.INQUIRY_DETAILS, inquiryDetails);
    inquiryDetailsRecord.store();
    inquiryDetailsRecord.refresh();

    // Update the latest inquirydetails id on the inquiry
    Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
    inquiry.setLatestDetailsId(inquiryDetailsRecord.getId());
    InquiryUtil.updateInquiry(inquiry);

    return inquiryDetailsRecord.getId();
  }

  /**
   * Get the inquiry details with a certain revision number for an inquiry.
   *
   * @param inquiry  the inquiry for which the details should be retrieved
   * @param revision the revision number
   * @return the set of inquiry details
   */
  public static InquiryDetails getInquiryDetailsForInquiryWithRevision(Inquiry inquiry,
      int revision) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.INQUIRY_DETAILS)
        .where(
            Tables.INQUIRY_DETAILS.REVISION.equal(revision)
                .and(Tables.INQUIRY_DETAILS.INQUIRY_ID.equal(inquiry.getId()))
        )
        .fetchOneInto(InquiryDetails.class);
  }

  /**
   * Get all inquiry details that are older than a given threshold.
   *
   * @param thresholdDays the amount of days
   * @return a list of inquiry details older than the given threshold
   */
  public static List<InquiryDetails> getInquiryDetailsOlderThanDays(int thresholdDays) {
    DSLContext dslContext = ResourceManager.getDslContext();

    return dslContext
        .selectFrom(Tables.INQUIRY_DETAILS)
        .where(
            DSL.currentTimestamp().greaterThan(
                DSL.timestampAdd(Tables.INQUIRY_DETAILS.RECEIVED_AT, thresholdDays, DatePart.DAY))
                .and(Tables.INQUIRY_DETAILS.STATUS.notEqual(InquiryStatusType.IS_ARCHIVED))
        )
        .fetchInto(InquiryDetails.class);
  }

  /**
   * Get the last scheduled inquiry.
   *
   * @return the inquiryDetails of the inquiry
   */
  public static InquiryDetails getLastScheduledInquiry() {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.INQUIRY_DETAILS)
        .where(Tables.INQUIRY_DETAILS.SCHEDULED_AT.equal(
            dslContext
                .select(DSL.max(Tables.INQUIRY_DETAILS.SCHEDULED_AT))
                .from(Tables.INQUIRY_DETAILS)
        ))
        .fetchOneInto(InquiryDetails.class);
  }
}
