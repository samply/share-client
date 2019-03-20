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

package de.samply.share.client.util.connector;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.samply.common.http.HttpConnector;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.centraxx.LdmClientCentraxx;
import de.samply.common.ldmclient.centraxx.LdmClientCentraxxException;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.util.MdrUtils;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.client.util.connector.centraxx.CxxMappingParser;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.*;
import de.samply.share.utils.QueryConverter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static de.samply.dktk.converter.PatientConverterUtil.convertDate;

/**
 * Implementation of the LdmConnector interface for centraxx backends
 */
// TODO clean up
public class LdmConnectorCentraxx implements LdmConnector<QueryResult, Patient> {

    /** The Constant logger. */
    private static final Logger logger = LogManager.getLogger(LdmConnectorCentraxx.class);

    private transient HttpConnector httpConnector;
    private transient MdrClient mdrClient;
    private LdmClientCentraxx ldmClient;
    private CloseableHttpClient httpClient;
    private String centraxxBaseUrl;
    private HttpHost centraxxHost;
    private CxxMappingParser cxxMappingParser = new CxxMappingParser();
    private MdrMappedElements mdrMappedElements;

    public LdmConnectorCentraxx() {
        try {
            init();
            this.mdrClient = ApplicationBean.getMdrClient();
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public LdmConnectorCentraxx(boolean useCaching) {
        try {
            init(useCaching);
            this.mdrClient = ApplicationBean.getMdrClient();
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public LdmConnectorCentraxx(boolean useCaching, int maxCacheSize) {
        try {
            init(useCaching, maxCacheSize);
            this.mdrClient = ApplicationBean.getMdrClient();
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws LDMConnectorException {
        try {
            this.centraxxBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.centraxxHost = SamplyShareUtils.getAsHttpHost(centraxxBaseUrl);
            httpClient = httpConnector.getHttpClient(centraxxHost);
            this.ldmClient = new LdmClientCentraxx(httpClient, centraxxBaseUrl);
        } catch (MalformedURLException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    private void init(boolean useCaching) throws LDMConnectorException {
        try {
            this.centraxxBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.centraxxHost = SamplyShareUtils.getAsHttpHost(centraxxBaseUrl);
            httpClient = httpConnector.getHttpClient(centraxxHost);
            this.ldmClient = new LdmClientCentraxx(httpClient, centraxxBaseUrl, useCaching);
        } catch (MalformedURLException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    private void init(boolean useCaching, int maxCacheSize) throws LDMConnectorException {
        try {
            this.centraxxBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.centraxxHost = SamplyShareUtils.getAsHttpHost(centraxxBaseUrl);
            httpClient = httpConnector.getHttpClient(centraxxHost);
            this.ldmClient = new LdmClientCentraxx(httpClient, centraxxBaseUrl, useCaching, maxCacheSize);
        } catch (MalformedURLException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postQuery(Query query,
                            List<String> removeKeysFromView,
                            boolean completeMdsViewFields,
                            boolean statisticsOnly,
                            boolean includeAdditionalViewfields) throws LDMConnectorException {
        try {
            View view = new View();
            // Substitute BETWEEN and IN operators
            Query fixedQuery = QueryConverter.substituteOperators(query);
            view.setQuery(fixedQuery);
            ViewFields viewFields = MdrUtils.getViewFields(completeMdsViewFields);

            // Add additional viewfields, as defined in the config
            String additionalViewfields = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.INQUIRY_ADDITIONAL_MDRKEYS);
            if (includeAdditionalViewfields && !SamplyShareUtils.isNullOrEmpty(additionalViewfields)) {
                List<String> viewFieldList = Splitter.on(';').splitToList(additionalViewfields);
                for (String viewField : viewFieldList) {
                        viewFields.getMdrKey().add(viewField);
                }
            }

            viewFields = filterNotExistentMdrIdsInViewFields(viewFields);

            view.setViewFields(viewFields);
            if (!SamplyShareUtils.isNullOrEmpty(removeKeysFromView)) {
                view = QueryConverter.removeAttributesFromView(view, removeKeysFromView);
            }
            return ldmClient.postView(view, statisticsOnly);
        } catch (MdrConnectionException | ExecutionException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    private ViewFields filterNotExistentMdrIdsInViewFields (ViewFields viewFields){

        if (viewFields != null){

            List<String> mdrKeyList = viewFields.getMdrKey();
            if (mdrKeyList != null){

                MdrMappedElements mdrMappedElements = getMdrMappedElements();

                List<String> filteredMdrKeyList = new ArrayList<>();
                filteredMdrKeyList.addAll(mdrKeyList);

                for (String mdrId : filteredMdrKeyList){
                    if (!mdrMappedElements.isMapped(mdrId)){
                        mdrKeyList.remove(mdrId);
                    }
                }


            }


        }

        return viewFields;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postViewString(String view, boolean statisticsOnly) throws LDMConnectorException {
        try {
            return ldmClient.postViewString(view, statisticsOnly);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postCriteriaString(String criteria, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException {
        try {
            Query query = QueryConverter.xmlToQuery(criteria);
            return postQuery(query, null, completeMdsViewFields, statisticsOnly, includeAdditionalViewfields);
        } catch (JAXBException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult getResults(String location) throws LDMConnectorException {
        try {
            return ldmClient.getResult(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult getResultsFromPage(String location, int page) throws LDMConnectorException {
        try {
            return ldmClient.getResultPage(location, page);
        } catch (LdmClientCentraxxException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isQueryPresent(String location) throws LDMConnectorException {
        try {
            return ldmClient.isQueryPresent(location);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * Gets the stats for a query on the given location.
     *
     * @param location
     *            the location
     * @return the stats or the error
     * @throws LDMConnectorException
     */
    public Object getStatsOrError(String location) throws LDMConnectorException {
        try {
            return ldmClient.getStatsOrError(location);
        } catch (LdmClientCentraxxException e) {
            throw new LDMConnectorException(e);
        }
    }

    public QueryResultStatistic getQueryResultStatistic(String location) throws LDMConnectorException {
        try {
            return ldmClient.getQueryResultStatistic(location);
        } catch (LdmClientCentraxxException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getResultCount(String location) throws LDMConnectorException {
        try {
            return ldmClient.getResultCount(location);
        } catch (LdmClientCentraxxException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * Gets the page count for a query on a given location.
     *
     * @param location the location
     * @return the result count
     * @throws LDMConnectorException
     */
    @Override
    public Integer getPageCount(String location) throws LDMConnectorException {
        try {
            return ldmClient.getQueryResultStatistic(location).getNumberOfPages();
        } catch (Exception e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstResultPageAvailable(String location) throws LDMConnectorException {
        if (SamplyShareUtils.isNullOrEmpty(location)) {
            throw new LDMConnectorException("Location of query is empty");
        }

        // If the stats are written and the results are empty, return true
        Integer resultCount = getResultCount(location);
        if (resultCount != null && resultCount == 0) {
            return true;
        }

        return ldmClient.isResultPageAvailable(location, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResultDone(String location, QueryResultStatistic queryResultStatistic) throws LDMConnectorException {
        if (SamplyShareUtils.isNullOrEmpty(location)) {
            throw new LDMConnectorException("Location of query is empty");
        }

        if (queryResultStatistic != null) {
            if (queryResultStatistic.getTotalSize() == 0) {
                return true;
            }
            int lastPageIndex = queryResultStatistic.getNumberOfPages() - 1;
            return ldmClient.isResultPageAvailable(location, lastPageIndex);
        } else {
            throw new LDMConnectorException("QueryResultStatistic is null.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeQueryResultPageToDisk(QueryResult queryResult, int index) throws IOException {
        try {
            File dir = (File) ProjectInfo.INSTANCE.getServletContext().getAttribute(TEMPDIR);
            File xmlFile = new File(dir + System.getProperty("file.separator") + queryResult.getId() + "_" + index + "_transformed" + XML_SUFFIX);
            final JAXBContext context = JAXBContext.newInstance(QueryResult.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ObjectFactory objectFactory = new ObjectFactory();
            marshaller.marshal(objectFactory.createQueryResult(queryResult), xmlFile);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentInfo() throws LDMConnectorException {
        try {
            return ldmClient.getUserAgentInfo();
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckResult checkConnection() {
        CheckResult result = new CheckResult();
        result.setExecutionDate(new Date());
        HttpGet httpGet = new HttpGet(centraxxBaseUrl + "rest/info/");
        result.getMessages().add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));

        try (CloseableHttpResponse response = httpClient.execute(centraxxHost, httpGet)) {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

            result.getMessages().add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 400) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
                result.getMessages().add(new Message(EntityUtils.toString(entity), "fa-long-arrow-left"));
            }
        } catch (IOException ioe) {
            result.getMessages().add(new Message("IOException: " + ioe.getMessage(), "fa-bolt"));
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPatientCount(boolean dktkFlagged) throws LDMConnectorException, InterruptedException {
        try {
            int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
            int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
            int retryNr = 0;

            View view = createViewForMonitoring(dktkFlagged);
            String resultLocation = ldmClient.postView(view, true);
            do {
                try {
                    Integer resultCount = getResultCount(resultLocation);
                    if (resultCount != null) {
                        return resultCount;
                    }
                } catch (LDMConnectorException e) {
                    // Catch the exception since it might just mean the result is not ready yet
                }
                TimeUnit.SECONDS.sleep(secondsSleep);
            } while (++retryNr < maxAttempts);
        } catch (LdmClientCentraxxException e) {
            throw new LDMConnectorException(e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceQueryCheckResult getReferenceQueryCheckResult(Query referenceQuery) throws LDMConnectorException {
        ReferenceQueryCheckResult result = new ReferenceQueryCheckResult();
        try {
            View referenceView = createReferenceViewForMonitoring(referenceQuery);
            Stopwatch stopwatch = Stopwatch.createStarted();
            String resultLocation = ldmClient.postView(referenceView, false);

            int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
            int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                    EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
            int retryNr = 0;
            do {
                try {
                    Object statsOrError = ldmClient.getStatsOrError(resultLocation);

                    if (statsOrError.getClass().equals(Error.class)) {
                        stopwatch.reset();
                        Error error = (Error) statsOrError;

                        switch (error.getErrorCode()) {
                            case LdmClientCentraxx.ERROR_CODE_DATE_PARSING_ERROR:
                            case LdmClientCentraxx.ERROR_CODE_UNIMPLEMENTED:
                            case LdmClientCentraxx.ERROR_CODE_UNCLASSIFIED_WITH_STACKTRACE:
                                logger.warn("Could not execute reference query correctly. Error: " + error.getErrorCode() + ": " + error.getDescription());
                                return result;
                            case LdmClientCentraxx.ERROR_CODE_UNKNOWN_MDRKEYS:
                            default:
                                ArrayList<String> unknownKeys = new ArrayList<>();
                                unknownKeys.addAll(error.getMdrKey());
                                referenceView = QueryConverter.removeAttributesFromView(referenceView, unknownKeys);
                                stopwatch.start();
                                resultLocation = ldmClient.postView(referenceView, false);
                                break;
                        }
                    } else if (statsOrError.getClass().equals(QueryResultStatistic.class)) {
                        QueryResultStatistic qrs = (QueryResultStatistic) statsOrError;
                        result.setCount(qrs.getTotalSize());
                        if (isResultDone(resultLocation, qrs)) {
                            stopwatch.stop();
                            result.setExecutionTimeMilis(stopwatch.elapsed(TimeUnit.MILLISECONDS));
                            return result;
                        }
                    }

                    retryNr += 1;
                    TimeUnit.SECONDS.sleep(secondsSleep);
                } catch (InterruptedException e) {
                    return result;
                }
            } while (retryNr < maxAttempts);
        } catch (LdmClientCentraxxException e) {
            throw new LDMConnectorException(e);
        }
        return result;
    }

    /**
     * Create a basic view that is used to get the amount of patients in centraxx
     *
     * @param dktkFlagged when true, only count those with dktk consent. when false, count ALL (not just those without consent)
     * @return the constructed view object that can be posted to centraxx
     */
    private View createViewForMonitoring(boolean dktkFlagged) throws LDMConnectorException {
        MdrIdDatatype mdrKeyDktkConsent =
                new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CONSENT_DKTK));
        View view = new View();
        Query query = new Query();
        ObjectFactory objectFactory = new ObjectFactory();
        Where where = new Where();
        And and = new And();

        if (dktkFlagged) {
            Attribute attr_dktkFlag = new Attribute();
            attr_dktkFlag.setMdrKey(mdrKeyDktkConsent.getLatestCentraxx());
            attr_dktkFlag.setValue(objectFactory.createValue("true"));

            Eq equals = new Eq();
            equals.setAttribute(attr_dktkFlag);
            and.getAndOrEqOrLike().add(equals);
            where.getAndOrEqOrLike().add(and);
        }

        query.setWhere(where);
        view.setQuery(query);
        try {
            view.setViewFields(MdrUtils.getViewFields(false));
        } catch (MdrConnectionException | ExecutionException e) {
            throw new LDMConnectorException(e);
        }
        return view;
    }

    /**
     * Create the reference view that is used to get the amount of patients for that query
     *
     * @param referenceQuery the reference query, as received from the broker
     * @return the constructed view object that can be posted to centraxx
     */
    private View createReferenceViewForMonitoring(Query referenceQuery) throws LDMConnectorException {
        View view = new View();
        view.setQuery(referenceQuery);
        try {
            view.setViewFields(MdrUtils.getViewFields(true));
        } catch (MdrConnectionException | ExecutionException e) {
            throw new LDMConnectorException(e);
        }
        return view;
    }

    /**
     * Retrieve the version of the used mapping script from centraxx
     *
     * @return whatever is written as revision information in this reply
     */
    public String getMappingVersion() {
        String centraxxMappingMdrKey = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CENTRAXX_MAPPING_VERSION);
        MdrIdDatatype mappingMdrItem = new MdrIdDatatype(centraxxMappingMdrKey);
        HttpGet httpGet = new HttpGet(centraxxBaseUrl + "rest/teiler/mapping/" + mappingMdrItem.getLatestCentraxx());
        httpGet.addHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLDM());

        try (CloseableHttpResponse response = httpClient.execute(centraxxHost, httpGet)) {
            HttpEntity entity = response.getEntity();
            String mappingInfo = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);

            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(mappingInfo).getAsJsonArray();

            if (jsonArray != null && jsonArray.size() > 0) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                return jsonObject.get("urnRevision").getAsString();
            } else {
                return "undefined";
            }
        } catch (Exception e) {
            logger.warn("Exception caught while trying to get centraxx mapping version", e);
            return "undefined";
        }
    }

    /**
     * Retrieve the date of the used mapping script from centraxx
     *
     * @return whatever is written as revision information in this reply
     */
    public String getMappingDate() {
        String centraxxMappingDateMdrKey = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CENTRAXX_MAPPING_DATE);
        MdrIdDatatype mappingMdrDateItem = new MdrIdDatatype(centraxxMappingDateMdrKey);
        HttpGet httpGet = new HttpGet(centraxxBaseUrl + "rest/teiler/mapping/" + mappingMdrDateItem.getLatestCentraxx());
        httpGet.addHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLDM());

        try (CloseableHttpResponse response = httpClient.execute(centraxxHost, httpGet)) {
            HttpEntity entity = response.getEntity();
            String mappingInfo = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);

            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(mappingInfo).getAsJsonArray();

            if (jsonArray != null && jsonArray.size() > 0) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                return jsonObject.get("urnRevision").getAsString();
            } else {
                return "undefined";
            }
        } catch (Exception e) {
            logger.warn("Exception caught while trying to get centraxx mapping date", e);
            return "undefined";
        }
    }

    public List<CxxMappingElement> getMapping (){

        HttpGet httpGet = new HttpGet(centraxxBaseUrl + "rest/teiler/mapping");
        httpGet.addHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLDM());

        return getMapping(httpGet);

    }

    private MdrMappedElements getMdrMappedElements(){

        if (mdrMappedElements == null){
            mdrMappedElements = new MdrMappedElements(this);
        }

        return mdrMappedElements;

    }

    private List<CxxMappingElement> getMapping (HttpGet httpGet){

        try (CloseableHttpResponse response = httpClient.execute(centraxxHost, httpGet)) {

            HttpEntity entity = response.getEntity();
            String  cxxMapping = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);

            return cxxMappingParser.parse(cxxMapping);

        } catch (Exception e) {

            logger.warn("Exception caught while trying to get centraxx mapping", e);
            return new ArrayList<>();

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPatientAge(Patient patient) {
        String birthdayValueString = null;

        for (de.samply.share.model.ccp.Attribute attr : patient.getAttribute()) {
            MdrIdDatatype attrMdrId = new MdrIdDatatype(attr.getMdrKey());
            if (BIRTHDAY_MDR_ID.equalsIgnoreVersion(attrMdrId)) {
                birthdayValueString = attr.getValue().getValue();
                break;
            }
        }

        if (birthdayValueString == null) {
            return -1;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date birthDate = convertDate(birthdayValueString, dateFormat);
            Date now = new Date();
            return Utils.getDiffYears(birthDate, now);
        } catch (Exception e) {
            logger.error("error trying to get date: " + e);
            return -1;
        }
    }

    public QueryResult getExportQueryResult(QueryResult queryResult) throws InterruptedException, IOException, LdmClientCentraxxException, JAXBException {
        QueryResult queryResult1=ldmClient.exportQuery(queryResult);
        return  queryResult1;
    }
}
