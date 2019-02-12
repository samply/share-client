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

package de.samply.share.client.util.db;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.tables.daos.ConfigurationDao;
import de.samply.share.client.model.db.tables.daos.ConfigurationTimingsDao;
import de.samply.share.client.model.db.tables.pojos.Configuration;
import de.samply.share.client.model.db.tables.pojos.ConfigurationTimings;
import de.samply.share.common.model.dto.UserAgent;
import de.samply.share.common.utils.ProjectInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static de.samply.common.http.HttpConnector.*;

/**
 * Helper Class for CRUD operations with configuration objects
 */
public class ConfigurationUtil {

    private static final Logger logger = LogManager.getLogger(ConfigurationUtil.class);

    private static ConfigurationDao configurationDao;
    private static ConfigurationTimingsDao configurationTimingsDao;

    static {
        configurationDao = new ConfigurationDao(ResourceManager.getConfiguration());
        configurationTimingsDao = new ConfigurationTimingsDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private ConfigurationUtil() {
    }

    /**
     * Get the configuration DAO
     *
     * @return the configuration DAO
     */
    public static ConfigurationDao getConfigurationDao() {
        return configurationDao;
    }

    /**
     * Get the configuration timings DAO
     *
     * @return the configuration timings DAO
     */
    public static ConfigurationTimingsDao getConfigurationTimingsDao() {
        return configurationTimingsDao;
    }

    /**
     * Get a configuration element
     *
     * @param configurationElement the element to get
     * @return the configuration element
     */
    public static Configuration getConfigurationElement(EnumConfiguration configurationElement) {
        return configurationDao.fetchOneByName(configurationElement.name());
    }

    /**
     * Get a configuration timing element
     *
     * @param configurationTimingsElement the timing element to get
     * @return the configuration timing element
     */
    public static ConfigurationTimings getConfigurationTimingsElement(EnumConfigurationTimings configurationTimingsElement) {
        return configurationTimingsDao.fetchOneByName(configurationTimingsElement.name());
    }

    /**
     * Insert a new configuration element into the database
     *
     * @param configuration the new configuration element to insert
     * @return the assigned database id of the newly inserted configuration element
     */
    public static void insertConfigurationElement(Configuration configuration) {
        configurationDao.insert(configuration);
    }

    /**
     * Insert a new configuration timing element into the database
     *
     * @param configurationTimings the new configuration timing element to insert
     * @return the assigned database id of the newly inserted configuration timing element
     */
    public static void insertConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
        configurationTimingsDao.insert(configurationTimings);
    }

    /**
     * Update a configuration element in the database
     *
     * @param configuration the configuration element to update
     */
    public static void updateConfigurationElement(Configuration configuration) {
        configurationDao.update(configuration);
    }

    /**
     * Update a configuration timing element in the database
     *
     * @param configurationTimings the configuration timing element to update
     */
    public static void updateConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
        configurationTimingsDao.update(configurationTimings);
    }

    /**
     * Delete a configuration element from the database
     *
     * @param configuration the configuration element to delete
     */
    public static void deleteConfigurationElement(Configuration configuration) {
        configurationDao.delete(configuration);
    }

    /**
     * Delete a configuration timing element from the database
     *
     * @param configurationTimings the configuration timing element to delete
     */
    public static void deleteConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
        configurationTimingsDao.delete(configurationTimings);
    }

    /**
     * Insert a configuration element into the database or update it, if it is already present
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
     * Insert a configuration timing element into the database or update it, if it is already present
     *
     * @param configurationTimings the configuration timing element to insert or update
     */
    public static void insertOrUpdateConfigurationTimingsElement(ConfigurationTimings configurationTimings) {
        ConfigurationTimings configurationTimingsElement = configurationTimingsDao.fetchOneByName(configurationTimings.getName());
        if (configurationTimings.getSetting() == null) {
            deleteConfigurationTimingsElement(configurationTimingsElement);
        } else if (configurationTimingsElement == null) {
            insertConfigurationTimingsElement(configurationTimings);
        } else if (!configurationTimings.getSetting().equals(configurationTimingsElement.getSetting())) {
            configurationTimingsElement.setSetting(configurationTimings.getSetting());
            updateConfigurationTimingsElement(configurationTimingsElement);
        }
    }

    /**
     * Get the string value of a configuration element
     *
     * @param configurationElement the configuration element to get the value from
     * @return the string representation of the configuration value
     */
    public static String getConfigurationElementValue(EnumConfiguration configurationElement) {
        Configuration configuration = getConfigurationElement(configurationElement);
        return (configuration == null) ? "" : configuration.getSetting();
    }

    /**
     * Get the int value of a configuration timing element
     *
     * @param configurationTimingsElement the configuration timing element to get the value from
     * @return the int representation of the configuration timing value or 0 if not set
     */
    public static int getConfigurationTimingsElementValue(EnumConfigurationTimings configurationTimingsElement) {
        ConfigurationTimings configurationTimings = getConfigurationTimingsElement(configurationTimingsElement);
        return (configurationTimings == null) ? 0 : configurationTimings.getSetting();
    }

    /**
     * Get the boolean value of a configuration element
     *
     * @param configurationElement the configuration element to get the value from
     * @return the boolean value of the configuration value or false if any errors occur
     */
    public static boolean getConfigurationElementValueAsBoolean(EnumConfiguration configurationElement) {
        Configuration configuration = getConfigurationElement(configurationElement);
        try {
            return Boolean.parseBoolean(configuration.getSetting());
        } catch (NullPointerException npe) {
            return false;
        }
    }

    /**
     * Get the proxy configuration
     *
     * @param configuration a samply.common.configuration element
     * @return a map containing the proxy settings as well as the user agent string
     */
    public static HashMap<String, String> getHttpConfigParams(de.samply.common.config.Configuration configuration) {
        HashMap<String, String> configParams = new HashMap<>();

        try {
            try {
                configParams.put(PROXY_HTTP_HOST, configuration.getProxy().getHTTP().getUrl().getHost());
                configParams.put(PROXY_HTTP_PORT, Integer.toString(configuration.getProxy().getHTTP().getUrl().getPort()));
            } catch (NullPointerException npe) {
                logger.debug("Could not get HTTP Proxy Settings...should be empty or null");
            }
            try {
                configParams.put(PROXY_HTTPS_HOST, configuration.getProxy().getHTTPS().getUrl().getHost());
                configParams.put(PROXY_HTTPS_PORT, Integer.toString(configuration.getProxy().getHTTPS().getUrl().getPort()));
            } catch (NullPointerException npe) {
                logger.debug("Could not get HTTPS Proxy Settings...should be empty or null");
            }
            configParams.put(PROXY_BYPASS_PRIVATE_NETWORKS, Boolean.TRUE.toString());

            UserAgent userAgent = new UserAgent(ProjectInfo.INSTANCE.getProjectName(), "Samply.Share", ProjectInfo.INSTANCE.getVersionString());
            configParams.put(USER_AGENT, userAgent.toString());

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return configParams;
    }
}
