package de.samply.share.client.util.db;

import static de.samply.share.client.model.db.enums.AuthSchemeType.AS_BASIC;
import static de.samply.share.client.model.db.enums.TargetType.TT_HTTPS_PROXY;
import static de.samply.share.client.model.db.enums.TargetType.TT_HTTP_PROXY;

import de.samply.common.config.Configuration;
import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.daos.CredentialsDao;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.model.db.tables.records.CredentialsRecord;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with credentials objects.
 */
public class CredentialsUtil {

  private static final Logger logger = LogManager.getLogger(CredentialsUtil.class);

  private static final CredentialsDao credentialsDao;

  static {
    credentialsDao = new CredentialsDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private CredentialsUtil() {
  }

  /**
   * Get the credentials DAO.
   *
   * @return the credentials DAO
   */
  public static CredentialsDao getCredentialsDao() {
    return credentialsDao;
  }

  /**
   * Insert a new set of credentials into the database.
   *
   * @param credentials the new set of credentials to insert
   * @return the assigned database id of the newly inserted set of credentials
   */
  public static int insertCredentials(Credentials credentials) {
    DSLContext dslContext = ResourceManager.getDslContext();
    CredentialsRecord credentialsRecord = dslContext.newRecord(Tables.CREDENTIALS, credentials);
    credentialsRecord.store();
    credentialsRecord.refresh();
    return credentialsRecord.getId();
  }

  /**
   * Update a set of credentials in the database.
   *
   * @param credentials the set of credentials to update
   */
  public static void updateCredentials(Credentials credentials) {
    credentialsDao.update(credentials);
  }

  /**
   * Delete a set of credentials from the database.
   *
   * @param credentials the set of credentials to delete
   */
  public static void deleteCredentials(Credentials credentials) {
    credentialsDao.delete(credentials);
  }

  /**
   * Get a list of all credentials.
   *
   * @return list of all credentials
   */
  public static List<Credentials> fetchCredentials() {
    return credentialsDao.findAll();
  }

  /**
   * Get the credentials to be used with a given broker.
   *
   * @param broker the broker for which the credentials are requested
   * @return the set of credentials to authenticate with the given broker
   */
  public static Credentials getCredentialsForBroker(Broker broker) {
    return credentialsDao.fetchOneById(broker.getCredentialsId());
  }

  /**
   * Delete the credentials associated with a given broker.
   *
   * @param broker the broker for which the credentials are deleted
   */
  public static void deleteCredentialsForBroker(Broker broker) {
    Credentials credentials = getCredentialsForBroker(broker);
    if (credentials != null) {
      deleteCredentials(credentials);
    }
  }

  /**
   * Get the credentials to be used for a certain target type.
   *
   * @param targetType the target for which the credentials are wanted
   * @return the credentials to authenticate with the given target
   */
  public static List<Credentials> getCredentialsByTarget(TargetType targetType) {
    return credentialsDao.fetchByTarget(targetType);
  }

  /**
   * Get the credentials to authenticate with the HTTP Proxy.
   *
   * @return the credentials to authenticate with the HTTP Proxy
   */
  public static Credentials getCredentialsForHttpProxy() {
    List<Credentials> credentials = getCredentialsByTarget(TT_HTTP_PROXY);
    if (SamplyShareUtils.isNullOrEmpty(credentials)) {
      return null;
    } else if (credentials.size() > 1) {
      logger.warn("More than 1 set of credentials stored for HTTP Proxy. Returning the first!");
    }
    return credentials.get(0);
  }

  /**
   * Get the credentials to authenticate with the HTTPS Proxy.
   *
   * @return the credentials to authenticate with the HTTPS Proxy
   */
  public static Credentials getCredentialsForHttpsProxy() {
    List<Credentials> credentials = getCredentialsByTarget(TT_HTTPS_PROXY);
    if (SamplyShareUtils.isNullOrEmpty(credentials)) {
      return null;
    } else if (credentials.size() > 1) {
      logger.warn("More than 1 set of credentials stored for HTTPS Proxy. Returning the first!");
    }
    return credentials.get(0);
  }

  /**
   * Update the proxy credentials in the database according to those read from a samply common
   * config file.
   *
   * @param configuration samply common configuration settings
   */
  public static void updateProxyCredentials(Configuration configuration) {
    boolean changed = false;
    String newProxyUsername;
    String newProxyPass;
    Credentials httpProxyCredentials = getCredentialsForHttpProxy();

    if (configuration.getProxy().getHttp() != null
        && configuration.getProxy().getHttp().getUrl() != null
        && configuration.getProxy().getHttp().getUrl().toString().length() > 0) {

      newProxyUsername = configuration.getProxy().getHttp().getUsername();
      newProxyPass = configuration.getProxy().getHttp().getPassword();

      if (httpProxyCredentials == null) {
        httpProxyCredentials = new Credentials();
        httpProxyCredentials.setUsername(newProxyUsername);
        httpProxyCredentials.setPasscode(newProxyPass);
        httpProxyCredentials.setTarget(TT_HTTP_PROXY);
        httpProxyCredentials.setAuthScheme(AS_BASIC);
        insertCredentials(httpProxyCredentials);
      } else {
        if (!httpProxyCredentials.getUsername().equalsIgnoreCase(newProxyUsername)) {
          changed = true;
          httpProxyCredentials.setUsername(newProxyUsername);
        }
        if (!httpProxyCredentials.getPasscode().equalsIgnoreCase(newProxyPass)) {
          changed = true;
          httpProxyCredentials.setPasscode(newProxyPass);
        }
        if (changed) {
          updateCredentials(httpProxyCredentials);
        }
      }
    } else if (httpProxyCredentials != null) {
      logger
          .info("Delete HTTP Proxy Credentials from DB since no HTTP proxy is configured any more");
      deleteCredentials(httpProxyCredentials);
    }
    changed = false;
    Credentials httpsProxyCredentials = getCredentialsForHttpsProxy();
    if (configuration.getProxy().getHttps() != null
        && configuration.getProxy().getHttps().getUrl() != null
        && configuration.getProxy().getHttps().getUrl().toString().length() > 0) {
      newProxyUsername = configuration.getProxy().getHttps().getUsername();
      newProxyPass = configuration.getProxy().getHttps().getPassword();
      if (httpsProxyCredentials == null) {
        httpsProxyCredentials = new Credentials();
        httpsProxyCredentials.setUsername(newProxyUsername);
        httpsProxyCredentials.setPasscode(newProxyPass);
        httpsProxyCredentials.setTarget(TT_HTTPS_PROXY);
        httpsProxyCredentials.setAuthScheme(AS_BASIC);
        insertCredentials(httpsProxyCredentials);
      } else {
        if (!httpsProxyCredentials.getUsername().equalsIgnoreCase(newProxyUsername)) {
          changed = true;
          httpsProxyCredentials.setUsername(newProxyUsername);
        }
        if (!httpsProxyCredentials.getPasscode().equalsIgnoreCase(newProxyPass)) {
          changed = true;
          httpsProxyCredentials.setPasscode(newProxyPass);
        }
        if (changed) {
          updateCredentials(httpsProxyCredentials);
        }
      }
    } else if (httpsProxyCredentials != null) {
      logger.info(
          "Delete HTTPS Proxy Credentials from DB since no HTTPS proxy is configured any more");
      deleteCredentials(httpsProxyCredentials);
    }
  }

  /**
   * Get the basic auth credentials for the local data management.
   *
   * @return basic auth
   */
  public static String getBasicAuthStringForLdm() {
    for (Credentials credentials : fetchCredentials()) {
      if (credentials.getTarget() == TargetType.TT_LDM) {
        return "Basic " + DatatypeConverter.printBase64Binary(
            (credentials.getUsername() + ":" + credentials.getPasscode()).getBytes(
                StandardCharsets.UTF_8));
      }
    }
    return "";
  }
}
