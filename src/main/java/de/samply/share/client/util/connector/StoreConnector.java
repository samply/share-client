package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class StoreConnector {

  private static final String SAVE_OR_UPDATE_USER = "saveOrUpdateUser";
  private static final String ACTIVATED = "1";
  private static final String NOT_ACTIVATED = "0";
  private static final String TEST_AUTH = "testAuth";
  private static final String DEFAULT_USERNAME = "local_admin";
  private static final String DEFAULT_PASSWORD = "local_admin";

  private static String storeUrl =
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL);
    public static String authorizedUsername = "";
    public static String authorizedPassword = "";

    private static final Logger logger = LogManager.getLogger(StoreConnector.class);

    /**
     * Gets only called after login of Connector UI was successful and handles login logic to Store.
     * <p>
     * First, tries provided user credentials as default.
     * If successful, do nothing.
     * <p>
     * Then, tries last successfully logged in user credentials.
     * If successful, send new user to Store and update internal.
     * <p>
     * At least, tries default user credentials, which are "local_admin" "local_admin".
     * If successful, send new user to Store.
     * And if new username is different from "local_admin", deactivate "local_admin" in Store.
     * Update internal.
     * <p>
     * If Store does no yet implement the necessary APIs, the updateCredentials next-to-last is the last successful authorization.
     * <p>
     * After first startup, credentials are "", so no connection to Store yet and the system does require a login to work.
     *
     * @param username used to create basic auth key or new user
     * @param password used to create basic auth key or new user
     */
    public static void login(String username, String password) {
        if (initAuthorizedCredentials(
                username, password)) {
            return; // Exit: authorized and internally updated, User already exists in Store
        }

        if (initAuthorizedCredentials(
                authorizedUsername, authorizedPassword
        )) { // first log in after restart, last User already exists in Store
            storeNewUser(username, password);
            initAuthorizedCredentials(username, password);
            return; // Exit: new User created, authorized and internally updated
        }

        // User and last User are not authorized, try fallback. This should only happen at first
        // connection to new Store
        if (initAuthorizedCredentials(DEFAULT_USERNAME, DEFAULT_PASSWORD)) {
            if (username.equalsIgnoreCase(DEFAULT_USERNAME)) {
                if (!password.equalsIgnoreCase(DEFAULT_PASSWORD)) {
                    changeUserPassword(DEFAULT_USERNAME, DEFAULT_PASSWORD, password);
                    return;
                } //if username and password default, first return was reached before
            }//if username different:
            storeNewUser(username, password);
            deactivateUser(DEFAULT_USERNAME);
            initAuthorizedCredentials(username, password); //if Store<4.2.7, authorizedUsername stays local_admin
            return;
        }

        // No Credentials authorized, abort.
        logger.error("Users: " +
                DEFAULT_USERNAME + ", " + username + " and " + authorizedUsername + " could not authorize at " + storeUrl + " | Store version 4.2.6 or up? Try to login with a previously used User, edit or delete Store db manually");
    }

    /**
     * Updates internal credentials used to authorize connection to Store.
     * Updates only if authorization to Store was successful.
     *
     * @param username used to create basic auth key
     * @param password used to create basic auth key
     * @return
     */
    private static boolean initAuthorizedCredentials(String username, String password) {
        boolean authorizedToStore =
                getResponse(new HttpGet(storeUrl + "/" + TEST_AUTH), username, password)
                        != Response.Status.UNAUTHORIZED.getStatusCode();
        if (authorizedToStore) { //Update internal credentials only if connection to Store established
            authorizedUsername = username;
            authorizedPassword = password;
            ApplicationBean.initLdmConnector(); // Update Auth Header in LdmClient
        }
        return authorizedToStore;
    }

    public static String getBase64Credentials(String username, String password) {
        try {
            return Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send request using protocol (http or https) of Store url.
     * @param httpUriRequest Request to send
     * @param username used to create basic auth key
     * @param password used to create basic auth key
     * @return
     */
    private static int getResponse(HttpUriRequest httpUriRequest, String username, String password) {
        CloseableHttpResponse response;
        try {
            HttpConnector httpConnector = ApplicationBean.createHttpConnector();
            httpConnector.addCustomHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + StoreConnector.getBase64Credentials(username, password));
            response = httpConnector.getHttpClient(storeUrl).execute(httpUriRequest);
            response.close();
            logger.debug(
                    "Response code for: "
                            + httpUriRequest.getURI()
                            + " with username = " + username + " was: "
                            + response.getStatusLine().getStatusCode());
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("IOException of HttpClient.execute() or CloseableHttpResponse.close");
            return -1;
        }
    }

    public static void deactivateUser(String username) {
        saveOrUpdateUser(username, "", NOT_ACTIVATED, "");
  }

    public static void storeNewUser(String username, String password) {
        saveOrUpdateUser(username, password, ACTIVATED, password);
  }

    public static void changeUserPassword(String username, String password, String newPassword) {
        saveOrUpdateUser(username, password, ACTIVATED, newPassword);
        initAuthorizedCredentials(username, newPassword);
  }

    private static void saveOrUpdateUser(
          String username, String password, String activated, String newPassword) {
    HttpPost httpPost = new HttpPost(storeUrl + "/" + SAVE_OR_UPDATE_USER);
    httpPost.setHeader("Username", username);
    httpPost.setHeader("Password", password);
    httpPost.setHeader("NewPassword", newPassword);
    httpPost.setHeader("Activated", activated);
        getResponse(httpPost, authorizedUsername, authorizedPassword);
  }
}
