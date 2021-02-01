package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.UserSeenInquiryDao;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.model.db.tables.pojos.UserSeenInquiry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with user seen inquiry relations.
 */
public class UserSeenInquiryUtil {

  private static final Logger logger = LogManager.getLogger(UserSeenInquiryUtil.class);

  private static final UserSeenInquiryDao userSeenInquiryDao;

  static {
    userSeenInquiryDao = new UserSeenInquiryDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private UserSeenInquiryUtil() {
  }

  /**
   * Get the user seen inquiry DAO.
   *
   * @return the user seen inquiry DAO
   */
  public static UserSeenInquiryDao getUserSeenInquiryDao() {
    return userSeenInquiryDao;
  }

  /**
   * Get a user seen inquiry relation.
   *
   * @param userId    the id of the user
   * @param inquiryId the id of the inquiry
   * @return an object if the user has seen the inquiry, null otherwise
   */
  public static UserSeenInquiry fetchUserSeenInquiryByUserIdAndInquiryId(int userId,
      int inquiryId) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext
        .selectFrom(Tables.USER_SEEN_INQUIRY)
        .where(Tables.USER_SEEN_INQUIRY.USER_ID.equal(userId))
        .and(Tables.USER_SEEN_INQUIRY.INQUIRY_ID.equal(inquiryId))
        .fetchOneInto(UserSeenInquiry.class);
  }

  /**
   * Get a user seen inquiry relation.
   *
   * @param user    the user
   * @param inquiry the inquiry
   * @return an object if the user has seen the inquiry, null otherwise
   */
  public static UserSeenInquiry fetchUserSeenInquiryByUserAndInquiry(User user, Inquiry inquiry) {
    return fetchUserSeenInquiryByUserIdAndInquiryId(user.getId(), inquiry.getId());
  }

  /**
   * Check if a user has seen a certain inquiry.
   *
   * @param userId    the id of the user
   * @param inquiryId the id of the inquiry
   * @return if the user has seen the inquiry
   */
  public static boolean hasUserSeenInquiryByIds(int userId, int inquiryId) {
    return fetchUserSeenInquiryByUserIdAndInquiryId(userId, inquiryId) != null;
  }

  /**
   * Check if a user has seen a certain inquiry.
   *
   * @param user    the user
   * @param inquiry the inquiry
   * @return if the user has seen the inquiry
   */
  public static boolean hasUserSeenInquiry(User user, Inquiry inquiry) {
    return fetchUserSeenInquiryByUserAndInquiry(user, inquiry) != null;
  }

  /**
   * Mark an inquiry as seen by the user.
   *
   * @param user    the user
   * @param inquiry the inquiry
   */
  public static void setUserSeenInquiry(User user, Inquiry inquiry) {
    if (!hasUserSeenInquiry(user, inquiry)) {
      insertUserSeenInquiry(new UserSeenInquiry(user.getId(), inquiry.getId()));
    }
  }

  /**
   * Insert a new user has seen inquiry relation to the database.
   *
   * @param usi the user has seen inquiry object
   */
  private static void insertUserSeenInquiry(UserSeenInquiry usi) {
    userSeenInquiryDao.insert(usi);
  }

  /**
   * Delete a user has seen inquiry relation from the database.
   *
   * @param user    the user
   * @param inquiry the inquiry
   */
  private static void removeUserSeenInquiry(User user, Inquiry inquiry) {
    UserSeenInquiry usi = fetchUserSeenInquiryByUserAndInquiry(user, inquiry);
    if (usi != null) {
      userSeenInquiryDao.delete(usi);
    }
  }
}
