package de.samply.share.client.control;

import com.google.common.base.Splitter;
import de.dth.mdr.validator.MDRValidator;
import de.dth.mdr.validator.MdrConnection;
import de.dth.mdr.validator.exception.MdrException;
import de.samply.common.config.Configuration;
import de.samply.common.config.ObjectFactory;
import de.samply.common.http.HttpConnector;
import de.samply.common.http.HttpConnectorException;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.config.util.JAXBUtil;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.QuartzJob;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.check.ConnectCheckResult;
import de.samply.share.client.model.common.Bridgehead;
import de.samply.share.client.model.common.Operator;
import de.samply.share.client.model.common.Urls;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.ReplyRuleType;
import de.samply.share.client.model.db.tables.pojos.JobSchedule;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizerImpl;
import de.samply.share.client.quality.report.chainlinks.statistics.manager.ChainStatisticsManager;
import de.samply.share.client.util.PatientValidator;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.IdManagerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.LdmConnectorSamplystoreBBMRI;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.common.model.dto.UserAgent;
import de.samply.share.common.utils.Constants;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.web.mdrFaces.MdrContext;
import org.apache.http.client.CredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.api.FlywayException;
import org.quartz.*;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Backing Bean that is valid during the whole runtime of the application.
 *
 * Holds methods that are needed system-wide
 */
@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(ApplicationBean.class);

    private static final String COMMON_CONFIG_FILENAME_SUFFIX = "_common_config.xml";
    private static final String COMMON_URLS_FILENAME_SUFFIX = "_common_urls.xml";
    private static final String COMMON_OPERATOR_FILENAME_SUFFIX = "_common_operator.xml";
    private static final String COMMON_INFOS_FILENAME_SUFFIX = "_bridgehead_info.xml";
    private static final List<String> NAMESPACES = new ArrayList<>(Arrays.asList("dktk","adt"));

    private static Urls urls;
    private static Operator operator;
    private static Bridgehead infos;

    private static boolean qrTaskRunning;

    private static HttpConnector httpConnector;
    private static Configuration configuration;
    private static MdrClient mdrClient;

    private static Scheduler scheduler;
    private static UserAgent userAgent;

    private static PatientValidator patientValidator;

    private static ChainStatisticsManager chainStatisticsManager = new ChainStatisticsManager();

    private static ChainFinalizer chainFinalizer = new ChainFinalizerImpl();

    private static MdrConnection mdrConnection;
    private static MDRValidator dthValidator;
    private static LdmConnector ldmConnector;

    private static final ConnectCheckResult shareAvailability = new ConnectCheckResult(true, "Samply.Share.Client", ProjectInfo.INSTANCE.getVersionString());
    private ConnectCheckResult ldmAvailability = new ConnectCheckResult();
    private ConnectCheckResult idmAvailability = new ConnectCheckResult();

    @PostConstruct
    public void init() {
        // On startup, check if there are changes to be done in the database
        try {
            Migration.doUpgrade();
        } catch (FlywayException e) {
            logger.fatal("Could not initialize or migrate database", e);
            throw new RuntimeException(e);
        }
        // Load common-config.xml
        loadCommonConfig();

        if (ProjectInfo.INSTANCE.getProjectName().equalsIgnoreCase("dktk")) {
            loadUrls();
            loadOperator();
            loadBridgeheadInfo();
            updateCommonUrls();
        }

        // Initialize HTTP Connector
        try {
            reInitHttpConnector();
        } catch (HttpConnectorException e) {
            throw new RuntimeException("Could not spawn http connector.", e);
        }

        resetMdrContext();
        patientValidator = new PatientValidator(MdrContext.getMdrContext().getMdrClient());

        // Initialize Quartz scheduler
        try {
            initScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException("Could not initialize quartz scheduler.", e);
        }

        initDthValidator();
        ApplicationBean.initLdmConnector();

        EventLogUtil.insertEventLogEntry(EventMessageType.E_SYSTEM_STARTUP);
    }

    /**
     * Initialize the Quartz Scheduler
     *
     * The configuration is done via web.xml and quartz.properties
     */
    private void initScheduler() throws SchedulerException {
        ServletContext servletContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();

        //Get QuartzInitializerListener
        StdSchedulerFactory stdSchedulerFactory = (StdSchedulerFactory) servletContext
                .getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);

        scheduler = stdSchedulerFactory.getScheduler();
        while (!scheduler.isStarted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                logger.warn("Caught Interrupted exception while trying to wait for scheduler start.", e);
            }
        }
        scheduleJobsFromDatabase();
    }

    private void initDthValidator() {
        try {
            mdrConnection = new MdrConnection(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL),
                    null,
                    null,
                    null,
                    null,
                    NAMESPACES,
                    true,
                    null);
            dthValidator = new MDRValidator(mdrConnection, true);
        } catch (MdrConnectionException | ExecutionException | MdrException | MdrInvalidResponseException e) {
            logger.error("Error initializing DTH Validator", e);
        }
    }

    // TODO: other connector implementations
    private static void initLdmConnector() {
        if (ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.LDM_CACHING_ENABLED)) {
            try {
                int maxCacheSize = Integer.parseInt(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_CACHING_MAX_SIZE));
                ApplicationBean.ldmConnector = new LdmConnectorCentraxx(true, maxCacheSize);
            } catch (NumberFormatException e) {
                ApplicationBean.ldmConnector = new LdmConnectorCentraxx(true);
            }
        } else {
            ApplicationBean.ldmConnector = new LdmConnectorCentraxx(false);
        }
    }

    /**
     * Fill the CredentialsProvider and reinitialize the HttpConnector
     */
    private static void reInitHttpConnector() throws HttpConnectorException {
        CredentialsProvider credentialsProvider = Utils.prepareCredentialsProvider();
        httpConnector = new HttpConnector(ConfigurationUtil.getHttpConfigParams(configuration), credentialsProvider);
        httpConnector.addCustomHeader(Constants.HEADER_XML_NAMESPACE, Constants.VALUE_XML_NAMESPACE_COMMON);
    }

    /**
     * Reinitialize the MdrClient
     *
     * Create a new MdrClient and clean the cache
     */
    private void resetMdrContext() {
        String mdrUrl;

//        if (ProjectInfo.INSTANCE.getProjectName().equals("osse")) {
//            logger.debug("Getting osse mdr url");
//            mdrUrl = OsseEdcContext.getOsseEdcConfiguration().getMdrUrl();
//        } else {
          mdrUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL);
//        }
        mdrClient = new MdrClient(mdrUrl, httpConnector.getJerseyClient(mdrUrl));
        mdrClient.cleanCache();
        MdrContext.getMdrContext().init(mdrClient);
        logger.debug("Reinitialized MDR Client with url " + mdrUrl + " - base uri is " + mdrClient.getBaseURI());
    }

    /**
     * Load the common config xml file from disk and apply the proxy settings
     */
    private static void loadCommonConfig() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            configuration = JAXBUtil
                    .findUnmarshall(ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_CONFIG_FILENAME_SUFFIX,
                            jaxbContext, Configuration.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase());
            CredentialsUtil.updateProxyCredentials(configuration);
            updateProxiesInDb();
        } catch (FileNotFoundException e) {
            logger.error("No config file found by using samply.common.config for project " + ProjectInfo.INSTANCE.getProjectName());
        } catch (UnmarshalException ue) {
            throw new RuntimeException("Unable to unmarshal config file");
        } catch (SAXException | JAXBException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the common urls xml file from disk
     */
    private static void loadUrls() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(de.samply.share.client.model.common.ObjectFactory.class);
            urls = JAXBUtil
                    .findUnmarshall(ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_URLS_FILENAME_SUFFIX,
                            jaxbContext, Urls.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase());
        }   catch (FileNotFoundException e) {
            logger.error("No common urls file found by using samply.common.config for project " + ProjectInfo.INSTANCE.getProjectName());
        } catch (UnmarshalException ue) {
            throw new RuntimeException("Unable to unmarshal common_urls file", ue);
        } catch (SAXException | JAXBException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the common operator xml file from disk
     */
    private static void loadOperator() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(de.samply.share.client.model.common.ObjectFactory.class);
            operator = JAXBUtil
                    .findUnmarshall(ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_OPERATOR_FILENAME_SUFFIX,
                            jaxbContext, Operator.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase());
        }   catch (FileNotFoundException e) {
            logger.error("No common operator file found by using samply.common.config for project " + ProjectInfo.INSTANCE.getProjectName());
        } catch (UnmarshalException ue) {
            throw new RuntimeException("Unable to unmarshal common_operator file");
        } catch (SAXException | JAXBException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the bridgehead info xml file from disk
     */
    private static void loadBridgeheadInfo() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(de.samply.share.client.model.common.ObjectFactory.class);
            infos = JAXBUtil
                    .findUnmarshall(ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_INFOS_FILENAME_SUFFIX,
                            jaxbContext, Bridgehead.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase());
        }   catch (FileNotFoundException e) {
            logger.error("No common bridgehead info file found by using samply.common.config for project " + ProjectInfo.INSTANCE.getProjectName());
        } catch (UnmarshalException ue) {
            throw new RuntimeException("Unable to unmarshal bridgehead_info file");
        } catch (SAXException | JAXBException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the proxy information in the db with the settings read from the config file
     */
    private static void updateProxiesInDb() {
        try {
            de.samply.share.client.model.db.tables.pojos.Configuration httpProxyConfigElement = new de.samply.share.client.model.db.tables.pojos.Configuration();
            httpProxyConfigElement.setName(EnumConfiguration.HTTP_PROXY.name());
            httpProxyConfigElement.setSetting(configuration.getProxy().getHTTP().getUrl().toString());
            ConfigurationUtil.insertOrUpdateConfigurationElement(httpProxyConfigElement);
            
            de.samply.share.client.model.db.tables.pojos.Configuration httpsProxyConfigElement = new de.samply.share.client.model.db.tables.pojos.Configuration();
            httpsProxyConfigElement.setName(EnumConfiguration.HTTPS_PROXY.name());
            httpsProxyConfigElement.setSetting(configuration.getProxy().getHTTPS().getUrl().toString());
            ConfigurationUtil.insertOrUpdateConfigurationElement(httpsProxyConfigElement);
        } catch (NullPointerException npe) {
            logger.debug("Caught nullpointer exception while trying to update proxies. This will most likely just mean that there is no proxy set in the config file. It is safe to ignore this.");
        }
    }

    /**
     * Update the URLs to the local components in the db with the settings read from the corresponding xml file
     */
    private static void updateCommonUrls() {
        if (urls != null) {
            de.samply.share.client.model.db.tables.pojos.Configuration idmanagerConfigElement = new de.samply.share.client.model.db.tables.pojos.Configuration();
            idmanagerConfigElement.setName(EnumConfiguration.ID_MANAGER_URL.name());
            idmanagerConfigElement.setSetting(urls.getIdmanagerUrl());
            ConfigurationUtil.insertOrUpdateConfigurationElement(idmanagerConfigElement);

            de.samply.share.client.model.db.tables.pojos.Configuration ldmConfigElement = new de.samply.share.client.model.db.tables.pojos.Configuration();
            ldmConfigElement.setName(EnumConfiguration.LDM_URL.name());
            ldmConfigElement.setSetting(urls.getCentraxxUrl());
            ConfigurationUtil.insertOrUpdateConfigurationElement(ldmConfigElement);

            de.samply.share.client.model.db.tables.pojos.Configuration shareConfigElement = new de.samply.share.client.model.db.tables.pojos.Configuration();
            shareConfigElement.setName(EnumConfiguration.SHARE_URL.name());
            shareConfigElement.setSetting(urls.getShareUrl());
            ConfigurationUtil.insertOrUpdateConfigurationElement(shareConfigElement);
        }
    }

    /**
     * Unschedule all jobs in a given group
     *
     * @param groupName which job group shall have its jobs cancelled?
     */
    private static void cancelAllJobsInGroup(String groupName) {
        logger.info("Cancelling Jobs in group " + groupName);
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupName))) {
                logger.info("Remove triggers for Job " + jobKey.toString());
                List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggersOfJob) {
                    scheduler.unscheduleJob(trigger.getKey());
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cancel all jobs that are linked with an upload
     */
    public static void cancelAllJobsForUpload() {
        logger.info("Cancelling upload related jobs");
        try {
            for (JobExecutionContext jobExecutionContext : scheduler.getCurrentlyExecutingJobs()) {
                boolean isUpload = jobExecutionContext.getMergedJobDataMap().getBoolean(CheckInquiryStatusJobParams.IS_UPLOAD);
                if (isUpload) {
                    scheduler.unscheduleJob(jobExecutionContext.getTrigger().getKey());
                }
            }
            // Also cancel the upload job itself
            cancelAllJobsInGroup("CentralSearchGroup");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the list of scheduled jobs from the database and arrange starting them
     */
    public static void scheduleJobsFromDatabase() {
        List<JobSchedule> jobSchedules = JobScheduleUtil.getJobSchedules();
        for (JobSchedule jobSchedule : jobSchedules) {
            QuartzJob quartzJob = new QuartzJob(jobSchedule.getJobKey(), null, null, null, jobSchedule.getCronExpression(), jobSchedule.getPaused(), "");
            rescheduleJobFromDatabase(quartzJob);
        }
    }

    /**
     * Add the trigger with the cron expression that is defined in the database to a job
     *
     * @param job the job that will have a trigger attached
     */
    @SuppressWarnings("unchecked")
    private static void rescheduleJobFromDatabase(QuartzJob job) {
        try {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

            // If there's not exactly one trigger, remove all and add the new trigger anyway
            if (triggers.size() != 1) {
                for (Trigger trigger : triggers) {
                    scheduler.unscheduleJob(trigger.getKey());
                }
            } else {
                CronTrigger cronTrigger = ((CronTrigger) triggers.get(0));
                if (cronTrigger.getCronExpression().equalsIgnoreCase(job.getCronExpression())) {
                    logger.debug("Trigger unchanged. Skipping.");
                    return;
                } else {
                    scheduler.unscheduleJob(cronTrigger.getKey());
                }
            }

            if (!CronExpression.isValidExpression(job.getCronExpression())) {
                throw new ValidatorException(new FacesMessage("Invalid Cron Expression: " + job.getCronExpression()));
            }

            CronTrigger newTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(job.getJobName() + ":trigger", job.getJobGroup())
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(job.getCronExpression())
                    )
                    .forJob(jobKey)
                    .build();

            // You can't add a trigger in a paused state. So schedule it and immediately pause if necessary
            scheduler.scheduleJob(newTrigger);
            if (job.isPaused()) {
                scheduler.pauseJob(jobKey);
            }

        } catch (SchedulerException e) {
            throw new ValidatorException(new FacesMessage(e.getLocalizedMessage()));
        }
    }

    public Urls getUrls() {
        return urls;
    }

    public Operator getOperator() {
        return operator;
    }

    public Bridgehead getInfos() {
        return infos;
    }

    public static HttpConnector getHttpConnector() {
        return httpConnector;
    }

    public static MdrClient getMdrClient() {
        return mdrClient;
    }

    public static Configuration getConfiguration() { return configuration; }

    public static void setConfiguration(Configuration configuration) {
        ApplicationBean.configuration = configuration;
    }

    public static UserAgent getUserAgent() {
        return userAgent;
    }

    public static void setUserAgent(UserAgent userAgent) {
        ApplicationBean.userAgent = userAgent;
        httpConnector.setUserAgent(userAgent.toString());
    }

    public static Scheduler getScheduler() { return scheduler; }

    public static void resetCredentialsProvider() {
        httpConnector.setCp(Utils.prepareCredentialsProvider());
    }

    public static String getDisplayName() {
        if (ProjectInfo.INSTANCE.getProjectName().equalsIgnoreCase("osse")) {
            return "OSSE.Share";
        } else if (ProjectInfo.INSTANCE.getProjectName().equalsIgnoreCase("dktk")) {
            return "DKTK.Teiler";
        } else if (ProjectInfo.INSTANCE.getProjectName().equalsIgnoreCase("gbn")) {
            return "DKTK.Teiler (GBN)";
        } else {
            return "Samply.Share";
        }
    }

    /**
     * Check the versions and availability of local datamanagement and id manager
     */
    public void updateLocalComponentInfo() {
        try {
            String ldmUserAgentInfo = getLdmConnector().getUserAgentInfo();
            List<String> list = Splitter.on('/').splitToList(ldmUserAgentInfo);
            if (list.size() == 2) {
                ldmAvailability.setName(list.get(0));
                ldmAvailability.setVersion(list.get(1));
            } else if (list.size() == 1) {
                ldmAvailability.setName(list.get(0));
                ldmAvailability.setVersion("");
            }
            ldmAvailability.setReachable(true);
        } catch (LDMConnectorException e) {
            ldmAvailability.setReachable(false);
        }

        try {
            IdManagerConnector idManagerConnector = new IdManagerConnector();
            String IdManagerUserAgentInfo = idManagerConnector.getUserAgentInfo();
            List<String> list = Splitter.on('/').splitToList(IdManagerUserAgentInfo);
            if (list.size() == 2) {
                idmAvailability.setName(list.get(0));
                idmAvailability.setVersion(list.get(1));
            } else if (list.size() == 1) {
                idmAvailability.setName(list.get(0));
                idmAvailability.setVersion("");
            }
            idmAvailability.setReachable(true);
        } catch (IdManagerConnectorException e) {
            idmAvailability.setReachable(false);
        }
    }

    public static LdmConnector getLdmConnector() {
        if (ApplicationBean.ldmConnector == null) {
            ApplicationBean.initLdmConnector();
        }
        return ApplicationBean.ldmConnector;
    }

    public static PatientValidator getPatientValidator() {
        return patientValidator;
    }

    public static ChainStatisticsManager getChainStatisticsManager(){
        return chainStatisticsManager;
    }

    public static ChainFinalizer getChainFinalizer() {
        return chainFinalizer;
    }

    public boolean isQrTaskRunning() {
        return qrTaskRunning;
    }

    public static void setQrTaskRunning(boolean qrTaskRunning) {
        ApplicationBean.qrTaskRunning = qrTaskRunning;
    }

    public ConnectCheckResult getShareAvailability() {
        return shareAvailability;
    }

    public ConnectCheckResult getLdmAvailability() {
        return ldmAvailability;
    }

    public ConnectCheckResult getIdmAvailability() {
        return idmAvailability;
    }

    public static MDRValidator getDthValidator() {
        return dthValidator;
    }

    /**
     * Get the array of defined reply rule types
     *
     * This should not be necessary, since it is possible to reference the Enum Class from the xhtml page directly.
     * However, in this case, the translations were not working - so this workaround is chosen
     * @return an array of all implemented reply rules
     * TODO: change this, when "reply with data" is defined
     */
    public ReplyRuleType[] getReplyRuleTypes() {
        ReplyRuleType[] replyRuleTypes = new ReplyRuleType[2];
        replyRuleTypes[0] = ReplyRuleType.RR_NO_AUTOMATIC_ACTION;
        replyRuleTypes[1] = ReplyRuleType.RR_TOTAL_COUNT;
        return replyRuleTypes;
    }
}
