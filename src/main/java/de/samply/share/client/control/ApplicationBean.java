package de.samply.share.client.control;

import static org.omnifaces.util.Faces.getServletContext;

import com.google.common.base.Splitter;
import de.dth.mdr.validator.MdrConnection;
import de.dth.mdr.validator.MdrValidator;
import de.dth.mdr.validator.exception.MdrException;
import de.samply.common.config.Configuration;
import de.samply.common.config.ObjectFactory;
import de.samply.common.http.HttpConnector;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.config.util.JaxbUtil;
import de.samply.project.directory.client.DktkProjectDirectory;
import de.samply.project.directory.client.DktkProjectDirectoryParameters;
import de.samply.project.directory.client.ProjectDirectory;
import de.samply.share.client.feature.ClientConfiguration;
import de.samply.share.client.feature.ClientFeature;
import de.samply.share.client.job.params.CheckInquiryStatusJobParams;
import de.samply.share.client.job.params.QuartzJob;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.check.ConnectCheckResult;
import de.samply.share.client.model.common.Bridgehead;
import de.samply.share.client.model.common.Cts;
import de.samply.share.client.model.common.Operator;
import de.samply.share.client.model.common.Urls;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.ReplyRuleType;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.JobSchedule;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizerImpl;
import de.samply.share.client.quality.report.chainlinks.statistics.manager.ChainStatisticsManager;
import de.samply.share.client.util.PatientValidator;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.CtsConnector;
import de.samply.share.client.util.connector.IdManagerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.LdmConnectorCql;
import de.samply.share.client.util.connector.LdmConnectorSamplystoreBiobank;
import de.samply.share.client.util.connector.MainzellisteConnector;
import de.samply.share.client.util.connector.StoreConnector;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.connector.MagicPlConnector;
import de.samply.share.client.util.connector.idmanagement.ldmswitch.LdmBasicConnectorSwitch;
import de.samply.share.client.util.connector.idmanagement.ldmswitch.LdmBasicConnectorSwitchFactory;
import de.samply.share.client.util.connector.idmanagement.ldmswitch.LdmBasicConnectorSwitchFactoryImpl;
import de.samply.share.client.util.connector.idmanagement.ldmswitch.LdmBasicConnectorSwitchFactoryParameters;
import de.samply.share.client.util.connector.idmanagement.ldmswitch.LdmConnectorSwitch;
import de.samply.share.client.util.connector.idmanagement.ldmswitch.LdmQueryLocationMapper;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverter;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverterFactoryImpl;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverterFactoryParameters;
import de.samply.share.client.util.connector.idmanagement.results.CentraXXundIdManagementResultBuilder;
import de.samply.share.client.util.connector.idmanagement.results.IdManagementResultGetter;
import de.samply.share.client.util.connector.idmanagement.results.IdManagementResultGetterException;
import de.samply.share.client.util.connector.idmanagement.results.LdmResultBuilder;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.JobScheduleUtil;
import de.samply.share.client.util.db.Migration;
import de.samply.share.common.model.dto.UserAgent;
import de.samply.share.common.utils.Constants;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.web.mdrfaces.MdrContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
import org.apache.http.HttpHeaders;
import org.apache.http.client.CredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.api.FlywayException;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.togglz.core.manager.FeatureManager;
import org.xml.sax.SAXException;

/**
 * Backing Bean that is valid during the whole runtime of the application. Holds methods that are
 * needed system-wide.
 */
@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {

  private static final Logger logger = LogManager.getLogger(ApplicationBean.class);

  private static final String PROJECT_DIRECTORY_FILENAME = "projectDirectory.json";
  private static final String COMMON_CONFIG_FILENAME_SUFFIX = "_common_config.xml";
  private static final String COMMON_URLS_FILENAME_SUFFIX = "_common_urls.xml";
  private static final String COMMON_OPERATOR_FILENAME_SUFFIX = "_common_operator.xml";
  private static final String COMMON_INFOS_FILENAME_SUFFIX = "_bridgehead_info.xml";
  private static final String CTS_FILENAME_SUFFIX = "_cts_info.xml";
  private static final List<String> NAMESPACES = new ArrayList<>(
      Arrays.asList("dktk", "adt", "marker"));

  private static final int TIMEOUT_IN_SECONDS = 60;
  private static final ConnectCheckResult shareAvailability = new ConnectCheckResult(true,
      "Samply.Share.Client", ProjectInfo.INSTANCE.getVersionString());
  private static final ChainStatisticsManager chainStatisticsManager = new ChainStatisticsManager();
  private static final ChainFinalizer chainFinalizer = new ChainFinalizerImpl();
  private static final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
  private static Urls urls;
  private static Operator operator;
  private static Bridgehead infos;
  private static Cts cts;
  private static boolean qrTaskRunning;
  private static Configuration configuration;
  private static MdrClient mdrClient;
  private static Scheduler scheduler;
  private static UserAgent userAgent;
  private static PatientValidator patientValidator;
  private static MdrConnection mdrConnection;
  private static MdrValidator mdrValidator;
  private static LdmConnector ldmConnector;
  private static MainzellisteConnector mainzellisteConnector;
  private static CtsConnector ctsConnector;
  private static ProjectDirectoryUtils projectDirectoryUtils;
  private static IdManagementConnector idManagementConnector;
  private static FeatureManager featureManager;
  private final ConnectCheckResult ldmAvailability = new ConnectCheckResult();
  private final ConnectCheckResult idmAvailability = new ConnectCheckResult();

  public static Locale getLocale() {
    return locale;
  }

  public static FeatureManager getFeatureManager() {
    return featureManager;
  }

  private static void initMainzelliste() {
    mainzellisteConnector = new MainzellisteConnector();
  }

  private static void initCts() {
    ctsConnector = new CtsConnector();
  }

  /**
   * Initialize a ldmConnector. Depends on which profile is selected.
   */
  public static void initLdmConnector() {
    switch (ApplicationUtils.getConnectorType()) {
      case DKTK:
        if (ConfigurationUtil
            .getConfigurationElementValueAsBoolean(EnumConfiguration.LDM_CACHING_ENABLED)) {
          try {
            int maxCacheSize = Integer.parseInt(ConfigurationUtil
                .getConfigurationElementValue(EnumConfiguration.LDM_CACHING_MAX_SIZE));
            ApplicationBean.ldmConnector = new LdmConnectorCentraxx(true, maxCacheSize);
          } catch (NumberFormatException e) {
            ApplicationBean.ldmConnector = new LdmConnectorCentraxx(true);
          }
        } else {
          ApplicationBean.ldmConnector = new LdmConnectorCentraxx(false);
        }

        ApplicationBean.ldmConnector = integrateSwitchInLdmConnectorCentraXX(
            (LdmConnectorCentraxx) ApplicationBean.ldmConnector, mdrClient);

        break;

      case SAMPLY:
        if (ApplicationUtils.isLanguageCql()) {
          ApplicationBean.ldmConnector = new LdmConnectorCql(false);
        } else {
          if (ConfigurationUtil
              .getConfigurationElementValueAsBoolean(EnumConfiguration.LDM_CACHING_ENABLED)) {
            try {
              int maxCacheSize = Integer.parseInt(ConfigurationUtil
                  .getConfigurationElementValue(EnumConfiguration.LDM_CACHING_MAX_SIZE));
              ApplicationBean.ldmConnector = new LdmConnectorSamplystoreBiobank(true, maxCacheSize);
            } catch (NumberFormatException e) {
              ApplicationBean.ldmConnector = new LdmConnectorSamplystoreBiobank(true);
            }
          } else {
            ApplicationBean.ldmConnector = new LdmConnectorSamplystoreBiobank(false);
          }
        }
        break;
      default:
        break;
    }
  }

  private static LdmConnector integrateSwitchInLdmConnectorCentraXX(
      LdmConnectorCentraxx ldmConnectorCentraxx, MdrClient mdrClient) {

    LdmBasicConnectorSwitch ldmBasicConnectorSwitch = createLdmBasicConnectorSwitch(
        idManagementConnector, projectDirectoryUtils, ldmConnectorCentraxx, mdrClient);
    return new LdmConnectorSwitch(ldmConnectorCentraxx, ldmBasicConnectorSwitch);

  }

  /**
   * Load the common config xml file from disk and apply the proxy settings.
   */
  private static void loadCommonConfig() {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
      configuration = JaxbUtil
          .findUnmarshall(
              ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_CONFIG_FILENAME_SUFFIX,
              jaxbContext, Configuration.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase(),
              System.getProperty("catalina.base") + File.separator + "conf",
              getServletContext().getRealPath("/WEB-INF"));
      CredentialsUtil.updateProxyCredentials(configuration);
      updateProxiesInDb();
    } catch (FileNotFoundException e) {
      logger.error(
          "No config file found by using samply.common.config for project " + ProjectInfo.INSTANCE
              .getProjectName());
    } catch (UnmarshalException ue) {
      throw new RuntimeException("Unable to unmarshal config file");
    } catch (SAXException | JAXBException | ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load the common urls xml file from disk.
   */
  private static void loadUrls() {
    try {
      JAXBContext jaxbContext = JAXBContext
          .newInstance(de.samply.share.client.model.common.ObjectFactory.class);
      urls = JaxbUtil
          .findUnmarshall(
              ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_URLS_FILENAME_SUFFIX,
              jaxbContext, Urls.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase(),
              System.getProperty("catalina.base") + File.separator + "conf",
              getServletContext().getRealPath("/WEB-INF"));
    } catch (FileNotFoundException e) {
      logger.error("No common urls file found by using samply.common.config for project "
          + ProjectInfo.INSTANCE.getProjectName());
    } catch (UnmarshalException ue) {
      throw new RuntimeException("Unable to unmarshal common_urls file", ue);
    } catch (SAXException | JAXBException | ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load the common operator xml file from disk.
   */
  private static void loadOperator() {
    try {
      JAXBContext jaxbContext = JAXBContext
          .newInstance(de.samply.share.client.model.common.ObjectFactory.class);
      operator = JaxbUtil
          .findUnmarshall(
              ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_OPERATOR_FILENAME_SUFFIX,
              jaxbContext, Operator.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase(),
              System.getProperty("catalina.base") + File.separator + "conf",
              getServletContext().getRealPath("/WEB-INF"));
    } catch (FileNotFoundException e) {
      logger.error("No common operator file found by using samply.common.config for project "
          + ProjectInfo.INSTANCE.getProjectName());
    } catch (UnmarshalException ue) {
      throw new RuntimeException("Unable to unmarshal common_operator file");
    } catch (SAXException | JAXBException | ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load the bridgehead info xml file from disk.
   */
  private static void loadBridgeheadInfo() {
    try {
      JAXBContext jaxbContext = JAXBContext
          .newInstance(de.samply.share.client.model.common.ObjectFactory.class);
      infos = JaxbUtil
          .findUnmarshall(
              ProjectInfo.INSTANCE.getProjectName().toLowerCase() + COMMON_INFOS_FILENAME_SUFFIX,
              jaxbContext, Bridgehead.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase(),
              System.getProperty("catalina.base") + File.separator + "conf",
              getServletContext().getRealPath("/WEB-INF"));
    } catch (FileNotFoundException e) {
      logger.error("No common bridgehead info file found by using samply.common.config for project "
          + ProjectInfo.INSTANCE.getProjectName());
    } catch (UnmarshalException ue) {
      throw new RuntimeException("Unable to unmarshal bridgehead_info file", ue);
    } catch (SAXException | JAXBException | ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load the CTS info xml file from disk.
   */
  private static void loadCtsInfo() {
    try {
      JAXBContext jaxbContext = JAXBContext
          .newInstance(de.samply.share.client.model.common.ObjectFactory.class);
      cts = JaxbUtil
          .findUnmarshall(ProjectInfo.INSTANCE.getProjectName().toLowerCase() + CTS_FILENAME_SUFFIX,
              jaxbContext, Cts.class, ProjectInfo.INSTANCE.getProjectName().toLowerCase(),
              System.getProperty("catalina.base") + File.separator + "conf",
              getServletContext().getRealPath("/WEB-INF"));
    } catch (FileNotFoundException e) {
      logger.error(
          "No CTS file found by using samply.common.config for project " + ProjectInfo.INSTANCE
              .getProjectName());
    } catch (UnmarshalException ue) {
      throw new RuntimeException("Unable to unmarshal CTS file", ue);
    } catch (SAXException | JAXBException | ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the proxy information in the db with the settings read from the config file.
   */
  private static void updateProxiesInDb() {
    try {
      de.samply.share.client.model.db.tables.pojos.Configuration httpProxyConfigElement =
          new de.samply.share.client.model.db.tables.pojos.Configuration();
      httpProxyConfigElement.setName(EnumConfiguration.HTTP_PROXY.name());
      httpProxyConfigElement.setSetting(configuration.getProxy().getHttp().getUrl().toString());
      ConfigurationUtil.insertOrUpdateConfigurationElement(httpProxyConfigElement);

      de.samply.share.client.model.db.tables.pojos.Configuration httpsProxyConfigElement =
          new de.samply.share.client.model.db.tables.pojos.Configuration();
      httpsProxyConfigElement.setName(EnumConfiguration.HTTPS_PROXY.name());
      httpsProxyConfigElement.setSetting(configuration.getProxy().getHttps().getUrl().toString());
      ConfigurationUtil.insertOrUpdateConfigurationElement(httpsProxyConfigElement);
    } catch (NullPointerException npe) {
      logger.debug(
          "Caught nullpointer exception while trying to update proxies. This will most likely "
              + "just mean that there is no proxy set in the config file. It is safe to ignore"
              + " this.");
    }
  }

  /**
   * Update the URLs to the local components in the db with the settings read from the corresponding
   * xml file.
   */
  private static void updateCommonUrls() {
    if (urls != null) {
      if (ApplicationUtils.isDktk()) {

        insertOrUpdateConfigurationElement(EnumConfiguration.ID_MANAGER_URL,
            urls.getIdmanagerUrl());
        insertOrUpdateConfigurationElement(EnumConfiguration.ID_MANAGER_API_KEY,
            urls.getIdmanagerApiKey());

      }

      insertOrUpdateConfigurationElement(EnumConfiguration.LDM_URL, urls.getLdmUrl());
      insertOrUpdateConfigurationElement(EnumConfiguration.SHARE_URL, urls.getShareUrl());
      insertOrUpdateConfigurationElement(EnumConfiguration.MDR_URL, urls.getMdrUrl());

    }
  }

  private static void insertOrUpdateConfigurationElement(EnumConfiguration enumConfiguration,
      String value) {

    de.samply.share.client.model.db.tables.pojos.Configuration configElement =
        new de.samply.share.client.model.db.tables.pojos.Configuration();
    configElement.setName(enumConfiguration.name());
    configElement.setSetting(value);
    ConfigurationUtil.insertOrUpdateConfigurationElement(configElement);

  }

  /**
   * Update the information associated with the CTS in the db with the settings read from the
   * corresponding xml file.
   */
  private static void updateCtsInfo() {
    if (cts != null) {
      if (ProjectInfo.INSTANCE.getProjectName().equals("dktk")) {
        insertConfigElement(EnumConfiguration.CTS_USERNAME.name(), cts.getUsername());
        insertConfigElement(EnumConfiguration.CTS_PASSWORD.name(), cts.getPassword());
        insertConfigElement(EnumConfiguration.CTS_URL.name(), cts.getUrl());
        insertConfigElement(EnumConfiguration.CTS_PROFILE.name(), cts.getProfile());
        insertConfigElement(EnumConfiguration.CTS_MAINZELLISTE_URL.name(),
            cts.getMainzellisteUrl());
        insertConfigElement(EnumConfiguration.CTS_MAINZELLISTE_API_KEY.name(),
            cts.getMainzellisteApiKey());
        insertConfigElement(EnumConfiguration.CTS_SEARCH_ID_TYPE.name(), cts.getSearchIdType());
      }
      if (ApplicationUtils.isSamply()) {
        de.samply.share.client.model.db.tables.pojos.Configuration directoryConfigElement =
            new de.samply.share.client.model.db.tables.pojos.Configuration();
        directoryConfigElement.setName(EnumConfiguration.DIRECTORY_URL.name());
        directoryConfigElement.setSetting(urls.getDirecotryUrl());
        ConfigurationUtil.insertOrUpdateConfigurationElement(directoryConfigElement);
      }
    }
  }

  private static void insertConfigElement(String name, String value) {
    de.samply.share.client.model.db.tables.pojos.Configuration configElement =
        new de.samply.share.client.model.db.tables.pojos.Configuration();
    configElement.setName(name);
    configElement.setSetting(value);
    ConfigurationUtil.insertOrUpdateConfigurationElement(configElement);
  }

  /**
   * Unschedule all jobs in a given group.
   *
   * @param groupName which job group shall have its jobs cancelled?
   */
  private static void cancelAllJobsInGroup(String groupName) {
    logger.info("Cancelling Jobs in group " + groupName);
    try {
      for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(groupName))) {
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
   * Cancel all jobs that are linked with an upload.
   */
  static void cancelAllJobsForUpload() {
    logger.info("Cancelling upload related jobs");
    try {
      for (JobExecutionContext jobExecutionContext : scheduler.getCurrentlyExecutingJobs()) {
        boolean isUpload = jobExecutionContext.getMergedJobDataMap()
            .getBoolean(CheckInquiryStatusJobParams.IS_UPLOAD);
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
   * Get the list of scheduled jobs from the database and arrange starting them.
   */
  static void scheduleJobsFromDatabase() {
    List<JobSchedule> jobSchedules = JobScheduleUtil.getJobSchedules();
    for (JobSchedule jobSchedule : jobSchedules) {
      QuartzJob quartzJob = new QuartzJob(jobSchedule.getJobKey(), null, null, null,
          jobSchedule.getCronExpression(), jobSchedule.getPaused(), "");
      rescheduleJobFromDatabase(quartzJob);
    }
  }

  /**
   * Add the trigger with the cron expression that is defined in the database to a job.
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
        throw new ValidatorException(
            new FacesMessage("Invalid Cron Expression: " + job.getCronExpression()));
      }

      CronTrigger newTrigger = TriggerBuilder
          .newTrigger()
          .withIdentity(job.getJobName() + ":trigger", job.getJobGroup())
          .withSchedule(
              CronScheduleBuilder.cronSchedule(job.getCronExpression())
          )
          .forJob(jobKey)
          .build();

      // You can't add a trigger in a paused state. So schedule it and immediately pause if
      // necessary
      scheduler.scheduleJob(newTrigger);
      if (job.isPaused()) {
        scheduler.pauseJob(jobKey);
      }

    } catch (SchedulerException e) {
      throw new ValidatorException(new FacesMessage(e.getLocalizedMessage()));
    }
  }

  public static Urls getUrlsForDirectory() {
    return urls;
  }

  public static Bridgehead getBridgeheadInfos() {
    return infos;
  }

  public static HttpConnector createHttpConnector() {
    return createHttpConnector(TIMEOUT_IN_SECONDS);
  }

  /**
   * Create an HttpConnector with a custom timeout.
   *
   * @param timeout timeout in seconds
   * @return HttpConnector with custom timeout
   */
  public static HttpConnector createHttpConnector(int timeout) {
    CredentialsProvider credentialsProvider = Utils.prepareCredentialsProvider();

    HttpConnector httpConnector = new HttpConnector(
        ConfigurationUtil.getHttpConfigParams(configuration), credentialsProvider, timeout);
    httpConnector.setUserAgent(getUserAgent().toString());
    httpConnector
        .addCustomHeader(Constants.HEADER_XML_NAMESPACE, Constants.VALUE_XML_NAMESPACE_COMMON);

    return httpConnector;
  }

  /**
   * Create an HttpConnector for a targetType.
   *
   * @param targetType the tagetType like local data management or httpProxy
   * @return HttpConnector for the targetType
   */
  public static HttpConnector createHttpConnector(TargetType targetType) {
    return createHttpConnector(targetType, TIMEOUT_IN_SECONDS);
  }

  /**
   * Create an HttpConnector for a targetType and a custom timeout.
   *
   * @param targetType the tagetType like local data management or httpProxy
   * @param timeout    timeout in seconds
   * @return HttpConnector for the targetType and a custom timeout
   */
  public static HttpConnector createHttpConnector(TargetType targetType, int timeout) {
    HttpConnector httpConnector = createHttpConnector(timeout);

    List<Credentials> credentialsByTarget = CredentialsUtil.getCredentialsByTarget(targetType);
    if (credentialsByTarget.isEmpty()) {
      logger.warn("No credentials for target type '" + targetType
          + "' found. Using default HttpConnector without credentials for '" + targetType + "'.");
      return createHttpConnector();
    }

    Credentials firstCredentials = credentialsByTarget.get(0);
    httpConnector.addCustomHeader(HttpHeaders.AUTHORIZATION, "Basic " + StoreConnector
        .getBase64Credentials(firstCredentials.getUsername(), firstCredentials.getPasscode()));

    return httpConnector;
  }

  public static MdrClient getMdrClient() {
    return mdrClient;
  }

  public static Configuration getConfiguration() {
    return configuration;
  }

  public static void setConfiguration(Configuration configuration) {
    ApplicationBean.configuration = configuration;
  }

  /**
   * Create a default UserAgent if not existing.
   *
   * @return the new UserAgent
   */
  public static UserAgent getUserAgent() {
    if (userAgent == null) {
      return getDefaultUserAgent();
    }

    return userAgent;
  }

  public static void setUserAgent(UserAgent userAgent) {
    ApplicationBean.userAgent = userAgent;
  }

  public static UserAgent getDefaultUserAgent() {
    return new UserAgent(ProjectInfo.INSTANCE.getProjectName(), "Samply.Share",
        ProjectInfo.INSTANCE.getVersionString());
  }

  public static Scheduler getScheduler() {
    return scheduler;
  }

  public static String getDisplayName() {
    return ApplicationUtils.getConnectorType().getDisplayName();
  }

  /**
   * Return LdmConnector. If no one exists then create one.
   *
   * @return LdmConnector
   */
  public static LdmConnector getLdmConnector() {
    if (ApplicationBean.ldmConnector == null) {
      ApplicationBean.initLdmConnector();
    }
    return ApplicationBean.ldmConnector;
  }

  /**
   * Return CtsConnector. If no one exists then create one.
   *
   * @return CtsConnector
   */
  public static CtsConnector getCtsConnector() {
    if (ApplicationBean.ctsConnector == null) {
      ApplicationBean.initCts();
    }
    return ctsConnector;
  }

  /**
   * Return MainzellisteConnector. If no one exists then create one.
   *
   * @return MainzellisteConnector
   */
  public static MainzellisteConnector getMainzellisteConnector() {
    if (ApplicationBean.mainzellisteConnector == null) {
      ApplicationBean.initMainzelliste();
    }
    return mainzellisteConnector;
  }

  public static PatientValidator getPatientValidator() {
    return patientValidator;
  }

  static ChainStatisticsManager getChainStatisticsManager() {
    return chainStatisticsManager;
  }

  static ChainFinalizer getChainFinalizer() {
    return chainFinalizer;
  }

  static MdrValidator getMdrValidator() {
    return mdrValidator;
  }

  private static LdmBasicConnectorSwitch createLdmBasicConnectorSwitch(
      IdManagementConnector idManagementConnector, ProjectDirectoryUtils projectDirectoryUtils,
      LdmConnectorCentraxx ldmConnectorCentraxx, MdrClient mdrClient) {

    LdmQueryConverter ldmQueryConverter = createLdmQueryConverter(idManagementConnector,
        projectDirectoryUtils);
    LdmBasicConnectorSwitchFactoryParameters ldmBasicConnectorSwitchFactoryParameters =
        new LdmBasicConnectorSwitchFactoryParameters();
    LdmQueryLocationMapper ldmQueryLocationMapper = new LdmQueryLocationMapper();
    IdManagementResultGetter idManagementResultGetter = createIdManagementResultGetter(
        idManagementConnector, projectDirectoryUtils, mdrClient);
    LdmResultBuilder ldmResultBuilder = new CentraXXundIdManagementResultBuilder(
        ldmConnectorCentraxx, idManagementResultGetter);

    ldmBasicConnectorSwitchFactoryParameters.setLdmConnectorCentraxx(ldmConnectorCentraxx);
    ldmBasicConnectorSwitchFactoryParameters.setLdmQueryConverter(ldmQueryConverter);
    ldmBasicConnectorSwitchFactoryParameters.setLdmQueryLocationMapper(ldmQueryLocationMapper);
    ldmBasicConnectorSwitchFactoryParameters.setLdmResultBuilder(ldmResultBuilder);

    LdmBasicConnectorSwitchFactory ldmBasicConnectorSwitchFactory =
        new LdmBasicConnectorSwitchFactoryImpl(ldmBasicConnectorSwitchFactoryParameters);

    return ldmBasicConnectorSwitchFactory.createLdmBasicConnectorSwitch();

  }

  private static IdManagementResultGetter createIdManagementResultGetter(
      IdManagementConnector idManagementConnector, ProjectDirectoryUtils projectDirectoryUtils,
      MdrClient mdrClient) {
    try {
      return new IdManagementResultGetter(idManagementConnector, projectDirectoryUtils, mdrClient);
    } catch (IdManagementResultGetterException e) {
      throw new RuntimeException(e);
    }
  }

  private static LdmQueryConverter createLdmQueryConverter(
      IdManagementConnector idManagementConnector, ProjectDirectoryUtils projectDirectoryUtils) {

    LdmQueryConverterFactoryParameters ldmQueryConverterFactoryParameters =
        new LdmQueryConverterFactoryParameters();
    ldmQueryConverterFactoryParameters.setIdManagementConnector(idManagementConnector);
    ldmQueryConverterFactoryParameters.setProjectDirectoryUtils(projectDirectoryUtils);

    LdmQueryConverterFactoryImpl ldmQueryConverterFactory = new LdmQueryConverterFactoryImpl(
        ldmQueryConverterFactoryParameters);

    return ldmQueryConverterFactory.createLdmQueryConverter();

  }

  public static IdManagementConnector getIdManagementConnector() {
    return idManagementConnector;
  }

  private static ProjectDirectory createProjectDirectory() {

    DktkProjectDirectoryParameters projectDirectoryParameters =
        new DktkProjectDirectoryParameters();

    String destinationFilePath =
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY)
            + File.separator + PROJECT_DIRECTORY_FILENAME;
    String sourceFilePath = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.PROJECT_DIRECTORY_URL);
    HttpConnector httpConnector = createHttpConnector();

    projectDirectoryParameters.setDestinationFilePath(destinationFilePath);
    projectDirectoryParameters.setSourceFilePath(sourceFilePath);
    projectDirectoryParameters.setHttpConnector(httpConnector);

    return new DktkProjectDirectory(projectDirectoryParameters);

  }

  private static ProjectDirectoryUtils createProjectDirectoryUtils() {

    ProjectDirectory projectDirectory = createProjectDirectory();
    return new ProjectDirectoryUtils(projectDirectory);

  }

  /**
   * Initialize the settings for the share client.
   */
  @PostConstruct
  public void init() {
    // On startup, check if there are changes to be done in the database
    try {
      logger.info("Migrating Flyway...");
      Migration.doUpgrade();
    } catch (FlywayException e) {
      logger.fatal("Could not initialize or migrate database", e);
      throw new RuntimeException(e);
    }
    logger.info("Creating featureManager...");
    featureManager = new ClientConfiguration().getFeatureManager();

    logger.info("Loading common-config.xml...");
    loadCommonConfig();

    logger.info("Loading Urls...");
    loadUrls();
    logger.info("Loading operator...");
    loadOperator();
    logger.info("Loading bidgehead info...");
    loadBridgeheadInfo();
    logger.info("Loading common urls...");
    updateCommonUrls();
    logger.info("Loading project directory...");
    loadProjectDirectoryClient();
    logger.info("Loading id management connector");
    loadIdManagementConnector();

    logger.info("Reseting MDR context");
    resetMdrContext();
    logger.info("Loading patient validator...");
    patientValidator = new PatientValidator(MdrContext.getMdrContext().getMdrClient());

    // Initialize Quartz scheduler
    try {
      logger.info("Initializing scheduler...");
      initScheduler();
    } catch (SchedulerException e) {
      throw new RuntimeException("Could not initialize quartz scheduler.", e);
    }

    logger.info("Initializing dth validator...");
    initDthValidator();

    logger.info("Initializing ldm connector...");
    ApplicationBean.initLdmConnector();

    logger.info("Inserting event log entry...");
    EventLogUtil.insertEventLogEntry(EventMessageType.E_SYSTEM_STARTUP);

    logger.info("Checking Processing inquiries...");
    checkProcessingInquiries();
    if (featureManager.getFeatureState(ClientFeature.NNGM_CTS).isEnabled()) {
      loadCtsInfo();
      updateCtsInfo();
      initMainzelliste();
    }
    logger.info("Application Bean initialized");
  }

  private void loadProjectDirectoryClient() {
    projectDirectoryUtils = createProjectDirectoryUtils();
  }

  private void loadIdManagementConnector() {
    idManagementConnector = new MagicPlConnector();
  }

  private void checkProcessingInquiries() {
    List<InquiryDetails> inquiryDetailsList = InquiryDetailsUtil
        .getInquiryDetailsByStatus(InquiryStatusType.IS_PROCESSING);
    inquiryDetailsList.addAll(
        (InquiryDetailsUtil.getInquiryDetailsByStatus(InquiryStatusType.IS_PARTIALLY_READY)));
    for (InquiryDetails inquiryDetails : inquiryDetailsList) {
      inquiryDetails.setStatus(InquiryStatusType.IS_NEW);
      InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);

      setInquiryCriteriaStatusNew(inquiryDetails);
    }
  }

  private void setInquiryCriteriaStatusNew(InquiryDetails inquiryDetails) {
    for (InquiryCriteria inquiryCriteria : InquiryCriteriaUtil
        .getInquiryCriteriaForInquiryDetails(inquiryDetails)) {
      inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_NEW);
      InquiryCriteriaUtil.updateInquiryCriteria(inquiryCriteria);
    }
  }

  /**
   * Initialize the Quartz Scheduler. The configuration is done via web.xml and quartz.properties.
   */
  private void initScheduler() throws SchedulerException {
    logger.info("Initializing FacesContext...");
    ServletContext servletContext = (ServletContext) FacesContext
        .getCurrentInstance().getExternalContext().getContext();

    //Get QuartzInitializerListener
    logger.info("Initializing Quartz Listener...");
    StdSchedulerFactory stdSchedulerFactory = (StdSchedulerFactory) servletContext
        .getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);

    logger.info("Getting scheduler...");
    scheduler = stdSchedulerFactory.getScheduler();
    while (!scheduler.isStarted()) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        logger.warn("Caught Interrupted exception while trying to wait for scheduler start.", e);
      }
    }
    logger.info("Scheduling jobs...");
    scheduleJobsFromDatabase();
  }

  private void initDthValidator() {
    try {

      mdrConnection = new MdrConnection(
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL),
          null,
          null,
          null,
          null,
          NAMESPACES,
          true,
          createHttpConnector());
      mdrValidator = new MdrValidator(mdrConnection, true);
    } catch (MdrConnectionException | ExecutionException | MdrException
        | MdrInvalidResponseException e) {
      logger.error("Error initializing DTH Validator", e);
    }
  }

  /**
   * Reinitialize the MdrClient. Create a new MdrClient and clean the cache.
   */
  private void resetMdrContext() {
    String mdrUrl;

    mdrUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL);
    mdrClient = new MdrClient(mdrUrl, createHttpConnector().getJerseyClient(mdrUrl));
    mdrClient.cleanCache();
    MdrContext.getMdrContext().init(mdrClient);
    logger.debug(
        "Reinitialized MDR Client with url " + mdrUrl + " - base uri is " + mdrClient.getBaseUri());
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

  /**
   * Check the versions and availability of local datamanagement and id manager.
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
    } catch (LdmConnectorException e) {
      ldmAvailability.setReachable(false);
    }

    try {
      IdManagerConnector idManagerConnector = new IdManagerConnector();
      String idManagerUserAgentInfo = idManagerConnector.getUserAgentInfo();
      List<String> list = Splitter.on('/').splitToList(idManagerUserAgentInfo);
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

  /**
   * Get the array of defined reply rule types. This should not be necessary, since it is possible
   * to reference the Enum Class from the xhtml page directly. However, in this case, the
   * translations were not working - so this workaround is chosen.
   *
   * @return an array of all implemented reply rules TODO: change this, when "reply with data" is
   *        defined
   */
  public ReplyRuleType[] getReplyRuleTypes() {
    ReplyRuleType[] replyRuleTypes = new ReplyRuleType[2];
    replyRuleTypes[0] = ReplyRuleType.RR_NO_AUTOMATIC_ACTION;
    replyRuleTypes[1] = ReplyRuleType.RR_TOTAL_COUNT;
    return replyRuleTypes;
  }
}
