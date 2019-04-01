package de.samply.share.client.rest;

import com.google.gson.Gson;
import de.samply.share.client.control.InquiryHandlingBean;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.*;
import de.samply.share.client.model.line.EventLogLine;
import de.samply.share.client.model.line.InquiryLine;
import de.samply.share.client.util.connector.StoreConnector;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Path("/inquiries")
public class Connector {

    private static final Logger logger = LogManager.getLogger(InquiryHandlingBean.class);

    private List<InquiryLine> activeInquiryList;
    private List<InquiryLine> erroneousInquiryList;
    private List<InquiryLine> archivedInquiryList;

    @Path("/active")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveInquiries(
            @HeaderParam("userid") Integer userId,
            @HeaderParam("Authorization") String authStringBase64) {
        if (!authorize(authStringBase64)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        loadActiveInquiryList(userId);
        return Response.ok(addDataBracket(activeInquiryList)).build();
    }

    @Path("/erroneous")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErroneousInquiries(
            @HeaderParam("userid") Integer userId,
            @HeaderParam("Authorization") String authStringBase64) {
        if (!authorize(authStringBase64)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        loadErroneousInquiryList(userId);
        return Response.ok(addDataBracket(erroneousInquiryList)).build();
    }

    @Path("/archived")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedInquiries(
            @HeaderParam("userid") Integer userId,
            @HeaderParam("Authorization") String authStringBase64) {
        if (!authorize(authStringBase64)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        loadArchivedInquiryList(userId);
        return Response.ok(addDataBracket(archivedInquiryList)).build();
    }

    @Path("/log")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLog(@HeaderParam("Authorization") String authStringBase64) {
        if (!authorize(authStringBase64)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(addDataBracket(loadEventLogList())).build();
    }

    private boolean authorize(String base64) {
        if (!StringUtils.startsWith(base64, "Basic")) {
            return false;
        }
        String base64Credentials = base64.substring("Basic".length()).trim();
        String credentials =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(base64Credentials),
                        Charset.forName("UTF-8"));
        final String[] values = credentials.split(":", 2);
        if (values.length != 2) {
            return false;
        }
        if (!StringUtils.equals(StoreConnector.authorizedUsername, values[0])) {
            return false;
        }
        if (!StringUtils.equals(StoreConnector.authorizedPassword, values[1])) {
            return false;
        }
        return true;
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

    private InquiryDetails fetchInquiryDetails(Inquiry inquiry){
        return InquiryDetailsUtil.fetchInquiryDetailsById(inquiry.getLatestDetailsId());
    }

    private InquiryLine fetchInquiryLine(Integer userId, Inquiry inquiry, InquiryDetails inquiryDetails){

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

    private InquiryLine setSeen (Integer userId, Inquiry inquiry, InquiryLine inquiryLine){

        boolean seen = (userId != null) ? UserSeenInquiryUtil.hasUserSeenInquiryByIds(userId, inquiry.getId()) : false;
        inquiryLine.setSeen(seen);

        return inquiryLine;

    }

    private InquiryLine setSearchFor (Inquiry inquiry, InquiryLine inquiryLine){

        List<RequestedEntity> requestedEntities = InquiryUtil.getRequestedEntitiesForInquiry(inquiry);
        inquiryLine.setSearchFor(getAbbreviatedLabelsFor(requestedEntities));

        return inquiryLine;

    }

    private InquiryLine setName (Inquiry inquiry, InquiryLine inquiryLine){

        if (SamplyShareUtils.isNullOrEmpty(inquiry.getLabel())) {
            inquiryLine.setName(Messages.getString("inqs_noLabel"));
        } else {
            inquiryLine.setName(inquiry.getLabel());
        }

        return inquiryLine;

    }

    private InquiryLine setReceivedAt (InquiryLine inquiryLine, InquiryDetails inquiryDetails){

        inquiryLine.setReceivedAt(SamplyShareUtils.convertSqlTimestampToString(inquiryDetails.getReceivedAt(), "dd.MM.yyyy HH:mm"));
        return inquiryLine;

    }

    private InquiryLine setAsOf (InquiryLine inquiryLine, InquiryResult inquiryResult){

        inquiryLine.setAsOf(SamplyShareUtils.convertSqlTimestampToString(inquiryResult.getExecutedAt(), "dd.MM.yyyy HH:mm"));
        return inquiryLine;

    }

    private InquiryLine setArchivedAt (Inquiry inquiry, InquiryLine inquiryLine){

        if (inquiry.getArchivedAt() != null) {
            inquiryLine.setArchivedAt(
                    SamplyShareUtils.convertSqlTimestampToString(
                            inquiry.getArchivedAt(), "dd.MM.yyyy HH:mm"));
        } else {
            inquiryLine.setArchivedAt(null);
        }

        return inquiryLine;

    }

    private InquiryLine fetchLatestInquiryResultForInquiryDetailsById(InquiryLine inquiryLine, InquiryDetails inquiryDetails){

        try{

            return fetchLatestInquiryResultForInquiryDetailsById_WithoutManagementException(inquiryLine, inquiryDetails);

        } catch (Exception e) {

            logger.warn("Exception caught while trying to populate inquiry lines", e);
            inquiryLine.setAsOf("");
            inquiryLine.setFound("Error");
            inquiryLine.setErrorCode("");

            return inquiryLine;

        }

    }

    private InquiryLine fetchLatestInquiryResultForInquiryDetailsById_WithoutManagementException(InquiryLine inquiryLine, InquiryDetails inquiryDetails){

            InquiryResult inquiryResult = InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId());

            if  (inquiryResult == null) {
                inquiryLine = addNoResultsToInquiryLine(inquiryLine);
            } else {
                inquiryLine = fetchLatestInquiryResultForInquiryDetailsById (inquiryLine, inquiryDetails, inquiryResult);
                inquiryLine = setAsOf(inquiryLine, inquiryResult);
            }

            return inquiryLine;

    }

    private InquiryLine addNoResultsToInquiryLine (InquiryLine inquiryLine){

        inquiryLine.setFound(Messages.getString("inqs_noResults"));
        inquiryLine.setAsOf("-");

        return inquiryLine;

    }

    private InquiryLine fetchLatestInquiryResultForInquiryDetailsById (InquiryLine inquiryLine, InquiryDetails inquiryDetails, InquiryResult inquiryResult){

        String found = "";
        String errorCode = "";

        if (inquiryResult.getIsError()) {

            found = "Error";

            if (!SamplyShareUtils.isNullOrEmpty(inquiryResult.getErrorCode())) {
                errorCode = inquiryResult.getErrorCode();
            }

        } else if (inquiryDetails.getStatus() == InquiryStatusType.IS_READY) {

            found = getSizeOfInquiryResult(inquiryResult, "inqs_ready");

        } else if (inquiryDetails.getStatus() == InquiryStatusType.IS_PROCESSING) {

            found = getSizeOfInquiryResult(inquiryResult,"inqs_processing" );
            errorCode = Messages.getString("");

        } else if (inquiryDetails.getStatus() == InquiryStatusType.IS_ABANDONED) {

            errorCode = Messages.getString("inqs_abandoned");

        }

        inquiryLine = setFound(inquiryLine, found);
        inquiryLine = setErrorCode(inquiryLine, errorCode);

        return inquiryLine;

    }

    private String getSizeOfInquiryResult (InquiryResult inquiryResult, String defaultErrorMessage){

        try {
            return inquiryResult.getSize().toString();

        } catch (Exception e) {
            return Messages.getString("inqs_processing");
        }

    }

    private InquiryLine setFound (InquiryLine inquiryLine, String found){

        inquiryLine.setFound(found);
        return inquiryLine;

    }

    private InquiryLine setErrorCode (InquiryLine inquiryLine, String errorCode){

        inquiryLine.setErrorCode(errorCode);
        return inquiryLine;

    }

    private InquiryLine setBrokerName (Inquiry inquiry, InquiryLine inquiryLine){

        String brokerName = getBrokerName(inquiry);
        inquiryLine.setBrokerName(brokerName);

        return inquiryLine;

    }


    private String getBrokerName (Inquiry inquiry){

        try {

            Broker broker = BrokerUtil.fetchBrokerById(inquiry.getBrokerId());
            String brokerName = broker.getName();

            if (SamplyShareUtils.isNullOrEmpty(brokerName)) {
                brokerName = broker.getAddress();
            }

            return brokerName;

        } catch (Exception e) {
            return "unknown";
        }

    }

    public static String getLabelsFor(List<RequestedEntity> requestedEntities) {
        if (SamplyShareUtils.isNullOrEmpty(requestedEntities)) {
            return "";
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
                    "<span class='requested-entity-label requested-entity-label-abbr label label-default' title='");
            stringBuilder.append(Messages.getString(re.getName().getLiteral()));
            stringBuilder.append("'>");
            stringBuilder.append(Messages.getString(re.getName().getLiteral() + "_ABBR"));
            stringBuilder.append("</span>");
        }
        return stringBuilder.toString();
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
