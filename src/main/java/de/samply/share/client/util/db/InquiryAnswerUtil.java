package de.samply.share.client.util.db;

import de.samply.share.client.model.db.tables.daos.InquiryAnswerDao;
import de.samply.share.client.model.db.tables.pojos.InquiryAnswer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper Class for CRUD operations with inquiry answer objects.
 */
public class InquiryAnswerUtil {

  private static final Logger logger = LogManager.getLogger(InquiryAnswerUtil.class);

  private static final InquiryAnswerDao inquiryAnswerDao;

  static {
    inquiryAnswerDao = new InquiryAnswerDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryAnswerUtil() {
  }

  /**
   * Get the inquiry answer DAO.
   *
   * @return the inquiry answer DAO
   */
  public static InquiryAnswerDao getInquiryAnswerDao() {
    return inquiryAnswerDao;
  }

  /**
   * Get one inquiry answer.
   *
   * @param id id of the inquiry answer
   * @return the inquiry answer
   */
  public static InquiryAnswer fetchInquiryAnswerById(int id) {
    return inquiryAnswerDao.fetchOneById(id);
  }

  /**
   * Get the inquiry answer belonging to a certain inquiry details object.
   *
   * @param inquiryDetailsId the id of the inquiry details object
   * @return the answer belonging to the inquiry details object
   */
  public static InquiryAnswer fetchInquiryAnswerByInquiryDetailsId(int inquiryDetailsId) {
    return inquiryAnswerDao.fetchOneByInquiryDetailsId(inquiryDetailsId);
  }

  /**
   * Insert a new inquiry answer into the database.
   *
   * @param inquiryAnswer the inquiry answer to insert
   */
  public static void insertInquiryAnswer(InquiryAnswer inquiryAnswer) {
    inquiryAnswerDao.insert(inquiryAnswer);
  }
}
