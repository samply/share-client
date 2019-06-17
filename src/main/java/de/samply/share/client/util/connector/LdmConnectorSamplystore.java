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
import de.samply.common.http.HttpConnector;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.samplystore.LdmClientSamplystore;
import de.samply.common.ldmclient.samplystore.LdmClientSamplystoreException;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.MdrUtils;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.*;
import de.samply.share.model.osse.ObjectFactory;
import de.samply.share.model.osse.Patient;
import de.samply.share.model.osse.QueryResult;
import de.samply.share.utils.QueryConverter;
import org.apache.http.HttpEntity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the LdmConnector interface for samply store rest backends
 */
// TODO: Make sure that the class is not used and delete it (or the whole project).
public class LdmConnectorSamplystore implements LdmConnector<QueryResult, Patient> {

    private static final Logger logger = LogManager.getLogger(LdmConnectorSamplystore.class);

    private transient HttpConnector httpConnector;
    private LdmClientSamplystore ldmClient;
    private CloseableHttpClient httpClient;
    private String samplystoreBaseUrl;
    private HttpHost samplystoreHost;

    public LdmConnectorSamplystore() {
        try {
            init();
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public LdmConnectorSamplystore(boolean useCaching) {
        try {
            init(useCaching);
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public LdmConnectorSamplystore(boolean useCaching, int maxCacheSize) {
        try {
            init(useCaching, maxCacheSize);
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws LDMConnectorException {
        try {
            this.samplystoreBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.samplystoreHost = SamplyShareUtils.getAsHttpHost(samplystoreBaseUrl);
            httpClient = httpConnector.getHttpClient(samplystoreHost);
            this.ldmClient = new LdmClientSamplystore(httpClient, samplystoreBaseUrl);
        } catch (MalformedURLException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    private void init(boolean useCaching) throws LDMConnectorException {
        try {
            this.samplystoreBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.samplystoreHost = SamplyShareUtils.getAsHttpHost(samplystoreBaseUrl);
            httpClient = httpConnector.getHttpClient(samplystoreHost);
            this.ldmClient = new LdmClientSamplystore(httpClient, samplystoreBaseUrl, useCaching);
        } catch (MalformedURLException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    private void init(boolean useCaching, int maxCacheSize) throws LDMConnectorException {
        try {
            this.samplystoreBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));
            httpConnector = ApplicationBean.getHttpConnector();
            this.samplystoreHost = SamplyShareUtils.getAsHttpHost(samplystoreBaseUrl);
            httpClient = httpConnector.getHttpClient(samplystoreHost);
            this.ldmClient = new LdmClientSamplystore(httpClient, samplystoreBaseUrl, useCaching, maxCacheSize);
        } catch (MalformedURLException | LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param completeMdsViewFields not yet supported
     * @param statisticsOnly        not yet supported
     */
    @Override
    public String postQuery(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException {
        try {
            View view = new View();
            view.setQuery(query);
            // TODO: How to get viewfields for samply store to use?
            ViewFields viewFields = new ViewFields();

            // Add additional viewfields, as defined in the config
            String additionalViewfields = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.INQUIRY_ADDITIONAL_MDRKEYS);
            if (includeAdditionalViewfields && !SamplyShareUtils.isNullOrEmpty(additionalViewfields)) {
                List<String> viewFieldList = Splitter.on(';').splitToList(additionalViewfields);
                for (String viewField : viewFieldList) {
                    viewFields.getMdrKey().add(viewField);
                }
            }

            view.setViewFields(viewFields);
            if (!SamplyShareUtils.isNullOrEmpty(removeKeysFromView)) {
                view = QueryConverter.removeAttributesFromView(view, removeKeysFromView);
            }
            return ldmClient.postView(view);
        } catch (LdmClientException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postViewString(String view, boolean statisticsOnly) throws LDMConnectorException {
        try {
            return ldmClient.postViewString(view);
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
        } catch (LdmClientSamplystoreException e) {
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
     * {@inheritDoc}
     */
    @Override
    public Object getStatsOrError(String location) throws LDMConnectorException {
        try {
            return ldmClient.getStatsOrError(location);
        } catch (LdmClientSamplystoreException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResultStatistic getQueryResultStatistic(String location) throws LDMConnectorException {
        try {
            return ldmClient.getQueryResultStatistic(location);
        } catch (LdmClientSamplystoreException e) {
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
        } catch (LdmClientSamplystoreException e) {
            throw new LDMConnectorException(e);
        }
    }

    /**
     * {@inheritDoc}
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
        HttpGet httpGet = new HttpGet(samplystoreBaseUrl + "rest/info/");
        result.getMessages().add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));

        try (CloseableHttpResponse response = httpClient.execute(samplystoreHost, httpGet)) {
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
            String resultLocation = ldmClient.postView(view);
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
        } catch (LdmClientSamplystoreException e) {
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
            String resultLocation = ldmClient.postView(referenceView);

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

                        ArrayList<String> unknownKeys = new ArrayList<>(error.getMdrKey());
                        referenceView = QueryConverter.removeAttributesFromView(referenceView, unknownKeys);
                        stopwatch.start();
                        resultLocation = ldmClient.postView(referenceView);
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
        } catch (LdmClientSamplystoreException e) {
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
}
