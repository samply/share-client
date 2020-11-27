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

package de.samply.share.client.rest;

import com.google.gson.Gson;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.*;
import de.samply.share.client.model.line.InquiryLine;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.SamplyShareUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Datasource for the datatables plugin on the inquiry related pages.
 */
@Path("/inquiries")
public class InquiriesDatasource {

    private static final Logger logger = LoggerFactory.getLogger(InquiriesDatasource.class);

    private List<InquiryLine> activeInquiryList;
    private List<InquiryLine> erroneousInquiryList;
    private List<InquiryLine> archivedInquiryList;

    @Path("/active")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveInquiries(@HeaderParam("userid") Integer userId) {
        loadActiveInquiryList(userId);
        Response response;

        StringBuilder stringBuilder = new StringBuilder();
        Gson gson = new Gson();
        String json = gson.toJson(activeInquiryList);

        stringBuilder.append("{\"data\": ");
        stringBuilder.append(json);
        stringBuilder.append("}");

        response = Response.ok(stringBuilder.toString()).build();
        return response;
    }

    @Path("/erroneous")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErroneousInquiries(@HeaderParam("userid") Integer userId) {
        loadErroneousInquiryList(userId);
        Response response;

        StringBuilder stringBuilder = new StringBuilder();
        Gson gson = new Gson();
        String json = gson.toJson(erroneousInquiryList);

        stringBuilder.append("{\"data\": ");
        stringBuilder.append(json);
        stringBuilder.append("}");

        response = Response.ok(stringBuilder.toString()).build();
        return response;
    }

    @Path("/archived")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedInquiries(@HeaderParam("userid") Integer userId) {
        loadArchivedInquiryList(userId);
        Response response;

        StringBuilder stringBuilder = new StringBuilder();
        Gson gson = new Gson();
        String json = gson.toJson(archivedInquiryList);

        stringBuilder.append("{\"data\": ");
        stringBuilder.append(json);
        stringBuilder.append("}");

        response = Response.ok(stringBuilder.toString()).build();
        return response;
    }

    /**
     * Load content of the active inquiries table from the database.
     */
    private void loadActiveInquiryList(int userId) {
        List<Inquiry> inquiryList = InquiryUtil.fetchInquiriesOrderByReceivedAt(InquiryStatusType.IS_NEW, InquiryStatusType.IS_PROCESSING, InquiryStatusType.IS_READY);
        activeInquiryList = populateInquiryLines(userId, inquiryList);
    }

    /**
     * Load content of the erroneous inquiries table from the database.
     */
    private void loadErroneousInquiryList(int userId) {
        List<Inquiry> inquiryList = InquiryUtil.fetchInquiriesOrderByReceivedAt(InquiryStatusType.IS_LDM_ERROR, InquiryStatusType.IS_ABANDONED);
        erroneousInquiryList = populateInquiryLines(userId, inquiryList);
    }

    /**
     * Load content of the archived inquiries table from the database.
     */
    private void loadArchivedInquiryList(int userId) {
        List<Inquiry> inquiryList = InquiryUtil.fetchInquiriesOrderByReceivedAt(InquiryStatusType.IS_ARCHIVED);
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
            InquiryDetails inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiry.getLatestDetailsId());
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

            inquiryLine.setSearchFor(InquiriesDatasource.getAbbreviatedLabelsFor(requestedEntities));

            inquiryLine.setReceivedAt(SamplyShareUtils.convertSqlTimestampToString(inquiryDetails.getReceivedAt(), "dd.MM.yyyy HH:mm"));

            if (inquiry.getArchivedAt() != null) {
                inquiryLine.setArchivedAt(SamplyShareUtils.convertSqlTimestampToString(inquiry.getArchivedAt(), "dd.MM.yyyy HH:mm"));
            } else {
                inquiryLine.setArchivedAt(null);
            }

            try {
                InquiryResult inquiryResult = InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId());
                if (inquiryResult == null) {
                    inquiryLine.setFound(Messages.getString("inqs_noResults"));
                    inquiryLine.setAsOf("-");
                } else {
                    if (inquiryResult.getIsError()) {
                        inquiryLine.setFound("Error");
                        inquiryLine.setErrorCode(SamplyShareUtils.isNullOrEmpty(inquiryResult.getErrorCode()) ? " " : inquiryResult.getErrorCode());
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
                    inquiryLine.setAsOf(SamplyShareUtils.convertSqlTimestampToString(inquiryResult.getExecutedAt(), "dd.MM.yyyy HH:mm"));
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
            stringBuilder.append("<span class='requested-entity-label requested-entity-label-abbr label label-default' title='");
            stringBuilder.append(Messages.getString(re.getName().getLiteral()));
            stringBuilder.append("'>");
            stringBuilder.append(Messages.getString(re.getName().getLiteral() + "_ABBR"));
            stringBuilder.append("</span>");
        }
        return stringBuilder.toString();
    }
}
