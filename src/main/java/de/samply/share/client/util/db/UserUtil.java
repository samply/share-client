package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.daos.UserDao;
import de.samply.share.client.model.db.tables.daos.UserNotificationDao;
import de.samply.share.client.model.db.tables.pojos.RequestedEntity;
import de.samply.share.client.model.db.tables.pojos.Token;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.model.db.tables.pojos.UserNotification;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with user objects.
 */
public class UserUtil {

  private static final Logger logger = LogManager.getLogger(UserUtil.class);

  private static final UserDao userDao;
  private static final UserNotificationDao userNotificationDao;

  static {
    userDao = new UserDao(ResourceManager.getConfiguration());
    userNotificationDao = new UserNotificationDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private UserUtil() {
  }

  /**
   * Get the user DAO.
   *
   * @return the user DAO
   */
  public static UserDao getUserDao() {
    return userDao;
  }

  /**
   * Get one user by its id.
   *
   * @param id id of the user
   * @return the user
   */
  public static User fetchUserById(int id) {
    return userDao.fetchOneById(id);
  }

  /**
   * Get one user by its username.
   *
   * @param name name of the user
   * @return the user
   */
  public static User fetchUserByName(String name) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext.selectFrom(Tables.USER)
        .where(Tables.USER.USERNAME.equalIgnoreCase(name))
        .fetchOneInto(User.class);
  }

  /**
   * Get one user by its login token.
   *
   * @param tokenString login token
   * @return the user
   */
  public static User fetchUserByToken(String tokenString) {
    Date now = new Date();
    long time = now.getTime();
    Timestamp timestamp = new Timestamp(time);

    DSLContext dslContext = ResourceManager.getDslContext();
    Token token = dslContext.select(Tables.TOKEN.ID)
        .from(Tables.USER)
        .join(Tables.TOKEN).onKey()
        .where(Tables.TOKEN.SIGNIN_TOKEN.equal(tokenString))
        .and(Tables.TOKEN.EXPIRES_AT.greaterOrEqual(timestamp))
        .fetchOneInto(Token.class);

    if (token != null) {
      return userDao.fetchOneByTokenId(token.getId());
    }
    return null;
  }

  /**
   * Get a list of all users.
   *
   * @return list of all users
   */
  public static List<User> fetchUsers() {
    return userDao.findAll();
  }

  /**
   * Update a user in the database.
   *
   * @param user the user to update
   */
  public static void updateUser(User user) {
    userDao.update(user);
  }

  /**
   * Delete a user from the database.
   *
   * @param user the user to delete
   */
  public static void deleteUser(User user) {
    if (user != null) {
      userDao.delete(user);
      EventLogUtil.insertEventLogEntry(EventMessageType.E_USER_DELETED, user.getUsername());
    }
  }

  /**
   * Insert a new user into the database.
   *
   * @param user the new user to insert
   * @return success of the insert operation
   */
  public static boolean insertUser(User user) {
    if (fetchUserByName(user.getUsername()) != null) {
      return false;
    }
    userDao.insert(user);
    EventLogUtil.insertEventLogEntry(EventMessageType.E_USER_CREATED, user.getUsername());
    return true;
  }

  /**
   * Get the list of entities about which a user will be notified in case of an incoming inquiry.
   *
   * @param user the user for whom to get the list of entities
   * @return the list of entities
   */
  public static List<RequestedEntity> getNotificationEntitiesForUser(User user) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext.select(Tables.REQUESTED_ENTITY.ID, Tables.REQUESTED_ENTITY.NAME)
        .from(Tables.USER)
        .join(Tables.USER_NOTIFICATION).onKey()
        .join(Tables.REQUESTED_ENTITY).onKey()
        .where(Tables.USER.ID.equal(user.getId()))
        .fetchInto(RequestedEntity.class);
  }

  /**
   * Set the list of entities about which a user will be notified in case of an incoming inquiry.
   *
   * @param user     the user for whom to set the list of entities
   * @param entities the new list of entities
   */
  public static void setNotificationEntitiesForUser(User user, List<RequestedEntity> entities) {
    clearNotificationEntitiesForUser(user);
    for (RequestedEntity entity : entities) {
      addNotificationEntityToUser(user, entity);
    }
  }

  /**
   * Add an entity about which a user will be notified in case of an incoming inquiry.
   *
   * @param user   the user for whom to set the new entity
   * @param entity the new entity
   */
  public static void addNotificationEntityToUser(User user, RequestedEntity entity) {
    // PostgreSQL doesn't support "on duplicate key ignore" function...
    // so we first have to check if it's already existing
    DSLContext dslContext = ResourceManager.getDslContext();
    UserNotification userNotification = dslContext.selectFrom(Tables.USER_NOTIFICATION)
        .where(Tables.USER_NOTIFICATION.USER_ID.equal(user.getId()))
        .and(Tables.USER_NOTIFICATION.REQUESTED_ENTITY_ID.equal(entity.getId()))
        .fetchOneInto(UserNotification.class);
    if (userNotification == null) {
      userNotification = new UserNotification();
      userNotification.setUserId(user.getId());
      userNotification.setRequestedEntityId(entity.getId());
      userNotificationDao.insert(userNotification);
    }
  }

  /**
   * Remove an entity about which a user will be notified in case of an incoming inquiry.
   *
   * @param user   the user from whom to remove the entity
   * @param entity the entity to remove
   */
  public static void removeNotificationEntityFromUser(User user, RequestedEntity entity) {
    DSLContext dslContext = ResourceManager.getDslContext();
    dslContext.deleteFrom(Tables.USER_NOTIFICATION)
        .where(Tables.USER_NOTIFICATION.USER_ID.equal(user.getId()))
        .and(Tables.USER_NOTIFICATION.REQUESTED_ENTITY_ID.equal(entity.getId()))
        .execute();
  }

  /**
   * Clear all notification settings for an user.
   *
   * @param user the user for whom to clear the notification settings
   */
  public static void clearNotificationEntitiesForUser(User user) {
    DSLContext dslContext = ResourceManager.getDslContext();
    dslContext.deleteFrom(Tables.USER_NOTIFICATION)
        .where(Tables.USER_NOTIFICATION.USER_ID.equal(user.getId()))
        .execute();
  }

  /**
   * Get a list of users that shall be notified about an entity type.
   *
   * @param entityType the entity type for which the users are wanted
   * @return the list of users to be notified
   */
  public static List<User> getUsersToNotify(EntityType entityType) {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext.select()
        .from(Tables.USER)
        .join(Tables.USER_NOTIFICATION).onKey()
        .join(Tables.REQUESTED_ENTITY).onKey()
        .where(Tables.REQUESTED_ENTITY.NAME.equal(entityType))
        .fetchInto(User.class);
  }
}
