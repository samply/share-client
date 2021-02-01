package de.samply.share.client.control;

import com.google.common.net.HttpHeaders;
import de.samply.dktk.converter.EnumValidationHandling;
import de.samply.dktk.converter.PatientConverter;
import de.samply.dktk.converter.PatientConverterUtil;
import de.samply.share.client.job.ExecuteInquiryJobCentraxx;
import de.samply.share.client.job.ExecuteInquiryJobCql;
import de.samply.share.client.job.ExecuteInquiryJobSamplystoreBiobanks;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.job.util.CqlResultFactory;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.Contact;
import de.samply.share.client.model.db.tables.pojos.Document;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryAnswer;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.InquiryResultStats;
import de.samply.share.client.model.db.tables.pojos.RequestedEntity;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.rest.Connector;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.WebUtils;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.ContactUtil;
import de.samply.share.client.util.db.DocumentUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryAnswerUtil;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultStatsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UserSeenInquiryUtil;
import de.samply.share.common.model.uiquerybuilder.QueryItem;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.QueryTreeUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Container;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.Result;
import de.samply.share.model.cql.CqlResult;
import de.samply.share.utils.Converter;
import de.samply.web.mdrfaces.MdrContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.omnifaces.model.tree.ListTreeModel;
import org.omnifaces.model.tree.TreeModel;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 * ViewScoped backing bean, used for pages that deal with inquiries.
 */
@ManagedBean(name = "inquiryBean")
@ViewScoped
public class InquiryBean implements Serializable {

  private static final Logger logger = LogManager.getLogger(InquiryBean.class);
  private static final String RESET_FILEINPUT = "$('.fileinput-remove-button').trigger('click');";
  private static final String CREATE_EVENTHANDLERS = "createEventhandlers();";
  private static final String XMLNS_PATH_COMMON = "/common/";
  private static final String XMLNS_PATH_CCP = "/ccp/";
  private static final String XMLNS_PATH_OSSE = "/osse/";
  @ManagedProperty(value = "#{loginBean}")
  private LoginBean loginBean;
  private LdmConnector ldmConnector;
  private int selectedInquiryId;
  private Inquiry inquiry;
  private InquiryDetails latestInquiryDetails;
  private InquiryResult latestInquiryResult;
  private InquiryAnswer latestInquiryAnswer;
  private List<InquiryResult> inquiryResultsList;
  private List<EventLog> inquiryEvents;
  private boolean resultsReady;
  private QueryResultStatistic latestResultStatistics;
  private Result latestQueryResult;
  private TreeModel<Container> patientPageTree;
  private List<InquiryCriteria> inquiryCriteria;
  private InquiryResultStats latestInquiryResultStats;
  private List<Document> documents;
  private Part newDocument;
  /**
   * A tree holding query items (and conjunction groups). Basically "the inquiry"
   */
  private TreeModel<QueryItem> latestOriginalCriteriaTree;
  private String requestedEntitiesLabelString;
  private Contact selectedInquiryContact;

  /**
   * Transform xml patient list page to tree model.
   *
   * @param queryResultPage xml list of patients
   */
  private static TreeModel<Container> resultPageToTree(Result queryResultPage) {
    TreeModel<Container> containerTree = new ListTreeModel<>();

    switch (ApplicationUtils.getConnectorType()) {
      case DKTK:

        QueryResult queryResultPageCcp = (QueryResult) queryResultPage;
        PercentageLogger percentageLogger = new PercentageLogger(logger,
            queryResultPageCcp.getPatient().size(), "building tree model...");

        for (de.samply.share.model.ccp.Patient patient : queryResultPageCcp.getPatient()) {

          de.samply.share.model.ccp.Container patientContainer =
              new de.samply.share.model.ccp.Container();
          patientContainer.getAttribute().addAll(patient.getAttribute());
          patientContainer.getContainer().addAll(patient.getContainer());
          patientContainer.setId(patient.getId());
          de.samply.share.model.common.Container containerTmp =
              new de.samply.share.model.common.Container();
          try {
            containerTmp = Converter.convertCcpContainerToCommonContainer(patientContainer);
          } catch (JAXBException e) {
            e.printStackTrace();
          }
          containerTree = visitContainerNode(containerTree, containerTmp);

          percentageLogger.incrementCounter();

        }
        break;

      case SAMPLY:
        BbmriResult queryResultPageBbmri = (BbmriResult) queryResultPage;
        for (de.samply.share.model.osse.Patient donor : queryResultPageBbmri.getDonors()) {
          de.samply.share.model.osse.Container patientContainer =
              new de.samply.share.model.osse.Container();
          patientContainer.getAttribute().addAll(donor.getAttribute());
          patientContainer.getContainer().addAll(donor.getContainer());
          patientContainer.setId(donor.getId());
          de.samply.share.model.common.Container containerTmp =
              new de.samply.share.model.common.Container();
          try {
            containerTmp = Converter.convertOsseContainerToCommonContainer(patientContainer);
          } catch (JAXBException e) {
            e.printStackTrace();
          }

          containerTree = visitContainerNode(containerTree, containerTmp);
        }
        break;
      default:
        break;
    }
    return containerTree;
  }

  /**
   * Add the information from the container to the parent node in the tree. Do this recursively for
   * the sub-containers as well.
   *
   * @param parentNode the treenode to which the container information will be attached
   * @param node       the container entity
   * @return the parent treenode, with attached information from this container
   */
  private static TreeModel<Container> visitContainerNode(TreeModel<Container> parentNode,
      Container node) {

    if (node.getContainer() == null || node.getContainer().isEmpty()) {
      parentNode.addChild(node);
    } else {
      TreeModel<Container> newNode = parentNode.addChild(node);
      for (Container subContainer : node.getContainer()) {
        newNode = visitContainerNode(newNode, subContainer);
      }
    }
    return parentNode;
  }

  @PostConstruct
  public void init() {
    ldmConnector = ApplicationBean.getLdmConnector();
  }

  public LoginBean getLoginBean() {
    return loginBean;
  }

  public void setLoginBean(LoginBean loginBean) {
    this.loginBean = loginBean;
  }

  public int getSelectedInquiryId() {
    return selectedInquiryId;
  }

  public void setSelectedInquiryId(int selectedInquiryId) {
    this.selectedInquiryId = selectedInquiryId;
  }

  public Inquiry getInquiry() {
    return inquiry;
  }

  public void setInquiry(Inquiry inquiry) {
    this.inquiry = inquiry;
  }

  public InquiryDetails getLatestInquiryDetails() {
    return latestInquiryDetails;
  }

  public void setLatestInquiryDetails(InquiryDetails latestInquiryDetails) {
    this.latestInquiryDetails = latestInquiryDetails;
  }

  public InquiryResult getLatestInquiryResult() {
    return latestInquiryResult;
  }

  public void setLatestInquiryResult(InquiryResult latestInquiryResult) {
    this.latestInquiryResult = latestInquiryResult;
  }

  public List<InquiryResult> getInquiryResultsList() {
    return inquiryResultsList;
  }

  public void setInquiryResultsList(List<InquiryResult> inquiryResultsList) {
    this.inquiryResultsList = inquiryResultsList;
  }

  public InquiryAnswer getLatestInquiryAnswer() {
    return latestInquiryAnswer;
  }

  public void setLatestInquiryAnswer(InquiryAnswer latestInquiryAnswer) {
    this.latestInquiryAnswer = latestInquiryAnswer;
  }

  public List<EventLog> getInquiryEvents() {
    return inquiryEvents;
  }

  public void setInquiryEvents(List<EventLog> inquiryEvents) {
    this.inquiryEvents = inquiryEvents;
  }

  public boolean isResultsReady() {
    return resultsReady;
  }

  public void setResultsReady(boolean resultsReady) {
    this.resultsReady = resultsReady;
  }

  public QueryResultStatistic getLatestResultStatistics() {
    return latestResultStatistics;
  }

  public void setLatestResultStatistics(QueryResultStatistic latestResultStatistics) {
    this.latestResultStatistics = latestResultStatistics;
  }

  public TreeModel<Container> getPatientPageTree() {
    return patientPageTree;
  }

  public void setPatientPageTree(TreeModel<Container> patientPageTree) {
    this.patientPageTree = patientPageTree;
  }

  public TreeModel<QueryItem> getLatestOriginalCriteriaTree() {
    return latestOriginalCriteriaTree;
  }

  public void setLatestOriginalCriteriaTree(TreeModel<QueryItem> latestOriginalCriteriaTree) {
    this.latestOriginalCriteriaTree = latestOriginalCriteriaTree;
  }

  public String getRequestedEntitiesLabelString() {
    return requestedEntitiesLabelString;
  }

  public void setRequestedEntitiesLabelString(String requestedEntitiesLabelString) {
    this.requestedEntitiesLabelString = requestedEntitiesLabelString;
  }

  public Contact getSelectedInquiryContact() {
    return selectedInquiryContact;
  }

  public void setSelectedInquiryContact(Contact selectedInquiryContact) {
    this.selectedInquiryContact = selectedInquiryContact;
  }

  public InquiryResultStats getLatestInquiryResultStats() {
    return latestInquiryResultStats;
  }

  public void setLatestInquiryResultStats(InquiryResultStats latestInquiryResultStats) {
    this.latestInquiryResultStats = latestInquiryResultStats;
  }

  public List<InquiryCriteria> getInquiryCriteria() {
    return inquiryCriteria;
  }

  public void setInquiryCriteria(List<InquiryCriteria> inquiryCriteria) {
    this.inquiryCriteria = inquiryCriteria;
  }

  public List<Document> getDocuments() {
    return documents;
  }

  public void setDocuments(List<Document> documents) {
    this.documents = documents;
  }

  public Part getNewDocument() {
    return newDocument;
  }

  public void setNewDocument(Part newDocument) {
    this.newDocument = newDocument;
  }

  /**
   * Load the selected inquiry (which is defined by the view parameter) and all related details.
   */
  public void loadSelectedInquiry() {
    try {
      inquiry = InquiryUtil.fetchInquiryById(selectedInquiryId);
      latestInquiryDetails = InquiryDetailsUtil
          .fetchInquiryDetailsById(inquiry.getLatestDetailsId());
      latestInquiryAnswer = InquiryAnswerUtil
          .fetchInquiryAnswerByInquiryDetailsId(latestInquiryDetails.getId());
      inquiryResultsList = InquiryResultUtil
          .fetchInquiryResultsForInquiryDetailsById(latestInquiryDetails.getId());
      UserSeenInquiryUtil.setUserSeenInquiry(loginBean.getUser(), inquiry);
      //TODO create criteriaTree with cql query
      if (inquiryCriteria != null) {
        if (ApplicationUtils.isLanguageQuery()) {
          this.inquiryCriteria.add(InquiryCriteriaUtil
              .getFirstCriteriaOriginal(latestInquiryDetails, QueryLanguageType.QL_QUERY));
          latestOriginalCriteriaTree = populateCriteriaTree(
              inquiryCriteria.get(0).getCriteriaOriginal());
        } else {
          this.inquiryCriteria = InquiryCriteriaUtil
              .getInquiryCriteriaForInquiryDetails(latestInquiryDetails);
        }
      }

      List<RequestedEntity> requestedEntities = InquiryUtil.getRequestedEntitiesForInquiry(inquiry);
      requestedEntitiesLabelString = Connector.getLabelsFor(requestedEntities);
      selectedInquiryContact = ContactUtil.fetchContactById(latestInquiryDetails.getContactId());
      inquiryEvents = EventLogUtil.fetchEventLogForInquiryById(inquiry.getId());
      loadDocuments();
      if (!SamplyShareUtils.isNullOrEmpty(inquiryResultsList)) {
        latestInquiryResult = inquiryResultsList.get(inquiryResultsList.size() - 1);
        try {
          resultsReady = ldmConnector.isFirstResultPageAvailable(latestInquiryResult.getLocation());
          // TODO: other types
          latestResultStatistics = ldmConnector
              .getQueryResultStatistic(latestInquiryResult.getLocation());
        } catch (LdmConnectorException e) {
          resultsReady = false;
          latestResultStatistics = new QueryResultStatistic();
        }
        latestInquiryResultStats = InquiryResultStatsUtil
            .getInquiryResultStatsForInquiryResultById(latestInquiryResult.getId());
      }
    } catch (NullPointerException npe) {
      //throw new RuntimeException("Could not load inquiry, inquirydetails or inquiry answer.");
      logger.error(npe);
    }
  }

  /**
   * Generate the query criteria tree from the criteria string in common namespace.
   *
   * @param queryString the query in xml representation
   * @return the tree representation of the query
   */
  private TreeModel<QueryItem> populateCriteriaTree(String queryString) {
    if (inquiry == null) {
      String msg = "Inquiry is null. Can't load criteria tree";
      logger.error(msg);
      throw new RuntimeException(msg);
    }

    return QueryTreeUtil.queryStringToTree(queryString);
  }

  /**
   * Move an inquiry to the archive.
   *
   * @return redirect url
   */
  public String archive() {
    inquiry.setArchivedAt(SamplyShareUtils.getCurrentSqlTimestamp());
    Utils.setStatus(latestInquiryDetails, InquiryStatusType.IS_ARCHIVED);

    InquiryUtil.updateInquiry(inquiry);
    InquiryDetailsUtil.updateInquiryDetails(latestInquiryDetails);

    EventLogUtil
        .insertEventLogEntryForInquiryId(EventMessageType.E_INQUIRY_ARCHIVED, inquiry.getId());
    return "inquiries_archive?faces-redirect=true";
  }

  /**
   * Attach a single-fire trigger to the execute inquiry job.
   *
   * @param statsOnly define if only the stats shall be requested
   * @return navigation information
   */
  public String spawnExecuteTask(boolean statsOnly) {

    Utils.setStatus(latestInquiryDetails, InquiryStatusType.IS_PROCESSING);
    InquiryDetailsUtil.updateInquiryDetails(latestInquiryDetails);

    JobBuilder jobBuilder;

    if (ApplicationUtils.isDktk()) {
      jobBuilder = JobBuilder.newJob(ExecuteInquiryJobCentraxx.class);
    } else if (ApplicationUtils.isLanguageQuery()) {
      jobBuilder = JobBuilder.newJob(ExecuteInquiryJobSamplystoreBiobanks.class);
    } else {
      jobBuilder = JobBuilder.newJob(ExecuteInquiryJobCql.class);
    }
    String jobGroup =
        inquiry.getBrokerId() + "::" + inquiry.getSourceId() + "::" + latestInquiryDetails
            .getRevision();
    JobKey jobKey = JobKey.jobKey(ExecuteInquiryJobParams.getJobName(), "job::" + jobGroup);
    TriggerKey triggerKey = TriggerKey
        .triggerKey(ExecuteInquiryJobParams.getJobName(), "trigger::" + jobGroup);
    JobDetail inquiryExecutionJob = jobBuilder
        .withIdentity(jobKey)
        .usingJobData(ExecuteInquiryJobParams.INQUIRY_ID, inquiry.getId())
        .usingJobData(ExecuteInquiryJobParams.INQUIRY_DETAILS_ID, latestInquiryDetails.getId())
        .usingJobData(ExecuteInquiryJobParams.STATS_ONLY, statsOnly)
        .build();

    /* Only fire this job once. Right now. */
    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(triggerKey)
        .startNow()
        .build();

    try {
      logger.info("Give Execute Job to scheduler for inquiry with id " + inquiry.getId());

      ApplicationBean.getScheduler().scheduleJob(inquiryExecutionJob, trigger);

      Messages.create("Inquiry Execution Job spawned")
          .detail("The Job has been spawned. It might take a while until it is completed.")
          .add();
      return "inquiries_list?faces-redirect=true";
    } catch (SchedulerException e) {
      logger.error("Error spawning Inquiry Execution Job", e);
      Messages.create("Inquiry Execution Job could not be spawned")
          .detail("An Scheduler Exception occurred: " + e.getMessage())
          .error().add();
      return "";
    }
  }

  /**
   * Attach a single-fire trigger to the execute inquiry job, requesting only stats.
   *
   * @return navigation information
   */
  public String spawnExecuteTask() {
    return spawnExecuteTask(true);
  }

  /**
   * Load the result of an inquiry from local datamanagement.
   */
  public void loadResult() {
    logger.debug("loadResult called");
    try {
      if (latestInquiryResult != null
          && !latestInquiryResult.getStatisticsOnly()
          && !latestInquiryResult.getIsError()
          && latestInquiryResult.getSize() != null
          && latestInquiryResult.getSize() > 0
          && ldmConnector.isResultDone(latestInquiryResult.getLocation(), latestResultStatistics)) {

        populateQueryResult();

      }
    } catch (LdmConnectorException e) {
      logger.error("An error occurred while trying to get and transform the result from LDM.");
    }
  }

  /**
   * Load the first result page for the selected inquiry.
   */
  private void populateQueryResult() throws LdmConnectorException {
    String queryResultLocation = latestInquiryResult.getLocation();
    logger.info("getting page 0...");
    switch (ApplicationUtils.getConnectorType()) {
      case DKTK:
        latestQueryResult = (QueryResult) ldmConnector.getResultsFromPage(queryResultLocation, 0);
        break;

      case SAMPLY:
        latestQueryResult = (BbmriResult) ldmConnector.getResultsFromPage(queryResultLocation, 0);
        break;
      default:
        break;
    }
    buildPatientPageTree(latestQueryResult);
  }

  /**
   * Load another page of the result. Page number comes from the paginator widget on the results
   * page.
   */
  public void changeResultPage() {
    int page = 0;
    try {
      page = Integer.parseInt(Faces.getRequestParameter("page"));
      page = page - 1; // paginator starts with 1, result from local datamanagement starts with 0
    } catch (NumberFormatException e) {
      logger.warn("Could not parse page number: " + page);
    }

    try {
      logger.info("getting results... page " + page);
      switch (ApplicationUtils.getConnectorType()) {
        case DKTK:
          latestQueryResult = (QueryResult) ldmConnector
              .getResultsFromPage(latestInquiryResult.getLocation(), page);
          break;

        case SAMPLY:
          latestQueryResult = (BbmriResult) ldmConnector
              .getResultsFromPage(latestInquiryResult.getLocation(), page);
          break;
        default:
          break;
      }
    } catch (LdmConnectorException e) {
      logger.error("Error changing result page", e);
    }
    buildPatientPageTree(latestQueryResult);
  }

  /**
   * Construct the tree with the patient information from the (xml) query result page.
   *
   * @param queryResultPage xml list of patients
   */
  private void buildPatientPageTree(Result queryResultPage) {
    if (queryResultPage == null) {
      logger.error("Could not build tree. Result is null.");
    }
    patientPageTree = resultPageToTree(queryResultPage);
  }

  private List<MdrIdDatatype> getExportMdrBlackList() {

    try {
      List<MdrIdDatatype> mdrIdDatatypeList = new ArrayList<>();

      List<String> configurationElementValueList = ConfigurationUtil
          .getConfigurationElementValueList(EnumConfiguration.EXPORT_MDR_BLACKLIST);

      for (String mdrIds : configurationElementValueList) {
        if (mdrIds.length() > 0) {
          mdrIdDatatypeList.add(new MdrIdDatatype(mdrIds));
        }
      }

      return mdrIdDatatypeList;

    } catch (Exception e) {
      logger.error(e);
      return new ArrayList<>();
    }

  }

  /**
   * Generate an Excel Workbook for the inquiry result and send it to the client.
   */
  public void generateExportFile(EnumValidationHandling validationHandling) {
    logger.debug("Generate Export File");

    // Add a list of mdr items that will not be included in the export.
    List<MdrIdDatatype> blacklist = getExportMdrBlackList();

    try {
      String queryResultLocation = latestInquiryResult.getLocation();
      PatientConverter patientConverter = new PatientConverter(
          MdrContext.getMdrContext().getMdrClient(),
          ApplicationBean.getMdrValidator(),
          validationHandling,
          blacklist);
      // TODO Use switch statement on ApplicationUtils.getConnectorType()
      Workbook workbook = null;
      if (ApplicationUtils.isDktk()) {
        QueryResult queryResult = (QueryResult) ldmConnector.getResults(queryResultLocation);
        logger.debug("Result completely loaded...write excel file");
        String executionDateString = WebUtils.getExecutionDate(latestInquiryResult);
        workbook = patientConverter.centraxxQueryResultToExcel(queryResult,
            PatientConverterUtil
                .createInquiryObjectForInfoSheet(inquiry.getLabel(), inquiry.getDescription()),
            PatientConverterUtil.createContactObjectForInfoSheet(selectedInquiryContact.getTitle(),
                selectedInquiryContact.getFirstName(), selectedInquiryContact.getLastName()),
            ConfigurationUtil
                .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID),
            executionDateString
        );
      } else if (ApplicationUtils.isSamply()) {
        BbmriResult queryResult = (BbmriResult) ldmConnector.getResults(queryResultLocation);
        logger.debug("Result completely loaded...write excel file");
        String executionDateString = WebUtils.getExecutionDate(latestInquiryResult);
        workbook = patientConverter.biobanksQueryResultToExcel(queryResult,
            PatientConverterUtil
                .createInquiryObjectForInfoSheet(inquiry.getLabel(), inquiry.getDescription()),
            PatientConverterUtil.createContactObjectForInfoSheet(selectedInquiryContact.getTitle(),
                selectedInquiryContact.getFirstName(), selectedInquiryContact.getLastName()),
            ConfigurationUtil
                .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID),
            executionDateString
        );
      }

      logger.debug("Workbook complete");

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try {
        workbook.write(bos);
      } finally {
        bos.close();
      }
      String filename =
          !(inquiry.getLabel().equals("")) ? inquiry.getLabel() + ".xlsx" : "Export.xlsx";

      createTemporaryFile(bos, "lastExport.xlsx");
      Faces.sendFile(bos.toByteArray(), filename, true);


    } catch (Exception e) {
      logger.error("Exception caught while trying to export data", e);
    }
  }

  private void createTemporaryFile(ByteArrayOutputStream byteArrayOutputStream, String filename) {

    try {

      String path = ConfigurationUtil
          .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
      filename = path + File.separator + filename;
      FileOutputStream fileOutputStream = new FileOutputStream(filename);
      byteArrayOutputStream.writeTo(fileOutputStream);

    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  /**
   * Send a reply back to the broker. Currently only supports the size. TODO: Add support for other
   * reply types TODO: Add success/error message
   */
  public String reply() {
    try {
      BrokerConnector brokerConnector = new BrokerConnector(
          BrokerUtil.fetchBrokerById(inquiry.getBrokerId()));
      switch (ApplicationUtils.getConnectorType()) {
        case DKTK:
          brokerConnector.reply(latestInquiryDetails, latestInquiryResult.getSize());
          break;

        case SAMPLY:
          if (ApplicationUtils.isLanguageQuery()) {
            try {
              BbmriResult queryResult = (BbmriResult) ldmConnector.getResults(InquiryResultUtil
                  .fetchLatestInquiryResultForInquiryDetailsById(latestInquiryDetails.getId())
                  .getLocation());
              brokerConnector.reply(latestInquiryDetails, queryResult);
            } catch (LdmConnectorException e) {
              e.printStackTrace();
            }
          } else if (ApplicationUtils.isLanguageCql()) {
            CqlResult queryResult = new CqlResultFactory(latestInquiryDetails).createCqlResult();
            brokerConnector.reply(latestInquiryDetails, queryResult);
          }
          break;
        default:
          break;
      }
      return "/user/show_inquiry.xhtml?inquiryId=" + inquiry.getId() + "&faces-redirect=true";
    } catch (BrokerConnectorException e) {
      logger.debug("Error trying to send reply.", e);
      return "";
    }
  }

  /**
   * Send the document with the given id to the user.
   *
   * @param documentId the id of the document to send
   */
  public void exportDocument(int documentId) throws IOException {
    logger.debug("Export Document called for document id " + documentId);
    Document document = DocumentUtil.fetchDocumentById(documentId);
    if (document != null) {
      ByteArrayOutputStream bos = DocumentUtil.getDocumentOutputStreamById(documentId);
      Faces.sendFile(bos.toByteArray(), document.getFilename(), true);
    } else {
      logger.error("Document with id " + documentId + " not found");
    }
  }

  /**
   * Delete a document with the given elementId. The id is transmitted via http request parameter.
   */
  public void deleteDocument() {
    String documentIdString = Faces.getRequestParameter("elementId");
    try {
      int documentId = Integer.parseInt(documentIdString);
      DocumentUtil.deleteDocument(documentId);
      loadDocuments();
      logger.debug("Deleted document with id: " + documentId);
      Ajax.oncomplete(CREATE_EVENTHANDLERS);
    } catch (NumberFormatException e) {
      logger.warn("Could not parse expose id: " + documentIdString);
    }
  }

  /**
   * Handle the upload of a document from the client.
   *
   * @param event the ajax event associated with this listener
   */
  public void handleDocumentUpload(AjaxBehaviorEvent event) {
    logger.debug("file size: " + newDocument.getSize());
    logger.debug("file type: " + newDocument.getContentType());
    logger.debug("file info: " + newDocument.getHeader("Content-Disposition"));
    try {
      File documentFile = save(newDocument);
      Document document = new Document();
      Path path = documentFile.toPath();
      document.setData(Files.readAllBytes(path));
      document.setFilename(SamplyShareUtils.getFilenameFromContentDisposition(
          newDocument.getHeader(HttpHeaders.CONTENT_DISPOSITION)));
      document.setFiletype(newDocument.getContentType());
      document.setInquiryId(selectedInquiryId);
      document.setUserId(loginBean.getUser().getId());
      DocumentUtil.insertDocument(document);
      loadDocuments();
      if (!documentFile.delete()) {
        logger.error("Could not delete document file");
      }
      newDocument = null;
      Ajax.oncomplete(RESET_FILEINPUT, CREATE_EVENTHANDLERS);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Document upload failed.");
    }
  }

  /**
   * Save a file part received from the client.
   *
   * @param part the file part to save
   * @return the new file
   */
  private File save(Part part) throws IOException {
    return Utils.savePartToTmpFile("inquiry_doc", part);
  }

  /**
   * (Re-)load the documents for the selected inquiry from the database.
   */
  private void loadDocuments() {
    documents = DocumentUtil.getDocumentsForInquiry(selectedInquiryId);
  }

  /**
   * Check if result stats were written for the latest inquiry.
   *
   * @return true if stats are available, false otherwise
   */
  public boolean latestInquiryResultHasStats() {
    return latestInquiryResultStats != null;
  }

  public String getResultCountByIdGroupedByAge() {
    return latestInquiryResultStats.getStatsAge();
  }

  public String getResultCountByIdGroupedByGender() {
    return latestInquiryResultStats.getStatsGender();
  }

  /**
   * Reload the statistics.
   */
  public void reloadStatistics() {

    Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext()
        .getRequestParameterMap();
    String parameterStatsReady = requestParameterMap.get("statisticsForm:statsReadyCondition");
    Boolean oldStatsReady = Utils.getAsBoolean(parameterStatsReady);
    boolean newStatsReady = latestInquiryResultHasStats();

    if (oldStatsReady != null && oldStatsReady == false && newStatsReady == true) {
      reloadPage();
    }

  }

  private void reloadPage() {
    try {
      reloadPageWithoutExceptionManagement();
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void reloadPageWithoutExceptionManagement() throws IOException {

    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) ec.getRequest();
    String redirectUri = getRedirectUri(request);

    ec.redirect(redirectUri);

  }

  private String getRedirectUri(HttpServletRequest request) {

    //TODO: Please refactor me! I am so ugly :(
    StringBuffer stringBuffer = new StringBuffer(request.getRequestURI());
    stringBuffer.append('?');
    stringBuffer.append("inquiryId=");
    stringBuffer.append(selectedInquiryId);
    stringBuffer.append("&faces-redirect=true");

    return stringBuffer.toString();

  }


}
