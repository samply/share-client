package de.samply.share.client.quality.report.localdatamanagement;/*
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

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class LocalDataManagementConnector {

    private int socketTimeout = 100000;
    private int connectTimeout = 100000;


    CloseableHttpResponse getResponse(String uri, HttpUriRequest request) throws IOException {
        return getHttpConnector().getHttpClient(uri).execute(request);
    }

    HttpPost createHttpPost(String uri, HttpEntity httpEntity){

        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");
        httpPost.setEntity(httpEntity);

        return httpPost;

    }

    HttpGet createHttpGet(String uri){

        HttpGet httpGet = new HttpGet(uri);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        httpGet.setConfig(requestConfig);

        return httpGet;

    }

    private HttpConnector getHttpConnector (){
        return ApplicationBean.createHttpConnector();
    }

    String getLocalDataManagementUrl() {
        return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL);
    }


    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
