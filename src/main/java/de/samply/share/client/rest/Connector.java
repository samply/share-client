package de.samply.share.client.rest;

import com.google.gson.Gson;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.control.InquiryHandlingBean;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.RequestedEntity;
import de.samply.share.client.model.line.EventLogLine;
import de.samply.share.client.model.line.InquiryLine;
import de.samply.share.client.util.connector.StoreConnector;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UserSeenInquiryUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/inquiries")
public class Connector {

  private static final Logger logger = LogManager.getLogger(InquiryHandlingBean.class);

  private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

  private static final String INQUIRIES_PROCESSING = "inqs_processing";
  private static final String INQUIRIES_NO_RESULTS = "inqs_noResults";
  private static final String INQUIRIES_NO_LABEL = "inqs_noLabel";
  private static final String INQUIRIES_NOT_AVAILABLE = "inqs_not_available";
  private static final String INQUIRIES_ABANDONED = "inqs_abandoned";
  private static final String INQUIRIES_ARCHIVED = "inqs_archived";
  private static final String INQUIRIES_NEW = "inqs_new";
  private static final String INQUIRIES_LDM_ERROR = "inqs_ldm_error";


  private static final String UNKNOWN = "unknown";
  private static final String ERROR = "error";
  private static final String EMPTY = "";


  private List<InquiryLine> activeInquiryList;
  private List<InquiryLine> erroneousInquiryList;
  private List<InquiryLine> archivedInquiryList;

  /**
   * Get the labels for the requested entities.
   *
   * @param requestedEntities the requested entities.
   * @return a list of labels
   */
  public static String getLabelsFor(List<RequestedEntity> requestedEntities) {
    if (SamplyShareUtils.isNullOrEmpty(requestedEntities)) {
      return EMPTY;
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (RequestedEntity re : requestedEntities) {
      stringBuilder.append("<span class='requested-entity-label label label-default title='");
      stringBuilder.append(Messages.getString(re.getName().getLiteral()));
      stringBuilder.append("'>");
      stringBuilder.append(Messages.getString(re.getName().getLiteral()));
      stringBuilder.append("</span>");
    }
    return stringBuilder.toString();
  }

  private static String getAbbreviatedLabelsFor(List<RequestedEntity> requestedEntities) {
    if (SamplyShareUtils.isNullOrEmpty(requestedEntities)) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (RequestedEntity re : requestedEntities) {
      stringBuilder.append(
          "<span class='requested-entity-label requested-entity-label-abbr label label-default' "
              + "title='");
      stringBuilder.append(Messages.getString(re.getName().getLiteral()));
      stringBuilder.append("'>");
      stringBuilder.append(Messages.getString(re.getName().getLiteral() + "_ABBR"));
      stringBuilder.append("</span>");
    }
    return stringBuilder.toString();
  }

  /**
   * Check if the logged in user has permission to see the active inquiries. If yes, then load the
   * active inquiries.
   *
   * @param userId           the user id
   * @param authStringBase64 basicAuth
   * @return UNAUTHORIZED or a list of active inquiries
   */
  @Path("/active")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses({
      @APIResponse(responseCode = "200", description = "A Json String of active inquiries",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON)),
      @APIResponse(responseCode = "401", description = "Unauthorized")
  })
  @Operation(summary = "Get the active inquiries")
  public Response getActiveInquiries(
      @HeaderParam("userid") Integer userId,
      @HeaderParam("Authorization") String authStringBase64) {
    if (!authorize(authStringBase64)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    loadActiveInquiryList(userId);
    return Response.ok(addDataBracket(activeInquiryList)).build();
  }

  /**
   * Check if the logged in user has permission to see the erroneous inquiries. If yes, then load
   * the erroneous inquiries.
   *
   * @param userId           the user id
   * @param authStringBase64 basicAuth
   * @return UNAUTHORIZED or a list of erroneous inquiries
   */
  @Path("/erroneous")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses({
      @APIResponse(responseCode = "200", description = "A Json String of erroneous inquiries",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON)),
      @APIResponse(responseCode = "401", description = "Unauthorized")
  })
  @Operation(summary = "Get the erroneous inquiries")
  public Response getErroneousInquiries(
      @HeaderParam("userid") Integer userId,
      @HeaderParam("Authorization") String authStringBase64) {
    if (!authorize(authStringBase64)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    loadErroneousInquiryList(userId);
    return Response.ok(addDataBracket(erroneousInquiryList)).build();
  }

  /**
   * Check if the logged in user has permission to see the archived inquiries. If yes, then load the
   * archived inquiries.
   *
   * @param userId           the user id
   * @param authStringBase64 basicAuth
   * @return UNAUTHORIZED or a list of archived inquiries
   */
  @Path("/archived")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses({
      @APIResponse(responseCode = "200", description = "A Json String of archived inquiries",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON)),
      @APIResponse(responseCode = "401", description = "Unauthorized")
  })
  @Operation(summary = "Get the archived inquiries")
  public Response getArchivedInquiries(
      @HeaderParam("userid") Integer userId,
      @HeaderParam("Authorization") String authStringBase64) {
    logger.debug("getting archived inquiries");
    logger.debug("authorizing");
    if (!authorize(authStringBase64)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    logger.debug("loading archived inquiry list");
    loadArchivedInquiryList(userId);
    logger.debug("return archived inquiry list");
    return Response.ok(addDataBracket(archivedInquiryList)).build();
  }

  /**
   * Check if the logged in user has permission to see the log events. If yes, then load the log
   * events.
   *
   * @param authStringBase64 basicAuth
   * @return UNAUTHORIZED or a list of log events.
   */
  @Path("/log")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses({
      @APIResponse(responseCode = "200", description = "A Json String of logs",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON)),
      @APIResponse(responseCode = "401", description = "Unauthorized")
  })
  @Operation(summary = "Get the event logs")
  public Response getLog(@HeaderParam("Authorization") String authStringBase64) {
    if (!authorize(authStringBase64)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    return Response.ok(addDataBracket(loadEventLogList())).build();
  }

  private boolean authorize(String base64) {
    if (!StringUtils.startsWith(base64, "Basic")) {
      //logger.debug ("auth1"+ base64);
      return false;
    }
    String base64Credentials = base64.substring("Basic".length()).trim();
    String credentials =
        new String(
            org.apache.commons.codec.binary.Base64.decodeBase64(base64Credentials),
            StandardCharsets.UTF_8);
    final String[] values = credentials.split(":", 2);
    if (values.length != 2) {
      //logger.debug ("auth2");
      return false;
    }
    if (!StringUtils.equals(StoreConnector.authorizedUsername, values[0])) {
      //logger.debug ("auth3");
      return false;
    }
    //logger.debug ("auth4");
    return StringUtils.equals(StoreConnector.authorizedPassword, values[1]);
    //logger.debug ("auth5");
  }

  private String addDataBracket(Object object) {
    StringBuilder stringBuilder = new StringBuilder();
    Gson gson = new Gson();
    String json = gson.toJson(object);
    stringBuilder.append("{\"data\": ");
    stringBuilder.append(json);
    stringBuilder.append("}");

    return stringBuilder.toString();
  }

  /**
   * Load content of the active inquiries table from the database.
   */
  private void loadActiveInquiryList(int userId) {
    List<Inquiry> inquiryList =
        InquiryUtil.fetchInquiriesOrderByReceivedAt(
            InquiryStatusType.IS_NEW, InquiryStatusType.IS_PROCESSING, InquiryStatusType.IS_READY);
    activeInquiryList = populateInquiryLines(userId, inquiryList);
  }

  /**
   * Load content of the erroneous inquiries table from the database.
   */
  private void loadErroneousInquiryList(int userId) {
    List<Inquiry> inquiryList =
        InquiryUtil.fetchInquiriesOrderByReceivedAt(
            InquiryStatusType.IS_LDM_ERROR, InquiryStatusType.IS_ABANDONED);
    erroneousInquiryList = populateInquiryLines(userId, inquiryList);
  }

  /**
   * Load content of the archived inquiries table from the database.
   */
  private void loadArchivedInquiryList(int userId) {
    List<Inquiry> inquiryList =
        InquiryUtil.fetchInquiriesOrderByReceivedAt(InquiryStatusType.IS_ARCHIVED);
    archivedInquiryList = populateInquiryLines(userId, inquiryList);
  }

  /**
   * Populate inquiry lines.
   *
   * @param inquiries the inquiries
   */
  private List<InquiryLine> populateInquiryLines(Integer userId, List<Inquiry> inquiries) {

    List<InquiryLine> targetList = new ArrayList<>();

    for (Inquiry inquiry : inquiries) {
      // First, load related entries
      InquiryDetails inquiryDetails = fetchInquiryDetails(inquiry);
      InquiryLine inquiryLine = fetchInquiryLine(userId, inquiry, inquiryDetails);
      targetList.add(inquiryLine);

    }

    return targetList;

  }

  private InquiryDetails fetchInquiryDetails(Inquiry inquiry) {
    return InquiryDetailsUtil.fetchInquiryDetailsById(inquiry.getLatestDetailsId());
  }

  private InquiryLine fetchInquiryLine(Integer userId, Inquiry inquiry,
      InquiryDetails inquiryDetails) {

    InquiryLine inquiryLine = new InquiryLine();

    inquiryLine.setId(inquiry.getId());

    inquiryLine = setSeen(userId, inquiry, inquiryLine);
    inquiryLine = setSearchFor(inquiry, inquiryLine);
    inquiryLine = setName(inquiry, inquiryLine);
    inquiryLine = setReceivedAt(inquiryLine, inquiryDetails);
    inquiryLine = setArchivedAt(inquiry, inquiryLine);
    inquiryLine = setBrokerName(inquiry, inquiryLine);
    inquiryLine = fetchLatestInquiryResultForInquiryDetailsById(inquiryLine, inquiryDetails);

    return inquiryLine;

  }

  private InquiryLine setSeen(Integer userId, Inquiry inquiry, InquiryLine inquiryLine) {

    boolean seen =
        userId != null && UserSeenInquiryUtil.hasUserSeenInquiryByIds(userId, inquiry.getId());
    inquiryLine.setSeen(seen);

    return inquiryLine;

  }

  private InquiryLine setSearchFor(Inquiry inquiry, InquiryLine inquiryLine) {

    List<RequestedEntity> requestedEntities = InquiryUtil.getRequestedEntitiesForInquiry(inquiry);
    inquiryLine.setSearchFor(getAbbreviatedLabelsFor(requestedEntities));

    return inquiryLine;

  }

  private InquiryLine setName(Inquiry inquiry, InquiryLine inquiryLine) {

    if (SamplyShareUtils.isNullOrEmpty(inquiry.getLabel())) {
      inquiryLine.setName(Messages.getString(INQUIRIES_NO_LABEL));
    } else {
      inquiryLine.setName(inquiry.getLabel());
    }

    return inquiryLine;

  }

  private InquiryLine setReceivedAt(InquiryLine inquiryLine, InquiryDetails inquiryDetails) {

    inquiryLine.setReceivedAt(
        SamplyShareUtils.convertSqlTimestampToString(inquiryDetails.getReceivedAt(), DATE_FORMAT));
    return inquiryLine;

  }

  private InquiryLine setAsOf(InquiryLine inquiryLine, InquiryResult inquiryResult) {

    inquiryLine.setAsOf(
        SamplyShareUtils.convertSqlTimestampToString(inquiryResult.getExecutedAt(), DATE_FORMAT));
    return inquiryLine;

  }

  private InquiryLine setArchivedAt(Inquiry inquiry, InquiryLine inquiryLine) {

    if (inquiry.getArchivedAt() != null) {
      inquiryLine.setArchivedAt(
          SamplyShareUtils.convertSqlTimestampToString(
              inquiry.getArchivedAt(), DATE_FORMAT));
    } else {
      inquiryLine.setArchivedAt(null);
    }

    return inquiryLine;

  }

  private InquiryLine fetchLatestInquiryResultForInquiryDetailsById(InquiryLine inquiryLine,
      InquiryDetails inquiryDetails) {

    try {

      return fetchLatestInquiryResultForInquiryDetailsById_WithoutManagementException(inquiryLine,
          inquiryDetails);

    } catch (Exception e) {

      logger.warn("Exception caught while trying to populate inquiry lines", e);
      inquiryLine.setAsOf(EMPTY);
      inquiryLine.setFound(ERROR);
      inquiryLine.setErrorCode(EMPTY);

      return inquiryLine;

    }

  }

  private InquiryLine fetchLatestInquiryResultForInquiryDetailsById(InquiryLine inquiryLine,
      InquiryDetails inquiryDetails, InquiryResult inquiryResult) {

    String found = EMPTY;
    String errorCode = EMPTY;

    if (inquiryResult.getIsError()) {

      found = ERROR;

      if (!SamplyShareUtils.isNullOrEmpty(inquiryResult.getErrorCode())) {
        errorCode = inquiryResult.getErrorCode();
      }

    } else {

      switch (inquiryDetails.getStatus()) {
        case IS_NEW:
          errorCode = Messages.getString(INQUIRIES_NEW);
          break;
        case IS_PROCESSING:
          found = getSizeOfInquiryResult(inquiryResult, INQUIRIES_PROCESSING);
          break;
        case IS_READY:
          found = getSizeOfInquiryResult(inquiryResult, INQUIRIES_NOT_AVAILABLE);
          break;
        case IS_ABANDONED:
          errorCode = Messages.getString(INQUIRIES_ABANDONED);
          break;
        case IS_LDM_ERROR:
          errorCode = Messages.getString(INQUIRIES_LDM_ERROR);
          break;
        case IS_ARCHIVED:
          errorCode = Messages.getString(INQUIRIES_ARCHIVED);
          break;
        default:
          break;
      }

    }

    inquiryLine = setFound(inquiryLine, found);
    inquiryLine = setErrorCode(inquiryLine, errorCode);

    return inquiryLine;

  }

  private InquiryLine fetchLatestInquiryResultForInquiryDetailsById_WithoutManagementException(
      InquiryLine inquiryLine, InquiryDetails inquiryDetails) {
    List<InquiryResult> inquiryResultList = new ArrayList<>();
    if (ApplicationUtils.isLanguageCql()) {
      inquiryResultList = InquiryResultUtil.fetchLastTwoInquiryResult(inquiryDetails.getId());
    } else if (ApplicationUtils.isLanguageQuery()) {
      inquiryResultList.add(
          InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId()));
    }
    StringBuilder result = new StringBuilder();
    for (InquiryResult inquiryResult : inquiryResultList) {
      if (inquiryResult == null) {
        inquiryLine = addNoResultsToInquiryLine(inquiryLine);
      } else {
        inquiryLine = fetchLatestInquiryResultForInquiryDetailsById(inquiryLine, inquiryDetails,
            inquiryResult);
        inquiryLine = setAsOf(inquiryLine, inquiryResult);
        if (!result.toString().equals(inquiryLine.getFound())) {
          result.append(inquiryLine.getFound());
        }
      }
    }
    inquiryLine.setFound(result.toString());
    return inquiryLine;

  }

  private InquiryLine addNoResultsToInquiryLine(InquiryLine inquiryLine) {

    inquiryLine.setFound(Messages.getString(INQUIRIES_NO_RESULTS));
    inquiryLine.setAsOf("-");

    return inquiryLine;

  }

  private String getSizeOfInquiryResult(InquiryResult inquiryResult, String defaultErrorMessage) {

    try {
      if (ApplicationUtils.isLanguageCql()) {
        String entityType = InquiryCriteriaUtil.fetchById(inquiryResult.getInquiryCriteriaId())
            .getEntityType();
        return entityType + ": " + inquiryResult.getSize() + (" ");
      }
      return inquiryResult.getSize().toString();

    } catch (Exception e) {
      return Messages.getString(defaultErrorMessage);
    }

  }

  private InquiryLine setFound(InquiryLine inquiryLine, String found) {

    inquiryLine.setFound(found);
    return inquiryLine;

  }

  private InquiryLine setErrorCode(InquiryLine inquiryLine, String errorCode) {

    inquiryLine.setErrorCode(errorCode);
    return inquiryLine;

  }

  private InquiryLine setBrokerName(Inquiry inquiry, InquiryLine inquiryLine) {

    String brokerName = getBrokerName(inquiry);
    inquiryLine.setBrokerName(brokerName);

    return inquiryLine;

  }

  private String getBrokerName(Inquiry inquiry) {

    try {

      Broker broker = BrokerUtil.fetchBrokerById(inquiry.getBrokerId());
      String brokerName = broker.getName();

      if (SamplyShareUtils.isNullOrEmpty(brokerName)) {
        brokerName = broker.getAddress();
      }

      return brokerName;

    } catch (Exception e) {
      return UNKNOWN;
    }

  }

  private List<EventLogLine> loadEventLogList() {
    List<EventLogLine> eventLogLines = new ArrayList<>();
    List<EventLog> eventLogs = EventLogUtil.fetchEventLogGlobal();
    for (EventLog el : eventLogs) {
      eventLogLines.add(new EventLogLine(el));
    }
    return eventLogLines;
  }
}
