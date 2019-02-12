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

package de.samply.share.client.model.line;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.EventLogEntityTypes;
import de.samply.share.client.model.EventLogEntry;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UserUtil;
import de.samply.share.common.utils.SamplyShareUtils;

import java.io.Serializable;

/**
 * One event to show in the event log
 */
public class EventLogLine implements Serializable {

    private String eventTime;
    private String message;
    private EventLogEntityTypes referencedEntityType;
    private String referencedEntityId;
    private String userName;

    public EventLogLine(EventLog eventLog) {
        this.eventTime = SamplyShareUtils.convertSqlTimestampToString(eventLog.getEventTime(), "dd.MM.yyyy HH:mm:ss");
        this.message = createMessageColumn(eventLog);

        try {
            this.userName = UserUtil.fetchUserById(eventLog.getUserId()).getUsername();
        } catch (Exception e) {
            this.userName = "";
        }
        if (eventLog.getInquiryId() != null) {
            this.referencedEntityType = EventLogEntityTypes.ELET_INQUIRY;
            this.referencedEntityId = Integer.toString(eventLog.getInquiryId());
        } else if (eventLog.getUploadId() != null) {
            this.referencedEntityType = EventLogEntityTypes.ELET_UPLOAD;
            this.referencedEntityId = Integer.toString(eventLog.getUploadId());
        } else if (eventLog.getQualityReportId() != null) {
            this.referencedEntityType = EventLogEntityTypes.ELET_QUALITY_REPORT;
            this.referencedEntityId = Integer.toString(eventLog.getQualityReportId());
        } else {
            this.referencedEntityType = EventLogEntityTypes.ELET_GENERIC;
        }
    }

    /**
     * Assemble the message column for an event log entry
     *
     * @param eventLog the event log entry
     * @return the message column
     */
    public String createMessageColumn(EventLog eventLog) {
        String messageColumn;
        EventMessageType eventType = eventLog.getEventType();
        if (eventType != null) {
            if (!SamplyShareUtils.isNullOrEmpty(eventLog.getEntry())) {

                Gson gson = new Gson();
                EventLogEntry entry = gson.fromJson(eventLog.getEntry(), EventLogEntry.class);
                // Special case: Unknown Keys entry. Since JSF Messages don't support a variable amount of parameters,
                // join them to a comma-separated string
                switch (eventType) {
                    case E_REPEAT_EXECUTE_INQUIRY_JOB_WITHOUT_UNKNOWN_KEYS:
                        String concatKeys = Joiner.on(", ").join(entry.getParameters());
                        messageColumn =  Messages.getString(eventType.getLiteral(), concatKeys);
                        break;
                    case E_NEW_INQUIRY_RECEIVED:
                        messageColumn = Messages.getString(eventType.getLiteral(),
                                entry.getParameters().toArray(new Object[entry.getParameters().size()]))
                                + ": \"" + InquiryUtil.fetchInquiryById(eventLog.getInquiryId()).getLabel() + "\"";
                        break;
                    default:
                        messageColumn =  Messages.getString(eventType.getLiteral(),
                                entry.getParameters().toArray(new Object[entry.getParameters().size()]));
                        break;
                }

            } else {
                messageColumn =  Messages.getString(eventType.getLiteral());
            }

        } else {
            messageColumn = eventLog.getEntry();
        }
        return messageColumn;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventLogEntityTypes getReferencedEntityType() {
        return referencedEntityType;
    }

    public void setReferencedEntityType(EventLogEntityTypes referencedEntityType) {
        this.referencedEntityType = referencedEntityType;
    }

    public String getReferencedEntityId() {
        return referencedEntityId;
    }

    public void setReferencedEntityId(String referencedEntityId) {
        this.referencedEntityId = referencedEntityId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
