package de.samply.share.client.control;

import com.google.common.net.HttpHeaders;
import de.samply.dktk.converter.EnumValidationHandling;
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
import de.samply.share.client.rest.Connector;
import de.samply.share.client.util.Utils;
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
import de.samply.share.common.utils.PercentageLogger;
import de.samply.share.common.utils.QueryTreeUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Container;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.Result;
import de.samply.share.model.cql.CqlResult;
import de.samply.share.utils.Converter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewScoped backing bean, used for pages that deal with inquiries.
 */
@ManagedBean(name = "inquiryBean")
@ViewScoped
public class InquiryBean implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(InquiryBean.class);
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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

  /**
   * Init.
   */
  @PostConstruct
  public void init() {
    ldmConnector = ApplicationBean.getLdmConnector();
  }

  /**
   * Gets login bean.
   *
   * @return the login bean
   */
  public LoginBean getLoginBean() {
    return loginBean;
  }

  /**
   * Sets login bean.
   *
   * @param loginBean the login bean
   */
  public void setLoginBean(LoginBean loginBean) {
    this.loginBean = loginBean;
  }

  /**
   * Gets selected inquiry id.
   *
   * @return the selected inquiry id
   */
  public int getSelectedInquiryId() {
    return selectedInquiryId;
  }

  /**
   * Sets selected inquiry id.
   *
   * @param selectedInquiryId the selected inquiry id
   */
  public void setSelectedInquiryId(int selectedInquiryId) {
    this.selectedInquiryId = selectedInquiryId;
  }

  /**
   * Gets inquiry.
   *
   * @return the inquiry
   */
  public Inquiry getInquiry() {
    return inquiry;
  }

  /**
   * Sets inquiry.
   *
   * @param inquiry the inquiry
   */
  public void setInquiry(Inquiry inquiry) {
    this.inquiry = inquiry;
  }

  /**
   * Gets latest inquiry details.
   *
   * @return the latest inquiry details
   */
  public InquiryDetails getLatestInquiryDetails() {
    return latestInquiryDetails;
  }

  /**
   * Sets latest inquiry details.
   *
   * @param latestInquiryDetails the latest inquiry details
   */
  public void setLatestInquiryDetails(InquiryDetails latestInquiryDetails) {
    this.latestInquiryDetails = latestInquiryDetails;
  }

  /**
   * Gets latest inquiry result.
   *
   * @return the latest inquiry result
   */
  public InquiryResult getLatestInquiryResult() {
    return latestInquiryResult;
  }

  private static InquiryResult getLastInquiryResult(List<InquiryResult> inquiryResultsList) {
    return inquiryResultsList.stream().sorted(Comparator.comparing(InquiryResult::getExecutedAt))
        .collect(Collectors.toList()).get(inquiryResultsList.size() - 1);
  }

  /**
   * Sets latest inquiry result.
   *
   * @param latestInquiryResult the latest inquiry result
   */
  public void setLatestInquiryResult(InquiryResult latestInquiryResult) {
    this.latestInquiryResult = latestInquiryResult;
  }

  /**
   * Gets inquiry results list.
   *
   * @return the inquiry results list
   */
  public List<InquiryResult> getInquiryResultsList() {
    return inquiryResultsList;
  }

  /**
   * Sets inquiry results list.
   *
   * @param inquiryResultsList the inquiry results list
   */
  public void setInquiryResultsList(List<InquiryResult> inquiryResultsList) {
    this.inquiryResultsList = inquiryResultsList;
  }

  /**
   * Gets latest inquiry answer.
   *
   * @return the latest inquiry answer
   */
  public InquiryAnswer getLatestInquiryAnswer() {
    return latestInquiryAnswer;
  }

  /**
   * Sets latest inquiry answer.
   *
   * @param latestInquiryAnswer the latest inquiry answer
   */
  public void setLatestInquiryAnswer(InquiryAnswer latestInquiryAnswer) {
    this.latestInquiryAnswer = latestInquiryAnswer;
  }

  /**
   * Gets inquiry events.
   *
   * @return the inquiry events
   */
  public List<EventLog> getInquiryEvents() {
    return inquiryEvents;
  }

  /**
   * Sets inquiry events.
   *
   * @param inquiryEvents the inquiry events
   */
  public void setInquiryEvents(List<EventLog> inquiryEvents) {
    this.inquiryEvents = inquiryEvents;
  }

  /**
   * Is results ready boolean.
   *
   * @return the boolean
   */
  public boolean isResultsReady() {
    return resultsReady;
  }

  /**
   * Sets results ready.
   *
   * @param resultsReady the results ready
   */
  public void setResultsReady(boolean resultsReady) {
    this.resultsReady = resultsReady;
  }

  /**
   * Gets latest result statistics.
   *
   * @return the latest result statistics
   */
  public QueryResultStatistic getLatestResultStatistics() {
    return latestResultStatistics;
  }

  /**
   * Sets latest result statistics.
   *
   * @param latestResultStatistics the latest result statistics
   */
  public void setLatestResultStatistics(QueryResultStatistic latestResultStatistics) {
    this.latestResultStatistics = latestResultStatistics;
  }

  /**
   * Gets patient page tree.
   *
   * @return the patient page tree
   */
  public TreeModel<Container> getPatientPageTree() {
    return patientPageTree;
  }

  /**
   * Sets patient page tree.
   *
   * @param patientPageTree the patient page tree
   */
  public void setPatientPageTree(TreeModel<Container> patientPageTree) {
    this.patientPageTree = patientPageTree;
  }

  /**
   * Gets latest original criteria tree.
   *
   * @return the latest original criteria tree
   */
  public TreeModel<QueryItem> getLatestOriginalCriteriaTree() {
    return latestOriginalCriteriaTree;
  }

  /**
   * Sets latest original criteria tree.
   *
   * @param latestOriginalCriteriaTree the latest original criteria tree
   */
  public void setLatestOriginalCriteriaTree(TreeModel<QueryItem> latestOriginalCriteriaTree) {
    this.latestOriginalCriteriaTree = latestOriginalCriteriaTree;
  }

  /**
   * Gets requested entities label string.
   *
   * @return the requested entities label string
   */
  public String getRequestedEntitiesLabelString() {
    return requestedEntitiesLabelString;
  }

  /**
   * Sets requested entities label string.
   *
   * @param requestedEntitiesLabelString the requested entities label string
   */
  public void setRequestedEntitiesLabelString(String requestedEntitiesLabelString) {
    this.requestedEntitiesLabelString = requestedEntitiesLabelString;
  }

  /**
   * Gets selected inquiry contact.
   *
   * @return the selected inquiry contact
   */
  public Contact getSelectedInquiryContact() {
    return selectedInquiryContact;
  }

  /**
   * Sets selected inquiry contact.
   *
   * @param selectedInquiryContact the selected inquiry contact
   */
  public void setSelectedInquiryContact(Contact selectedInquiryContact) {
    this.selectedInquiryContact = selectedInquiryContact;
  }

  /**
   * Gets latest inquiry result stats.
   *
   * @return the latest inquiry result stats
   */
  public InquiryResultStats getLatestInquiryResultStats() {
    return latestInquiryResultStats;
  }

  /**
   * Sets latest inquiry result stats.
   *
   * @param latestInquiryResultStats the latest inquiry result stats
   */
  public void setLatestInquiryResultStats(InquiryResultStats latestInquiryResultStats) {
    this.latestInquiryResultStats = latestInquiryResultStats;
  }

  /**
   * Gets inquiry criteria.
   *
   * @return the inquiry criteria
   */
  public List<InquiryCriteria> getInquiryCriteria() {
    return inquiryCriteria;
  }

  /**
   * Sets inquiry criteria.
   *
   * @param inquiryCriteria the inquiry criteria
   */
  public void setInquiryCriteria(List<InquiryCriteria> inquiryCriteria) {
    this.inquiryCriteria = inquiryCriteria;
  }

  /**
   * Gets documents.
   *
   * @return the documents
   */
  public List<Document> getDocuments() {
    return documents;
  }

  /**
   * Sets documents.
   *
   * @param documents the documents
   */
  public void setDocuments(List<Document> documents) {
    this.documents = documents;
  }

  /**
   * Gets new document.
   *
   * @return the new document
   */
  public Part getNewDocument() {
    return newDocument;
  }

  /**
   * Sets new document.
   *
   * @param newDocument the new document
   */
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
      //TODO create criteriaTree with cql

      if (inquiryCriteria == null) {
        inquiryCriteria = new ArrayList<>();
      }
      if (ApplicationUtils.isLanguageQuery()) {
        this.inquiryCriteria.add(InquiryCriteriaUtil
            .getFirstCriteriaOriginal(latestInquiryDetails, QueryLanguageType.QL_QUERY));
        latestOriginalCriteriaTree = populateCriteriaTree(
            inquiryCriteria.get(0).getCriteriaOriginal());
      } else {
        this.inquiryCriteria = InquiryCriteriaUtil
            .getInquiryCriteriaForInquiryDetails(latestInquiryDetails);
      }

      List<RequestedEntity> requestedEntities = InquiryUtil.getRequestedEntitiesForInquiry(inquiry);
      requestedEntitiesLabelString = Connector.getLabelsFor(requestedEntities);
      selectedInquiryContact = ContactUtil.fetchContactById(latestInquiryDetails.getContactId());
      inquiryEvents = EventLogUtil.fetchEventLogForInquiryById(inquiry.getId());
      loadDocuments();
      if (!SamplyShareUtils.isNullOrEmpty(inquiryResultsList)) {
        latestInquiryResult = getLastInquiryResult(inquiryResultsList);
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
      logger.error(npe.getMessage());
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

  /**
   * Load the sublist results.
   * @return sublist results
   */
  public List<String> loadSubListUrls() {
    List<InquiryResult> inquiryResultList = InquiryResultUtil.fetchLastTwoInquiryResult(
        latestInquiryDetails.getId(), false);
    List<String> locations = new ArrayList<>();
    locations.add(inquiryResultList.get(0).getLocation());
    locations.add(inquiryResultList.get(1).getLocation());
    return locations;
  }

  /**
   * Send a reply back to the broker. Currently only supports the size. TODO: Add support for other
   * reply types TODO: Add success/error message
   *
   * @return the string
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
              logger.error(e.getMessage(), e);
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
   * @throws IOException the io exception
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
      logger.error(e.getMessage(), e);
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

  /**
   * Gets result count by id grouped by age.
   *
   * @return the result count by id grouped by age
   */
  public String getResultCountByIdGroupedByAge() {
    return latestInquiryResultStats.getStatsAge();
  }

  /**
   * Gets result count by id grouped by gender.
   *
   * @return the result count by id grouped by gender
   */
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
      logger.error(e.getMessage(), e);
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

  /**
   * Generates an export file.
   *
   * @param validationHandling validating handling
   */
  public void generateExportFile(EnumValidationHandling validationHandling) {

    try {
      generateExportFileWithoutManagementException(validationHandling);
    } catch (ExportFileGeneratorException | IOException e) {
      logger.error("Exception caught while trying to export data", e);
    }

  }

  private void generateExportFileWithoutManagementException(
      EnumValidationHandling validationHandling) throws ExportFileGeneratorException, IOException {

    Integer timeout = ConfigurationUtil
        .getConfigurationElementValueAsInteger(EnumConfiguration.EXPORT_TIMEOUT_IN_MINUTES);
    ExportFileGenerator exportFileGenerator =
        new ExportFileGenerator(latestInquiryResult, ldmConnector, inquiry, selectedInquiryContact,
            validationHandling, timeout);

    ByteArrayOutputStream bos = exportFileGenerator.generateExport();

    if (bos != null) {

      String filename =
          !(inquiry.getLabel().equals("")) ? inquiry.getLabel() + ".xlsx" : "Export.xlsx";

      Faces.sendFile(bos.toByteArray(), filename, true);

    }

  }

}
