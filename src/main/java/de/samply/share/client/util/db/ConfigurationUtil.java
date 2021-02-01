package de.samply.share.client.util.db;

import static de.samply.common.http.HttpConnector.PROXY_BYPASS_PRIVATE_NETWORKS;
import static de.samply.common.http.HttpConnector.PROXY_HTTPS_HOST;
import static de.samply.common.http.HttpConnector.PROXY_HTTPS_PORT;
import static de.samply.common.http.HttpConnector.PROXY_HTTP_HOST;
import static de.samply.common.http.HttpConnector.PROXY_HTTP_PORT;
import static de.samply.common.http.HttpConnector.USER_AGENT;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.tables.daos.ConfigurationDao;
import de.samply.share.client.model.db.tables.daos.ConfigurationTimingsDao;
import de.samply.share.client.model.db.tables.pojos.Configuration;
import de.samply.share.client.model.db.tables.pojos.ConfigurationTimings;
import de.samply.share.common.model.dto.UserAgent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper Class for CRUD operations with configuration objects.
 */
public class ConfigurationUtil {

  private static final Logger logger = LogManager.getLogger(ConfigurationUtil.class);

  private static final ConfigurationDao configurationDao;
  private static final ConfigurationTimingsDao configurationTimingsDao;

  static {
    configurationDao = new ConfigurationDao(ResourceManager.getConfiguration());
    configurationTimingsDao = new ConfigurationTimingsDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private ConfigurationUtil() {
  }

  /**
   * Get the configuration DAO.
   *
   * @return the configuration DAO
   */
  public static ConfigurationDao getConfigurationDao() {
    return configurationDao;
  }

  /**
   * Get the configuration timings DAO.
   *
   * @return the configuration timings DAO
   */
  public static ConfigurationTimingsDao getConfigurationTimingsDao() {
    return configurationTimingsDao;
  }

  /**
   * Get a configuration element.
   *
   * @param configurationElement the element to get
   * @return the configuration element
   */
  public static Configuration getConfigurationElement(EnumConfiguration configurationElement) {
    return configurationDao.fetchOneByName(configurationElement.name());
  }

  /**
   * Get a configuration timing element.
   *
   * @param configurationTimingsElement the timing element to get
   * @return the configuration timing element
   */
  public static ConfigurationTimings getConfigurationTimingsElement(
      EnumConfigurationTimings configurationTimingsElement) {
    return configurationTimingsDao.fetchOneByName(configurationTimingsElement.name());
  }

  /**
   * Insert a new configuration element into the database.
   *
   * @param configuration the new configuration element to insert
   */
  private static void insertConfigurationElement(Configuration configuration) {
    configurationDao.insert(configuration);
  }

  /**
   * Insert a new configuration timing element into the database.
   *
   * @param configurationTimings the new configuration timing element to insert
   */
  private static void insertConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
    configurationTimingsDao.insert(configurationTimings);
  }

  /**
   * Update a configuration element in the database.
   *
   * @param configuration the configuration element to update
   */
  private static void updateConfigurationElement(Configuration configuration) {
    configurationDao.update(configuration);
  }

  /**
   * Update a configuration timing element in the database.
   *
   * @param configurationTimings the configuration timing element to update
   */
  private static void updateConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
    configurationTimingsDao.update(configurationTimings);
  }

  /**
   * Delete a configuration element from the database.
   *
   * @param configuration the configuration element to delete
   */
  private static void deleteConfigurationElement(Configuration configuration) {
    if (configuration != null) {
      configurationDao.delete(configuration);
    }
  }

  /**
   * Delete a configuration timing element from the database.
   *
   * @param configurationTimings the configuration timing element to delete
   */
  private static void deleteConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
    configurationTimingsDao.delete(configurationTimings);
  }

  /**
   * Insert a configuration element into the database or update it, if it is already present.
   *
   * @param configuration the configuration element to insert or update
   */
  public static void insertOrUpdateConfigurationElement(Configuration configuration) {
    Configuration configurationElement = configurationDao.fetchOneByName(configuration.getName());
    if (configuration.getSetting() == null) {
      deleteConfigurationElement(configurationElement);
    } else if (configurationElement == null) {
      insertConfigurationElement(configuration);
    } else if (!configuration.getSetting().equals(configurationElement.getSetting())) {
      configurationElement.setSetting(configuration.getSetting());
      updateConfigurationElement(configurationElement);
    }
  }

  /**
   * Insert a configuration timing element into the database or update it, if it is already
   * present.
   *
   * @param configurationTimings the configuration timing element to insert or update
   */
  public static void insertOrUpdateConfigurationTimingsElement(
      ConfigurationTimings configurationTimings) {
    ConfigurationTimings configurationTimingsElement = configurationTimingsDao
        .fetchOneByName(configurationTimings.getName());
    if (configurationTimings.getSetting() == null) {
      deleteConfigurationTimingsElement(configurationTimingsElement);
    } else if (configurationTimingsElement == null) {
      insertConfigurationTimingsElement(configurationTimings);
    } else if (!configurationTimings.getSetting()
        .equals(configurationTimingsElement.getSetting())) {
      configurationTimingsElement.setSetting(configurationTimings.getSetting());
      updateConfigurationTimingsElement(configurationTimingsElement);
    }
  }

  /**
   * Get the string value of a configuration element.
   *
   * @param configurationElement the configuration element to get the value from
   * @return the string representation of the configuration value
   */
  public static String getConfigurationElementValue(EnumConfiguration configurationElement) {
    Configuration configuration = getConfigurationElement(configurationElement);
    return (configuration == null) ? "" : configuration.getSetting();
  }

  /**
   * Get the settings for a configuration element.
   *
   * @param configurationElement configurationElement
   * @return a list of the settings
   */
  public static List<String> getConfigurationElementValueList(
      EnumConfiguration configurationElement) {

    Configuration configuration = getConfigurationElement(configurationElement);

    List<String> results;

    if (configuration != null) {

      String setting = configuration.getSetting();
      String[] split = setting.split(";");
      results = Arrays.asList(split);

    } else {
      results = new ArrayList<>();
    }

    return results;

  }


  /**
   * Get the int value of a configuration timing element.
   *
   * @param configurationTimingsElement the configuration timing element to get the value from
   * @return the int representation of the configuration timing value or 0 if not set
   */
  public static int getConfigurationTimingsElementValue(
      EnumConfigurationTimings configurationTimingsElement) {
    ConfigurationTimings configurationTimings = getConfigurationTimingsElement(
        configurationTimingsElement);
    return (configurationTimings == null) ? 0 : configurationTimings.getSetting();
  }

  /**
   * Get the boolean value of a configuration element.
   *
   * @param configurationElement the configuration element to get the value from
   * @return the boolean value of the configuration value or false if any errors occur
   */
  public static boolean getConfigurationElementValueAsBoolean(
      EnumConfiguration configurationElement) {
    Configuration configuration = getConfigurationElement(configurationElement);
    try {
      return Boolean.parseBoolean(configuration.getSetting());
    } catch (NullPointerException npe) {
      return false;
    }
  }

  /**
   * Get the proxy configuration.
   *
   * @param configuration a samply.common.configuration element
   * @return a map containing the proxy settings as well as the user agent string
   */
  public static HashMap<String, String> getHttpConfigParams(
      de.samply.common.config.Configuration configuration) {
    HashMap<String, String> configParams = new HashMap<>();

    try {
      try {
        configParams.put(PROXY_HTTP_HOST, configuration.getProxy().getHttp().getUrl().getHost());
        configParams.put(PROXY_HTTP_PORT,
            Integer.toString(configuration.getProxy().getHttp().getUrl().getPort()));
      } catch (NullPointerException npe) {
        // Note: it is too often shown -> makes large logs
        //logger.debug("Could not get HTTP Proxy Settings...should be empty or null");
      }
      try {
        configParams.put(PROXY_HTTPS_HOST, configuration.getProxy().getHttps().getUrl().getHost());
        configParams.put(PROXY_HTTPS_PORT,
            Integer.toString(configuration.getProxy().getHttps().getUrl().getPort()));
      } catch (NullPointerException npe) {
        //logger.debug("Could not get HTTPS Proxy Settings...should be empty or null");
      }
      configParams.put(PROXY_BYPASS_PRIVATE_NETWORKS, Boolean.TRUE.toString());

      UserAgent userAgent = ApplicationBean.getDefaultUserAgent();
      configParams.put(USER_AGENT, userAgent.toString());

    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return configParams;
  }
}
