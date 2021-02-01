package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.tables.daos.InquiryResultDao;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.records.InquiryResultRecord;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 * Helper Class for CRUD operations with inquiry result objects.
 */
public class InquiryResultUtil {

  private static final Logger logger = LogManager.getLogger(InquiryResultUtil.class);

  private static final InquiryResultDao inquiryResultDao;

  static {
    inquiryResultDao = new InquiryResultDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryResultUtil() {
  }

  /**
   * Get the inquiry result DAO.
   *
   * @return the inquiry result DAO
   */
  public static InquiryResultDao getInquiryResultDao() {
    return inquiryResultDao;
  }

  /**
   * Get a list of all inquiry results.
   *
   * @return list of all inquiry results
   */
  public static List<InquiryResult> fetchInquiryResults() {
    return inquiryResultDao.findAll();
  }

  /**
   * Get a list of all inquiry results for certain inquiry details.
   *
   * @param inquiryDetailsId the id of the inquiry details
   * @return the list of inquiry results
   */
  public static List<InquiryResult> fetchInquiryResultsForInquiryDetailsById(int inquiryDetailsId) {
    return inquiryResultDao.fetchByInquiryDetailsId(inquiryDetailsId);
  }

  /**
   * Get the last two inquiry result (patient,specimen) for a cql query.
   *
   * @param inquiryDetailsId the id of the inquiry details
   * @return the list of inquiry results
   */
  public static List<InquiryResult> fetchLastTwoInquiryResult(int inquiryDetailsId) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.INQUIRY_RESULT)
        .where(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(inquiryDetailsId))
        .orderBy(Tables.INQUIRY_RESULT.EXECUTED_AT.desc()).limit(2)
        .fetchInto(InquiryResult.class);
  }

  /**
   * Get one inquiry result.
   *
   * @param inquiryResultId id of the inquiry result
   * @return
   */
  public static InquiryResult fetchInquiryResultById(int inquiryResultId) {
    return inquiryResultDao.fetchOneById(inquiryResultId);
  }

  /**
   * Get the last result for a inquiryCriteria by Id.
   *
   * @param inquiryCriteriaId id of inquiryCriteria
   * @return InquiryResult of the InquiryCriteria
   */
  public static InquiryResult fetchLatestInquiryResultForInquiryCriteriaById(
      int inquiryCriteriaId) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.INQUIRY_RESULT)
        .where(Tables.INQUIRY_RESULT.INQUIRY_CRITERIA_ID.equal(inquiryCriteriaId))
        .and(Tables.INQUIRY_RESULT.EXECUTED_AT.equal(
            dslContext
                .select(DSL.max(Tables.INQUIRY_RESULT.EXECUTED_AT))
                .from(Tables.INQUIRY_RESULT)
                .where(Tables.INQUIRY_RESULT.INQUIRY_CRITERIA_ID.equal(inquiryCriteriaId))
        ))
        .fetchOneInto(InquiryResult.class);
  }

  /**
   * Get the latest inquiry result for certain inquiry details.
   *
   * @param inquiryDetailsId the id of the inquiry details
   * @return the latest inquiry result belonging to the given inquiry details
   */
  public static InquiryResult fetchLatestInquiryResultForInquiryDetailsById(int inquiryDetailsId) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.INQUIRY_RESULT)
        .where(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(inquiryDetailsId))
        .and(Tables.INQUIRY_RESULT.EXECUTED_AT.equal(
            dslContext
                .select(DSL.max(Tables.INQUIRY_RESULT.EXECUTED_AT))
                .from(Tables.INQUIRY_RESULT)
                .where(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(inquiryDetailsId))
        ))
        .fetchOneInto(InquiryResult.class);
  }

  /**
   * Insert a new inquiry result into the database.
   *
   * @param inquiryResult the new inquiry result to insert
   * @return the assigned database id of the newly inserted inquiry result
   */
  public static int insertInquiryResult(InquiryResult inquiryResult) {
    DSLContext dslContext = ResourceManager.getDslContext();
    InquiryResultRecord inquiryResultRecord = dslContext
        .newRecord(Tables.INQUIRY_RESULT, inquiryResult);
    inquiryResultRecord.store();
    inquiryResultRecord.refresh();
    return inquiryResultRecord.getId();
  }

  /**
   * Update an inquiry result in the database.
   *
   * @param inquiryResult the inquiry result to update
   */
  public static void updateInquiryResult(InquiryResult inquiryResult) {
    inquiryResultDao.update(inquiryResult);
  }

  /**
   * Get a list of inquiries for a certain entity type, where no notifications have been sent yet.
   *
   * @param entityType   the requested entity type
   * @param includeEmpty if set to true, results with 0 matching data sets will be included
   * @return the list of inquiry results
   */
  public static List<InquiryResult> getInquiryResultsForNotification(EntityType entityType,
      boolean includeEmpty) {
    if (includeEmpty) {
      return getInquiryResultsForNotificationIncludeEmpty(entityType);
    } else {
      return getInquiryResultsForNotification(entityType);
    }
  }


  /**
   * Get a list of inquiries for a certain entity type, where no notifications have been sent yet.
   * Do not include empty results.
   *
   * @param entityType the requested entity type
   * @return the list of inquiry results
   */
  public static List<InquiryResult> getInquiryResultsForNotification(EntityType entityType) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .select(Tables.INQUIRY_RESULT.fields())
        .from(Tables.INQUIRY).join(Tables.INQUIRY_DETAILS)
        .on(Tables.INQUIRY_DETAILS.INQUIRY_ID.equal(Tables.INQUIRY.ID))
        .join(Tables.INQUIRY_RESULT)
        .on(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(Tables.INQUIRY_DETAILS.ID))
        .join(Tables.INQUIRY_REQUESTED_ENTITY)
        .on(Tables.INQUIRY_REQUESTED_ENTITY.INQUIRY_ID.equal(Tables.INQUIRY.ID))
        .join(Tables.REQUESTED_ENTITY)
        .on(Tables.INQUIRY_REQUESTED_ENTITY.REQUESTED_ENTITY_ID.equal(Tables.REQUESTED_ENTITY.ID))
        .where(Tables.INQUIRY_RESULT.SIZE.isNotNull()
            .and(Tables.INQUIRY_RESULT.SIZE.greaterThan(0))
            .and(Tables.INQUIRY_RESULT.IS_ERROR.equal(Boolean.FALSE))
            .and(Tables.INQUIRY_RESULT.NOTIFICATION_SENT.equal(Boolean.FALSE))
            .and(Tables.REQUESTED_ENTITY.NAME.equal(entityType)))
        .fetchInto(InquiryResult.class);
  }


  /**
   * Get a list of inquiries for a certain entity type, where no notifications have been sent yet.
   * Include empty results.
   *
   * @param entityType the requested entity type
   * @return the list of inquiry results
   */
  public static List<InquiryResult> getInquiryResultsForNotificationIncludeEmpty(
      EntityType entityType) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .select(Tables.INQUIRY_RESULT.fields())
        .from(Tables.INQUIRY).join(Tables.INQUIRY_DETAILS)
        .on(Tables.INQUIRY_DETAILS.INQUIRY_ID.equal(Tables.INQUIRY.ID))
        .join(Tables.INQUIRY_RESULT)
        .on(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(Tables.INQUIRY_DETAILS.ID))
        .join(Tables.INQUIRY_REQUESTED_ENTITY)
        .on(Tables.INQUIRY_REQUESTED_ENTITY.INQUIRY_ID.equal(Tables.INQUIRY.ID))
        .join(Tables.REQUESTED_ENTITY)
        .on(Tables.INQUIRY_REQUESTED_ENTITY.REQUESTED_ENTITY_ID.equal(Tables.REQUESTED_ENTITY.ID))
        .where(Tables.INQUIRY_RESULT.SIZE.isNotNull()
            .and(Tables.INQUIRY_RESULT.IS_ERROR.equal(Boolean.FALSE))
            .and(Tables.INQUIRY_RESULT.NOTIFICATION_SENT.equal(Boolean.FALSE))
            .and(Tables.REQUESTED_ENTITY.NAME.equal(entityType)))
        .fetchInto(InquiryResult.class);
  }

  /**
   * Set the notification sent flag for a list of inquiryResults.
   *
   * @param inquiryResults the inquiry results for which to set the flag
   */
  public static void setNotificationSentForInquiryResults(List<InquiryResult> inquiryResults) {
    List<Integer> ids = new ArrayList<>();
    for (InquiryResult inquiryResult : inquiryResults) {
      ids.add(inquiryResult.getId());
    }
    DSLContext dslContext = ResourceManager.getDslContext();
    dslContext.update(Tables.INQUIRY_RESULT)
        .set(Tables.INQUIRY_RESULT.NOTIFICATION_SENT, true)
        .where(Tables.INQUIRY_RESULT.ID.in(ids))
        .execute();
  }
}
