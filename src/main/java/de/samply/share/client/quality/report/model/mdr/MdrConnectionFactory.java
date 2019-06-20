package de.samply.share.client.quality.report.model.mdr;/*
* Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
* Deutsches Krebsforschungszentrum in Heidelberg
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
* along with this program; if not, see http://www.gnu.org/licenses.
*
* Additional permission under GNU GPL version 3 section 7:
*
* If you modify this Program, or any covered work, by linking or combining it
* with Jersey (https://jersey.java.net) (or a modified version of that
* library), containing parts covered by the terms of the General Public
* License, version 2.0, the licensors of this Program grant you additional
* permission to convey the resulting work.
*/

import de.dth.mdr.validator.MdrConnection;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.properties.PropertyUtils;
import de.samply.share.client.util.db.ConfigurationUtil;

import java.util.Arrays;
import java.util.List;

public class MdrConnectionFactory {

    private enum PROPERTIES {

        NAMESPACE (EnumConfiguration.QUALITY_REPORT_MDR_NAMESPACE),
        AUTH_USER_ID (EnumConfiguration.QUALITY_REPORT_MDR_AUTH_USER_ID),
        AUTH_KEY_ID (EnumConfiguration.QUALITY_REPORT_MDR_AUTH_KEY_ID),
        AUTH_URL (EnumConfiguration.QUALITY_REPORT_MDR_AUTH_URL),
        AUTH_PRIVATE_KEY_BASE_64 (EnumConfiguration.QUALITY_REPORT_MDR_AUTH_PRIVATE_KEY_BASE_64);

        private EnumConfiguration enumConfiguration;

        PROPERTIES(EnumConfiguration enumConfiguration) {
            this.enumConfiguration = enumConfiguration;
        }

        public EnumConfiguration getEnumConfiguration(){
            return enumConfiguration;
        }

    }

    private static String authUserId;
    private static String authKeyId;
    private static String authUrl;
    private static String privateKeyBase64;
    private static List<String> namespaces;


    public MdrConnectionFactory() {

        authUserId = loadProperty(PROPERTIES.AUTH_USER_ID);
        authKeyId = loadProperty(PROPERTIES.AUTH_KEY_ID);
        authUrl = loadProperty(PROPERTIES.AUTH_URL);
        privateKeyBase64 = loadProperty(PROPERTIES.AUTH_PRIVATE_KEY_BASE_64);
        namespaces = getNamespaces();

    }

    private List<String> getNamespaces(){
        return Arrays.asList(PropertyUtils.getListOfProperties(PROPERTIES.NAMESPACE.getEnumConfiguration()));
    }

    private String loadProperty (PROPERTIES property){
        return ConfigurationUtil.getConfigurationElementValue(property.getEnumConfiguration());
    }

    public MdrConnection getMdrConnection() {

        String mdrUrl = getMdrUrl();
        HttpConnector httpConnector = ApplicationBean.createHttpConnector();

        return getMdrConnection (mdrUrl, authUserId, authKeyId, authUrl, privateKeyBase64, namespaces, httpConnector);

    }

    private MdrConnection getMdrConnection(String mdrUrl, String authUserId, String keyId, String authURL, String privateKeyBase64, List<String> namespaces, HttpConnector httpConnector43) {
        return new MdrConnection(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, namespaces, true, httpConnector43);
    }


    private String getMdrUrl() {
        return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL);
    }


}
