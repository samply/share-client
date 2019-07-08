/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.control;

import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.connector.StoreConnector;
import de.samply.share.client.util.db.UserUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.omnifaces.context.OmniPartialViewContext;
import org.omnifaces.util.Messages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.TimeZone;

/**
 * A SessionScoped backing bean handling login information
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(LoginBean.class);

    /**
     * The Constant SESSION_USERNAME.
     */
    private static final String SESSION_USERNAME = "username";

    private static final String SESSION_USER = "user";

    public static final String REQUESTED_PAGE_PARAMETER = "requestedPage";
    public static final String ESCAPED_PARAMETER_SEPARATOR = "__";

    /**
     * The signin token.
     */
    private String signinToken;

    /**
     * The user.
     */
    private User user = new User();
    private String currentPass;
    private String newPass;
    private String newPassRepeat;
    private String newEmail;

    private String requestedPageParameter;

    public String getSigninToken() {
        return signinToken;
    }

    public void setSigninToken(String signinToken) {
        this.signinToken = signinToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCurrentPass() {
        return currentPass;
    }

    public void setCurrentPass(String currentPass) {
        this.currentPass = currentPass;
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

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getRequestedPageParameter() {
        return requestedPageParameter;
    }

    public void setRequestedPageParameter(String requestedPageParameter) {
        this.requestedPageParameter = requestedPageParameter;
    }

    /**
     * Get the logged in user name.
     *
     * @return the user name of the logged in user
     */
    public String getLoggedUsername() {
        // check the session
        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return httpServletRequest.getSession().getAttribute(SESSION_USERNAME).toString().toLowerCase();
    }

    /**
     * Inject login.
     * When an external application (e.g. Samply.EDC) generates a signinToken via its application key,
     * and the user of that application is then using a link to login to samply share without entering personal credentials (username/password)
     * the login will be injected, depending on the signinToken affiliation to a user.
     *
     * @param session  the current http session
     * @param context  the current request context
     * @param username the username of the injected user
     */
    private void injectLogin(HttpSession session, OmniPartialViewContext context, String username) {
        logger.info("Login injected from OSSE: " + username);
        session.setAttribute(SESSION_USERNAME, username);
        context.addArgument("loggedIn", true);
    }

    /**
     * Try to login the user with the credentials entered on the login page
     *
     * @return the outcome of the credentials check. Either back to the login page or to the configured success page (usually configuration.xhtml)
     */
    public String login() {
        OmniPartialViewContext context = OmniPartialViewContext.getCurrentInstance();

        boolean loggedIn = false;

        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        User userToCheck = UserUtil.fetchUserByName(getUser().getUsername().trim());

        if (userToCheck != null && BCrypt.checkpw(getUser().getPasswordHash(), userToCheck.getPasswordHash())) {
            logger.debug("Login successful");
            loggedIn = true;

            StoreConnector.login(userToCheck.getUsername(), getUser().getPasswordHash());

            setUser(userToCheck);

            // set the session
            HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            httpServletRequest.getSession().setAttribute(SESSION_USERNAME, userToCheck.getUsername());
            httpServletRequest.getSession().setAttribute(SESSION_USER, userToCheck);
        } else {
            logger.debug("Login failed");
            Messages.create("LoginController_loginFailedTitle")
                    .detail("LoginController_loginFailedMessage")
                    .error().flash().add();
            context.addArgument("loggedIn", false);
        }


        if (!loggedIn) {
            return "";
        }

        if (!SamplyShareUtils.isNullOrEmpty(requestedPageParameter)) {
            return requestedPageParameter.replaceFirst(ESCAPED_PARAMETER_SEPARATOR, "?").replaceAll(ESCAPED_PARAMETER_SEPARATOR, "&");
        }

        if (user.getAdminPrivilege() != null && user.getAdminPrivilege()) {
            return "admin/dashboard?faces-redirect=true";
        }

        return "user/dashboard?faces-redirect=true";
    }

    /**
     * Logout.
     *
     * @return the outcome logout to redirect to the login page
     */
    public String logout() {
        // invalidate the session
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getSession().invalidate();
        return "/login.xhtml?faces-redirect=true";
    }

    public void refreshSession() {
        FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }

    /**
     * Change the current users password
     */
    public void changePassword() {
        User userToCheck = UserUtil.fetchUserByName(getUser().getUsername());

        if (userToCheck != null && BCrypt.checkpw(currentPass, userToCheck.getPasswordHash())) {
            if (newPass.equals(newPassRepeat)) {
                String newPassHashed = BCrypt.hashpw(newPass, BCrypt.gensalt());
                userToCheck.setPasswordHash(newPassHashed);
                UserUtil.updateUser(userToCheck);
                StoreConnector.changeUserPassword(userToCheck.getUsername(), currentPass, newPass);
                logger.info("Password changed");
                Messages.create("common_passwordChangeTitle")
                        .detail("common_passwordChanged")
                        .add();
            } else {
                logger.info("Password mismatch");
                Messages.create("common_passwordChangeTitle")
                        .detail("common_passwordMismatch")
                        .error()
                        .add();
            }
        } else {
            logger.info("Old Password wrong");
            Messages.create("common_passwordChangeTitle")
                    .detail("common_wrongPassword")
                    .error()
                    .add();
        }
    }

    /**
     * Change the current users email address
     */
    public void changeEmail() {
        User user = UserUtil.fetchUserByName(getUser().getUsername());
        if (user != null) {
            user.setEmail(newEmail);
            UserUtil.updateUser(user);
        }
    }

}
