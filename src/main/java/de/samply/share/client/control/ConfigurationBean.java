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

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.tables.pojos.Configuration;
import de.samply.share.client.model.db.tables.pojos.ConfigurationTimings;
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A SessionScoped backing bean that is used for configuration handling
 */
@ManagedBean(name = "configurationBean")
@SessionScoped
public class ConfigurationBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(ConfigurationBean.class);
    private static final DateFormat hourMinuteFormat = new SimpleDateFormat("HH:mm");
    private Map<EnumConfiguration, Object> configurationMap;
    private Map<EnumConfigurationTimings, Object> configurationTimingsMap;

    @PostConstruct
    public void init() {
        loadConfigurationMap();
        loadConfigurationTimingsMap();
    }

    /**
     * Load all entries defined in EnumConfiguration and stores them in a Map
     */
    private void loadConfigurationMap() {
        configurationMap = new HashMap<>();
        for (EnumConfiguration conf : EnumConfiguration.values()) {
            Configuration configurationElement = ConfigurationUtil.getConfigurationElement(conf);
            if (configurationElement != null) {
                configurationMap.put(conf, configurationElement.getSetting());
            }
        }
    }

    /**
     * Load all entries defined in EnumConfigurationTimings and stores them in a Map
     */
    private void loadConfigurationTimingsMap() {
        configurationTimingsMap = new HashMap<>();
        for (EnumConfigurationTimings conf : EnumConfigurationTimings.values()) {
            if (ApplicationUtils.isDktk() ||
                    (ApplicationUtils.isSamply() && !conf.name().equals("UPLOAD_RETRY_PATIENT_UPLOAD_ATTEMPTS") && !conf.name().equals("UPLOAD_RETRY_PATIENT_UPLOAD_INTERVAL"))) {
                ConfigurationTimings configurationTimingsElement = ConfigurationUtil.getConfigurationTimingsElement(conf);
                configurationTimingsMap.put(conf, configurationTimingsElement.getSetting());
            }
        }
    }

    /**
     * Write the settings from both maps back to the database
     */
    public void storeConfiguration() {
        storeConfigurationMap();
        storeConfigurationTimingsMap();
        Messages.create("ConfigurationController_configuration")
                .detail("ConfigurationController_configurationSaved")
                .add();
    }

    /**
     * Write the settings from the Configuration Map to the database
     */
    private void storeConfigurationMap() {
        for (Map.Entry<EnumConfiguration, Object> conf : configurationMap.entrySet()) {
            Configuration configurationElement = new Configuration();
            configurationElement.setName(conf.getKey().name());
            String value = null;
            if (conf.getValue().getClass() == String.class) {
                value = (String) conf.getValue();
            } else if (conf.getValue().getClass() == Boolean.class) {
                value = Boolean.toString((Boolean) conf.getValue());
            } else if (conf.getValue().getClass() == Date.class) {
                value = hourMinuteFormat.format((Date) conf.getValue());
            }
            configurationElement.setSetting(value);
            ConfigurationUtil.insertOrUpdateConfigurationElement(configurationElement);
        }
    }

    /**
     * Write the settings from the ConfigurationTimings Map to the database
     */
    private void storeConfigurationTimingsMap() throws ValidatorException {
        try {
            for (Map.Entry<EnumConfigurationTimings, Object> conf : configurationTimingsMap.entrySet()) {
                ConfigurationTimings configurationTimingsElement = new ConfigurationTimings();
                configurationTimingsElement.setName(conf.getKey().name());
                Integer value;
                if (conf.getValue().getClass() == String.class) {
                    value = Integer.parseInt((String) conf.getValue());
                } else if (conf.getValue().getClass() == Integer.class) {
                    value = (Integer) conf.getValue();
                } else {
                    logger.warn("Unknown class for Element " + conf.getKey());
                    value = null;
                }
                configurationTimingsElement.setSetting(value);
                ConfigurationUtil.insertOrUpdateConfigurationTimingsElement(configurationTimingsElement);
            }
        } catch (NumberFormatException e) {
            throw new ValidatorException(
                    Messages.create("Validation failed.")
                            .detail("configurationTimings_parseException")
                            .error().get()
            );
        }
    }

    public Map<EnumConfiguration, Object> getConfigurationMap() {
        return configurationMap;
    }

    public void setConfigurationMap(Map<EnumConfiguration, Object> configurationMap) {
        this.configurationMap = configurationMap;
    }

    public Map<EnumConfigurationTimings, Object> getConfigurationTimingsMap() {
        return configurationTimingsMap;
    }

    public void setConfigurationTimingsMap(Map<EnumConfigurationTimings, Object> configurationTimingsMap) {
        this.configurationTimingsMap = configurationTimingsMap;
    }

    public boolean getAsBoolean(EnumConfiguration configuration) {
        return ConfigurationUtil.getConfigurationElementValueAsBoolean(configuration);
    }

}
