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
 * One event to show in the event log.
 */
public class EventLogLine implements Serializable {

  private String eventTime;
  private String message;
  private EventLogEntityTypes referencedEntityType;
  private String referencedEntityId;
  private String userName;

  /**
   * Todo.
   *
   * @param eventLog Todo.
   */
  public EventLogLine(EventLog eventLog) {
    this.eventTime = SamplyShareUtils
        .convertSqlTimestampToString(eventLog.getEventTime(), "dd.MM.yyyy HH:mm:ss");
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
   * Assemble the message column for an event log entry.
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
        // Special case: Unknown Keys entry. Since JSF Messages don't support a variable amount of
        // parameters, join them to a comma-separated string
        switch (eventType) {
          case E_REPEAT_EXECUTE_INQUIRY_JOB_WITHOUT_UNKNOWN_KEYS:
            String concatKeys = Joiner.on(", ").join(entry.getParameters());
            messageColumn = Messages.getString(eventType.getLiteral(), concatKeys);
            break;
          case E_NEW_INQUIRY_RECEIVED:
            messageColumn = Messages.getString(eventType.getLiteral(),
                entry.getParameters().toArray(new Object[entry.getParameters().size()]))
                + ": \"" + InquiryUtil.fetchInquiryById(eventLog.getInquiryId()).getLabel() + "\"";
            break;
          default:
            messageColumn = Messages.getString(eventType.getLiteral(),
                entry.getParameters().toArray(new Object[entry.getParameters().size()]));
            break;
        }

      } else {
        messageColumn = Messages.getString(eventType.getLiteral());
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
