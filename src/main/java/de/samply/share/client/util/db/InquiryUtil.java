package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.daos.InquiryDao;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.RequestedEntity;
import de.samply.share.client.model.db.tables.pojos.Upload;
import de.samply.share.client.model.db.tables.records.InquiryRecord;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with inquiry objects.
 */
public class InquiryUtil {

  private static final Logger logger = LogManager.getLogger(InquiryUtil.class);

  private static final InquiryDao inquiryDao;

  static {
    inquiryDao = new InquiryDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryUtil() {
  }

  /**
   * Get the inquiry DAO.
   *
   * @return the inquiry DAO
   */
  public static InquiryDao getInquiryDao() {
    return inquiryDao;
  }

  /**
   * Get one inquiry.
   *
   * @param id id of the inquiry
   * @return the inquiry
   */
  public static Inquiry fetchInquiryById(int id) {
    return inquiryDao.fetchOneById(id);
  }

  /**
   * Insert a new inquiry into the database.
   *
   * @param inquiry the new inquiry to insert
   * @return the assigned database id of the newly inserted inquiry
   */
  public static int insertInquiry(Inquiry inquiry) {
    DSLContext dslContext = ResourceManager.getDslContext();
    InquiryRecord inquiryRecord = dslContext.newRecord(Tables.INQUIRY, inquiry);
    inquiryRecord.store();
    inquiryRecord.refresh();
    return inquiryRecord.getId();
  }

  /**
   * Update an inquiry in the database.
   *
   * @param inquiry the inquiry to update
   */
  public static void updateInquiry(Inquiry inquiry) {
    inquiryDao.update(inquiry);
  }

  /**
   * Get an inquiry by its source id and the broker id.
   *
   * @param sourceId the id under which the broker keeps that inquiry
   * @param brokerId the id of the broker
   * @return the inquiry with the given source id from the given broker
   */
  public static Inquiry fetchInquiryBySourceIdAndBrokerId(int sourceId, int brokerId) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.INQUIRY)
        .where(
            Tables.INQUIRY.SOURCE_ID.equal(sourceId)
                .and(Tables.INQUIRY.BROKER_ID.equal(brokerId))
        )
        .fetchOneInto(Inquiry.class);
  }

  /**
   * Get all inquiries associated with a given upload.
   *
   * @param upload the upload for which the inquiries are wanted
   * @return the list of inquiries associated with the given upload
   */
  public static List<Inquiry> fetchInquiriesForUpload(Upload upload) {
    return fetchInquiriesForUploadId(upload.getId());
  }

  /**
   * Get all inquiries associated with a given upload.
   *
   * @param uploadId the id of the upload for which the inquiries are wanted
   * @return the list of inquiries associated with the given upload
   */
  private static List<Inquiry> fetchInquiriesForUploadId(int uploadId) {
    return inquiryDao.fetchByUploadId(uploadId);
  }

  /**
   * Get the latest inquiry associated with a given upload.
   *
   * @param upload the upload for which the latest inquiry is wanted
   * @return the latest inquiry associated with the given upload
   */
  public static Inquiry fetchLatestInquiryForUpload(Upload upload) {
    return fetchLatestInquiryForUploadId(upload.getId());
  }

  /**
   * Get the latest inquiry associated with a given upload.
   *
   * @param uploadId the id of the upload for which the inquiries are wanted
   * @return the latest inquiry associated with the given upload
   */
  private static Inquiry fetchLatestInquiryForUploadId(int uploadId) {
    List<Inquiry> inquiries = fetchInquiriesForUploadId(uploadId);
    if (inquiries.isEmpty()) {
      return null;
    } else {
      return inquiries.get(inquiries.size() - 1);
    }
  }

  /**
   * Get a list of inquiries with a certain status, ordered by the date they were received.
   *
   * @param statusTypes list of wanted status types
   * @return chronologically ordered list of inquiries
   */
  public static List<Inquiry> fetchInquiriesOrderByReceivedAt(InquiryStatusType... statusTypes) {
    DSLContext dslContext = ResourceManager.getDslContext();

    return dslContext
        .select(Tables.INQUIRY.fields())
        .from(Tables.INQUIRY)
        .join(Tables.INQUIRY_DETAILS)
        .on(Tables.INQUIRY.LATEST_DETAILS_ID.equal(Tables.INQUIRY_DETAILS.ID))
        .where(Tables.INQUIRY_DETAILS.STATUS.in(statusTypes))
        .and(Tables.INQUIRY.BROKER_ID.isNotNull())
        .orderBy(Tables.INQUIRY_DETAILS.RECEIVED_AT)
        .fetchInto(Inquiry.class);
  }

  /**
   * Get a list of entities that were requested with the given inquiry.
   *
   * @param inquiry the inquiry for which the requested entities are wanted
   * @return the list of requested entities for this inquiry
   */
  public static List<RequestedEntity> getRequestedEntitiesForInquiry(Inquiry inquiry) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext.select(Tables.REQUESTED_ENTITY.ID, Tables.REQUESTED_ENTITY.NAME)
        .from(Tables.INQUIRY)
        .join(Tables.INQUIRY_REQUESTED_ENTITY).onKey()
        .join(Tables.REQUESTED_ENTITY).onKey()
        .where(Tables.INQUIRY.ID.equal(inquiry.getId()))
        .fetchInto(RequestedEntity.class);
  }

  /**
   * Get the amount of inquiries of a certain status.
   *
   * @param status list of wanted status types
   * @return the number of inquiries with the given status
   */
  public static Integer countInquiries(InquiryStatusType... status) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .fetchCount(
            dslContext.select()
                .from(Tables.INQUIRY_DETAILS)
                .join(Tables.INQUIRY)
                .on(Tables.INQUIRY_DETAILS.INQUIRY_ID.equal(Tables.INQUIRY.ID))
                .where(Tables.INQUIRY_DETAILS.STATUS.in(status))
                .and(Tables.INQUIRY.BROKER_ID.isNotNull())
        );
  }
}
