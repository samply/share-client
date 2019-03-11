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
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
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
        InquiryLine inquiryLine;

        for (Inquiry inquiry : inquiries) {
            // First, load related entries
            InquiryDetails inquiryDetails =
                    InquiryDetailsUtil.fetchInquiryDetailsById(inquiry.getLatestDetailsId());
            List<RequestedEntity> requestedEntities = InquiryUtil.getRequestedEntitiesForInquiry(inquiry);

            boolean seen = false;
            if (userId != null) {
                seen = UserSeenInquiryUtil.hasUserSeenInquiryByIds(userId, inquiry.getId());
            }

            inquiryLine = new InquiryLine();
            inquiryLine.setId(inquiry.getId());
            inquiryLine.setSeen(seen);

            if (SamplyShareUtils.isNullOrEmpty(inquiry.getLabel())) {
                inquiryLine.setName(Messages.getString("inqs_noLabel"));
            } else {
                inquiryLine.setName(inquiry.getLabel());
            }

            inquiryLine.setSearchFor(getAbbreviatedLabelsFor(requestedEntities));

            inquiryLine.setReceivedAt(
                    SamplyShareUtils.convertSqlTimestampToString(
                            inquiryDetails.getReceivedAt(), "dd.MM.yyyy HH:mm"));

            if (inquiry.getArchivedAt() != null) {
                inquiryLine.setArchivedAt(
                        SamplyShareUtils.convertSqlTimestampToString(
                                inquiry.getArchivedAt(), "dd.MM.yyyy HH:mm"));
            } else {
                inquiryLine.setArchivedAt(null);
            }

            try {
                InquiryResult inquiryResult =
                        InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId());
                if (inquiryResult == null) {
                    inquiryLine.setFound(Messages.getString("inqs_noResults"));
                    inquiryLine.setAsOf("-");
                } else {
                    if (inquiryResult.getIsError()) {
                        inquiryLine.setFound("Error");
                        inquiryLine.setErrorCode(
                                SamplyShareUtils.isNullOrEmpty(inquiryResult.getErrorCode())
                                        ? " "
                                        : inquiryResult.getErrorCode());
                    } else if (inquiryDetails.getStatus() == InquiryStatusType.IS_READY) {
                        inquiryLine.setFound(inquiryResult.getSize().toString());
                        inquiryLine.setErrorCode("");
                    } else if (inquiryDetails.getStatus() == InquiryStatusType.IS_PROCESSING) {
                        try {
                            inquiryLine.setFound(inquiryResult.getSize().toString());
                        } catch (Exception e) {
                            inquiryLine.setFound(Messages.getString("inqs_processing"));
                        }
                        inquiryLine.setErrorCode(Messages.getString(""));
                    } else if (inquiryDetails.getStatus() == InquiryStatusType.IS_ABANDONED) {
                        inquiryLine.setFound("");
                        inquiryLine.setErrorCode(Messages.getString("inqs_abandoned"));
                    }
                    inquiryLine.setAsOf(
                            SamplyShareUtils.convertSqlTimestampToString(
                                    inquiryResult.getExecutedAt(), "dd.MM.yyyy HH:mm"));
                }

            } catch (Exception e) {
                logger.warn("Exception caught while trying to populate inquiry lines", e);
                inquiryLine.setAsOf("");
                inquiryLine.setFound("Error");
                inquiryLine.setErrorCode("");
            }

            String brokerName;
            try {
                Broker broker = BrokerUtil.fetchBrokerById(inquiry.getBrokerId());
                brokerName = broker.getName();
                if (SamplyShareUtils.isNullOrEmpty(brokerName)) {
                    brokerName = broker.getAddress();
                }
            } catch (Exception e) {
                brokerName = "unknown";
            }
            inquiryLine.setBrokerName(brokerName);
            targetList.add(inquiryLine);
        }
        return targetList;
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
