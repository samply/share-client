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


import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Error;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.View;
import de.samply.common.ldmclient.centraxx.model.QueryResultStatistic;
import de.samply.share.utils.QueryConverter;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;


public class LocalDataManagementRequesterImpl extends  LocalDataManagementConnector implements LocalDataManagementRequester{




    @Override
    public LocalDataManagementResponse<String> postViewAndGetLocationUrlStatisticsOnly(View view) throws LocalDataManagementRequesterException {
        return postViewAndGetLocationUrl(view, true);
    }

    @Override
    public LocalDataManagementResponse<String> postViewAndGetLocationUrl(View view) throws LocalDataManagementRequesterException {
        return postViewAndGetLocationUrl(view, false);
    }

    private LocalDataManagementResponse<String> postViewAndGetLocationUrl(View view, boolean statisticsOnly) throws LocalDataManagementRequesterException {

        try {

            return postViewAndGetLocationUrlWithoutExceptions(view, statisticsOnly);

        } catch (Exception e) {
            throw new LocalDataManagementRequesterException(e);
        }
    }

    private LocalDataManagementResponse<String> postViewAndGetLocationUrlWithoutExceptions (View view, boolean statisticsOnly) throws SQLException, JAXBException, UnsupportedEncodingException, LocalDataManagementRequesterException {

        String localDataManagementUrl = SamplyShareUtils.addTrailingSlash(getLocalDataManagementUrl());
        localDataManagementUrl += LocalDataManagementUrlSuffixAndParameters.BASE;

        MyUri myUri = new MyUri(localDataManagementUrl, LocalDataManagementUrlSuffixAndParameters.REQUESTS_URL_SUFFIX);
        if (statisticsOnly){
            myUri.addParameter(LocalDataManagementUrlSuffixAndParameters.STATISTICS_ONLY_PARAMETER, "true");
        }
        String uri = myUri.toString();

        de.samply.share.model.ccp.View ccpView = QueryConverter.convertCommonViewToCcpView(view);
        String sCcpView = QueryConverter.viewToXml(ccpView);
        HttpEntity httpEntity = new StringEntity(sCcpView);


        HttpPost httpPost = createHttpPost(uri, httpEntity);


        return getLocationHeader(localDataManagementUrl, httpPost);

    }


    private LocalDataManagementResponse<String> getLocationHeader(String localDataManagementUrl, HttpPost httpPost)  throws LocalDataManagementRequesterException {

        try (CloseableHttpResponse response = getResponse(localDataManagementUrl, httpPost)) {


            LocalDataManagementResponse<String> ldmResponse = new LocalDataManagementResponse<>();

            int statusCode = response.getStatusLine().getStatusCode();
            ldmResponse.setStatusCode(statusCode);

            Header location = response.getFirstHeader("Location");
            if (location != null){
                ldmResponse.setResponse(location.getValue());
            }

            return ldmResponse;


        } catch (Exception e) {
            throw new LocalDataManagementRequesterException(e);
        }

    }

    @Override
    public LocalDataManagementResponse<QueryResultStatistic> getQueryResultStatistic(String locationUrl) throws LocalDataManagementRequesterException {

        MyUri myUri = new MyUri(locationUrl, LocalDataManagementUrlSuffixAndParameters.STATISTICS_URL_SUFFIX);

        return getQueryResultStatistic(myUri);
    }

    private LocalDataManagementResponse<QueryResultStatistic> getQueryResultStatistic (MyUri myUri) throws LocalDataManagementRequesterException {
        return getLocalDataManagementResponse(myUri, QueryResultStatistic.class);
    }

    @Override
    public LocalDataManagementResponse<QueryResult> getQueryResult(String locationUrl, int page) throws LocalDataManagementRequesterException {

        MyUri myUri = new MyUri(locationUrl, LocalDataManagementUrlSuffixAndParameters.RESULTS_URL_SUFFIX);
        myUri.addParameter(LocalDataManagementUrlSuffixAndParameters.PAGE_PARAMETER, String.valueOf(page));

        return getQueryResult(myUri);
    }

    @Override
    public LocalDataManagementResponse<String> getSqlMappingVersion() throws LocalDataManagementRequesterException {

        try {
            return getSqlMappingVersion_WithoutManagementException();
        } catch (Exception e){
            throw new LocalDataManagementRequesterException(e);
        }

    }

    public LocalDataManagementResponse<String> getSqlMappingVersion_WithoutManagementException() throws LocalDataManagementRequesterException {

        LdmConnectorCentraxx ldmConnector = (LdmConnectorCentraxx) ApplicationBean.getLdmConnector();
        String version = ldmConnector.getMappingVersion();

        LocalDataManagementResponse<String> localDataManagementResponse = new LocalDataManagementResponse<>();
        localDataManagementResponse.setStatusCode(HttpStatus.SC_OK);
        localDataManagementResponse.setResponse(version);

        return localDataManagementResponse;

    }


//    @Override
//    public LocalDataManagementResponse<String> getSqlMappingVersion() throws LocalDataManagementRequesterException {
//
//        String localDataManagementUrl = SamplyShareUtils.addTrailingSlash(getLocalDataManagementUrl());
//        localDataManagementUrl += LocalDataManagementUrlSuffixAndParameters.BASE;
//        String suffix = LocalDataManagementUrlSuffixAndParameters.SQL_MAPPING_VERSION + "/" + getCentraXXMappingMdrKey();
//
//        String uri = new MyUri(localDataManagementUrl, suffix).toString();
//        HttpGet httpGet = createHttpGet(uri);
//
//        httpGet.addHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLDM());
//
//        //TODO
//        return getLocalDataManagementResponse(uri, httpGet,String.class);
//
//    }
//
//    private String getCentraXXMappingMdrKey (){
//
//        String centraxxMappingMdrKey = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CENTRAXX_MAPPING_VERSION);
//        MdrIdDatatype mappingMdrItem = new MdrIdDatatype(centraxxMappingMdrKey);
//
//        return mappingMdrItem.getLatestCentraxx();
//
//    }

    private LocalDataManagementResponse<QueryResult> getQueryResult(MyUri myUri) throws LocalDataManagementRequesterException {
        return getLocalDataManagementResponse(myUri, QueryResult.class);
    }

    private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse (MyUri myUri, Class<T> clazz) throws LocalDataManagementRequesterException {

        String uri = myUri.toString();
        HttpGet httpGet = createHttpGet(uri);

        return getLocalDataManagementResponse(uri, httpGet, clazz);
    }

    private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse (String url, HttpGet httpGet, Class<T> clazz) throws LocalDataManagementRequesterException {

        try (CloseableHttpResponse response = getResponse(url, httpGet)){

            return getLocalDataManagementResponse(response, clazz);

        } catch (Exception e) {
            throw new LocalDataManagementRequesterException(e);
        }

    }

    private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse (CloseableHttpResponse response, Class<T> clazz) throws IOException, JAXBException {

        int statusCode = response.getStatusLine().getStatusCode();

        HttpEntity ccpEntity = response.getEntity();
        String entityOutput = EntityUtils.toString(ccpEntity, Consts.UTF_8);

        LocalDataManagementResponse<T> ldmResponse = new LocalDataManagementResponse<>();
        ldmResponse.setStatusCode(statusCode);

        if (statusCode == HttpStatus.SC_OK) {

            T resp = createObject(entityOutput, clazz);
            ldmResponse.setResponse(resp);

        } else if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {

            Error error = createError(entityOutput);
            ldmResponse.setError(error);
        }

        EntityUtils.consume(ccpEntity);

        return ldmResponse;

    }

    private Error createError (String ccpEntity) throws JAXBException {

        de.samply.share.model.ccp.Error error = createObject(ccpEntity, de.samply.share.model.ccp.Error.class);
        return QueryConverter.convertCcpErrorToCommonError(error);

    }

    private <T> T createObject(String entity, Class<T> clazz) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object object = jaxbUnmarshaller.unmarshal(new StringReader(entity));

        return (object instanceof JAXBElement) ? (T) ((JAXBElement) object).getValue() : (T) object;

    }

}
