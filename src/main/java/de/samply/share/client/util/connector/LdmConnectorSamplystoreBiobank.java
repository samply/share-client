package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.centraxx.LdmClientCentraxxException;
import de.samply.common.ldmclient.samplystoreBiobank.LdmClientSamplystoreBiobank;
import de.samply.common.ldmclient.samplystoreBiobank.LdmClientSamplystoreBiobankException;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.MdrUtils;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.common.*;
import de.samply.share.model.osse.ObjectFactory;
import de.samply.share.model.osse.Patient;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static de.samply.dktk.converter.PatientConverterUtil.convertDate;

public class LdmConnectorSamplystoreBiobank implements LdmConnector<BbmriResult, Patient> {

    /**
     * Implementation of the LdmConnector interface for samply store rest backends
     */

    private static final Logger logger = LogManager.getLogger(de.samply.share.client.util.connector.LdmConnectorSamplystoreBiobank.class);

    private transient HttpConnector httpConnector;
    private transient MdrClient mdrClient;
    private LdmClientSamplystoreBiobank ldmClient;
    private CloseableHttpClient httpClient;
    private String samplystoreBaseUrl;
    private HttpHost samplystoreHost;

    public LdmConnectorSamplystoreBiobank() {
        try {
            init();
            this.mdrClient = ApplicationBean.getMdrClient();
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public LdmConnectorSamplystoreBiobank(boolean useCaching) {
        try {
            init(useCaching);
            this.mdrClient = ApplicationBean.getMdrClient();
        } catch (LDMConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public LdmConnectorSamplystoreBiobank(boolean useCaching, int maxCacheSize) {
        try {
            init(useCaching, maxCacheSize);
            this.mdrClient = ApplicationBean.getMdrClient();
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
            this.ldmClient = new LdmClientSamplystoreBiobank(httpClient, samplystoreBaseUrl);
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
            this.ldmClient = new LdmClientSamplystoreBiobank(httpClient, samplystoreBaseUrl, useCaching);
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
            this.ldmClient = new LdmClientSamplystoreBiobank(httpClient, samplystoreBaseUrl, useCaching, maxCacheSize);
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
//    @Override
//    public String postQuery(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException {
//        try {
//            View view = new View();
//            view.setQuery(query);
//            // TODO: How to get viewfields for samply store to use?
//            ViewFields viewFields = new ViewFields();
//
//            // Add additional viewfields, as defined in the config
////            String additionalViewfields = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.INQUIRY_ADDITIONAL_MDRKEYS);
////            if (includeAdditionalViewfields && !SamplyShareUtils.isNullOrEmpty(additionalViewfields)) {
////                List<String> viewFieldList = Splitter.on(';').splitToList(additionalViewfields);
////                for (String viewField : viewFieldList) {
////                    viewFields.getMdrKey().add(viewField);
////                }
////            }
//
//            view.setViewFields(viewFields);
////            if (!SamplyShareUtils.isNullOrEmpty(removeKeysFromView)) {
////                view = QueryConverter.osseRemoveAttributesFromView(view, removeKeysFromView);
////            }
//            return ldmClient.postView(view);
//        } catch (LdmClientException e) {
//            throw new LDMConnectorException(e);
//        }
//    }
    @Override
    public String postQuery(de.samply.share.model.common.Query query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException {
        try {
            View view = new View();
            view.setQuery(query);
            // TODO: How to get viewfields for samply store to use?
            ViewFields viewFields = new ViewFields();

            // Add additional viewfields, as defined in the config
//            String additionalViewfields = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.INQUIRY_ADDITIONAL_MDRKEYS);
//            if (includeAdditionalViewfields && !SamplyShareUtils.isNullOrEmpty(additionalViewfields)) {
//                List<String> viewFieldList = Splitter.on(';').splitToList(additionalViewfields);
//                for (String viewField : viewFieldList) {
//                    viewFields.getMdrKey().add(viewField);
//                }
//            }

            view.setViewFields(viewFields);
//            if (!SamplyShareUtils.isNullOrEmpty(removeKeysFromView)) {
//                view = QueryConverter.osseRemoveAttributesFromView(view, removeKeysFromView);
//            }
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
    public BbmriResult getResults(String location) throws LDMConnectorException {
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
    public BbmriResult getResultsFromPage(String location, int page) throws LDMConnectorException {
        try {
            return ldmClient.getResultPage(location, page);
        } catch (LdmClientSamplystoreBiobankException e) {
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
        } catch (LdmClientSamplystoreBiobankException e) {
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
        } catch (LdmClientSamplystoreBiobankException e) {
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
        } catch (LdmClientSamplystoreBiobankException e) {
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

    @Override
    public boolean isResultDone(String location, de.samply.share.model.common.QueryResultStatistic qrs) throws LDMConnectorException {
        if (SamplyShareUtils.isNullOrEmpty(location)) {
            throw new LDMConnectorException("Location of query is empty");
        }
        if (qrs != null) {
            if (qrs.getTotalSize() == 0) {
                return true;
            }
            int lastPageIndex = qrs.getNumberOfPages() - 1;
            return ldmClient.isResultPageAvailable(location, lastPageIndex);
        } else {
            throw new LDMConnectorException("QueryResultStatistic is null.");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void writeQueryResultPageToDisk(BbmriResult queryResult, int index) throws IOException {
        try {
            File dir = (File) ProjectInfo.INSTANCE.getServletContext().getAttribute(TEMPDIR);
            File xmlFile = new File(dir + System.getProperty("file.separator") + queryResult.getQueryId() + "_" + index + "_transformed" + XML_SUFFIX);
            final JAXBContext context = JAXBContext.newInstance(BbmriResult.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(queryResult, xmlFile);
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
        int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(
                EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS);
        int secondsSleep = ConfigurationUtil.getConfigurationTimingsElementValue(
                EnumConfigurationTimings.JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS);
        int retryNr = 0;

        View view = createViewForMonitoring(dktkFlagged);
        String resultLocation = null;
        try {
            resultLocation = ldmClient.postView(view);
        } catch (LdmClientException e) {
            e.printStackTrace();
        }
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
        return 0;
    }

    @Override
    public ReferenceQueryCheckResult getReferenceQueryCheckResult(de.samply.share.model.common.Query referenceQuery) throws LDMConnectorException {
        return null;
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
     * {@inheritDoc}
     */
    @Override
    public int getPatientAge(Patient patient) {
        String birthdayValueString = null;

        for (de.samply.share.model.osse.Attribute attr : patient.getAttribute()) {
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

    @Override
    public de.samply.share.model.ccp.QueryResult getExportQueryResult(de.samply.share.model.ccp.QueryResult queryResult) throws InterruptedException, IOException, LdmClientCentraxxException, JAXBException {
        return null;
    }
}

