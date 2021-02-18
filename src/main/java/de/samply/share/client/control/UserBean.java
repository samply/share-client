package de.samply.share.client.control;

import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.tables.pojos.RequestedEntity;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.connector.StoreConnector;
import de.samply.share.client.util.db.RequestedEntityUtil;
import de.samply.share.client.util.db.UserUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.omnifaces.util.Messages;

/**
 * ViewScoped backing bean that is used on the user list.
 */
@ManagedBean(name = "userBean")
@ViewScoped
public class UserBean implements Serializable {

  private static final Logger logger = LogManager.getLogger(UserBean.class);

  private List<User> userList;
  private User newUser;
  private Map<EntityType, Boolean> newUserNotifications;
  private String password;
  private String passwordRepeat;
  private String newPass;
  private String newPassRepeat;
  private String userForPwChange;

  public List<User> getUserList() {
    return userList;
  }

  public void setUserList(List<User> userList) {
    this.userList = userList;
  }

  public User getNewUser() {
    return newUser;
  }

  public void setNewUser(User newUser) {
    this.newUser = newUser;
  }

  public Map<EntityType, Boolean> getNewUserNotifications() {
    return newUserNotifications;
  }

  public void setNewUserNotifications(Map<EntityType, Boolean> newUserNotifications) {
    this.newUserNotifications = newUserNotifications;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String passwordRepeat) {
    this.passwordRepeat = passwordRepeat;
  }

  public String getNewPass() {
    return newPass;
  }

  public void setNewPass(String newPass) {
    this.newPass = newPass;
  }

  public String getNewPassRepeat() {
    return newPassRepeat;
  }

  public void setNewPassRepeat(String newPassRepeat) {
    this.newPassRepeat = newPassRepeat;
  }

  public String getUserForPwChange() {
    return userForPwChange;
  }

  public void setUserForPwChange(String userForPwChange) {
    this.userForPwChange = userForPwChange;
  }

  /**
   * Initialize the user list.
   */
  @PostConstruct
  public void init() {
    refreshUserList();
    newUser = new User();
    newUserNotifications = new HashMap<>();
    for (EntityType entityType : EntityType.values()) {
      if (ApplicationUtils.isSamply() && entityType == EntityType.E_BIOMATERIAL) {
        newUserNotifications.put(entityType, true);
      } else {
        newUserNotifications.put(entityType, false);
      }
    }
  }

  /**
   * Reload the user list from the database.
   */
  private void refreshUserList() {
    userList = UserUtil.fetchUsers();
  }

  /**
   * Delete a user from the database and refresh the user list.
   *
   * @param user the user to delete
   */
  public void deleteUser(User user) {
    UserUtil.deleteUser(user);
    refreshUserList();
    StoreConnector.deactivateUser(user.getUsername());
  }

  /**
   * Insert a new user to the database.
   *
   * @return navigation information
   */
  public String storeNewUser() {
    newUser.setAdminPrivilege(false);
    newUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
    if (UserUtil.insertUser(newUser)) {
      Messages.create("ul_createNewUser")
          .detail("ul_userCreated")
          .add();
      setNotificationsForNewUser();
      newUser = new User();
      refreshUserList();
    } else {
      Messages.create("ul_createNewUser")
          .detail("ul_userNameExists")
          .error().add();
    }
    return "user_list?faces-redirect=true";
  }

  /**
   * Assign event notifications to a user.
   */
  private void setNotificationsForNewUser() {
    newUser = UserUtil.fetchUserByName(newUser.getUsername());
    List<RequestedEntity> notifications = new ArrayList<>();
    for (Map.Entry<EntityType, Boolean> entry : newUserNotifications.entrySet()) {
      if (entry.getValue()) {
        notifications.add(RequestedEntityUtil.getRequestedEntityForValue(entry.getKey()));
      }
    }
    UserUtil.setNotificationEntitiesForUser(newUser, notifications);
  }

  /**
   * Get the user notification settings for a user.
   *
   * @param user the user
   * @return user notification settings
   */
  public Map<EntityType, Boolean> getUserNotifications(User user) {
    Map<EntityType, Boolean> userNotifications = new HashMap<>();
    for (EntityType entityType : EntityType.values()) {
      userNotifications.put(entityType, false);
    }

    List<RequestedEntity> userNotificationsList = getNotificationSettings(user);
    for (RequestedEntity re : userNotificationsList) {
      userNotifications.put(re.getName(), true);
    }
    return userNotifications;
  }

  private List<RequestedEntity> getNotificationSettings(User user) {
    return UserUtil.getNotificationEntitiesForUser(user);
  }

  /**
   * Add or delete user notifications for a entityType.
   *
   * @param user   the user
   * @param entity the entity
   */
  public void toggleUserNotification(User user, EntityType entity) {
    Map<EntityType, Boolean> userNotifications = getUserNotifications(user);
    RequestedEntity requestedEntity = RequestedEntityUtil.getRequestedEntityForValue(entity);

    if (userNotifications.get(entity)) {
      UserUtil.removeNotificationEntityFromUser(user, requestedEntity);
    } else {
      UserUtil.addNotificationEntityToUser(user, requestedEntity);
    }
  }

  /**
   * Change user password.
   */
  public void changeUserPassword() {
    User userToCheck = UserUtil.fetchUserByName(userForPwChange);

    if (userToCheck != null) {
      if (newPass.equals(newPassRepeat)) {
        userToCheck.setPasswordHash(BCrypt.hashpw(newPass, BCrypt.gensalt()));
        UserUtil.updateUser(userToCheck);
        StoreConnector.changeUserPassword(userToCheck.getUsername(), password, newPass);
        logger.info("Password changed for user ", userToCheck.getId());
        Messages.create("common_passwordChanged")
            .detail("common_passwordChanged")
            .add();
      } else {
        logger.info("Password mismatch");
        Messages.create("common_passwordMismatch")
            .detail("common_passwordMismatch")
            .error()
            .add();
      }
    }
  }
}
