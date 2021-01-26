package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.InquiryResultStatsDao;
import de.samply.share.client.model.db.tables.pojos.InquiryResultStats;
import de.samply.share.client.model.db.tables.records.InquiryResultStatsRecord;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with inquiry result stats objects.
 */
public class InquiryResultStatsUtil {

  private static final Logger logger = LogManager.getLogger(InquiryResultStatsUtil.class);

  private static final InquiryResultStatsDao inquiryResultStatsDao;

  static {
    inquiryResultStatsDao = new InquiryResultStatsDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryResultStatsUtil() {
  }

  /**
   * Get the inquiry result stats DAO.
   *
   * @return the inquiry result stats DAO
   */
  public static InquiryResultStatsDao getInquiryResultStatsDao() {
    return inquiryResultStatsDao;
  }

  /**
   * Get a list of all inquiry result stats.
   *
   * @return list of all inquiry result stats
   */
  public static List<InquiryResultStats> fetchInquiryResultStats() {
    return inquiryResultStatsDao.findAll();
  }

  /**
   * Get a list of all inquiry result stats for certain inquiry result.
   *
   * @param inquiryResultId the id of the inquiry result
   * @return the list of inquiry result stats
   */
  public static InquiryResultStats getInquiryResultStatsForInquiryResultById(int inquiryResultId) {
    return inquiryResultStatsDao.fetchOneByInquiryResultId(inquiryResultId);
  }

  /**
   * Get one inquiry result stats object.
   *
   * @param inquiryResultStatsId id of the inquiry result stats
   * @return
   */
  public static InquiryResultStats fetchInquiryResultStatsById(int inquiryResultStatsId) {
    return inquiryResultStatsDao.fetchOneById(inquiryResultStatsId);
  }

  /**
   * Insert new inquiry result stats into the database.
   *
   * @param inquiryResultStats the new inquiry result stats to insert
   * @return the assigned database id of the newly inserted inquiry result stats
   */
  public static int insertInquiryResultStats(InquiryResultStats inquiryResultStats) {
    DSLContext dslContext = ResourceManager.getDslContext();
    InquiryResultStatsRecord inquiryResultStatsRecord = dslContext
        .newRecord(Tables.INQUIRY_RESULT_STATS, inquiryResultStats);
    inquiryResultStatsRecord.store();
    inquiryResultStatsRecord.refresh();
    return inquiryResultStatsRecord.getId();
  }

  /**
   * Update inquiry result stats in the database.
   *
   * @param inquiryResultStats the inquiry result stats to update
   */
  public static void updateInquiryResultStats(InquiryResultStats inquiryResultStats) {
    inquiryResultStatsDao.update(inquiryResultStats);
  }
}
