package de.samply.share.client.util.db;

import com.google.gson.Gson;
import de.samply.share.client.model.EventLogEntry;
import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.daos.EventLogDao;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.common.utils.SamplyShareUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with event log objects.
 */
public class EventLogUtil {

  private static final Logger logger = LogManager.getLogger(EventLogUtil.class);

  private static final EventLogDao eventLogDao;

  static {
    eventLogDao = new EventLogDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private EventLogUtil() {

  }

  /**
   * Get the event log DAO.
   *
   * @return the event log DAO
   */
  public static EventLogDao getEventLogDao() {
    return eventLogDao;
  }

  /**
   * Insert a new event log entry into the database.
   *
   * @param eventLog the new event log entry to insert
   */
  public static void insertEventLog(EventLog eventLog) {
    eventLogDao.insert(eventLog);
  }

  /**
   * Insert a new event log entry into the database.
   *
   * @param messageType pre-defined event type
   * @param params      parameters that will be substituted via resource bundle and messageformat
   */
  public static void insertEventLogEntry(EventMessageType messageType, String... params) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(
        createJsonEventLogEntry(null, null, null,
            new ArrayList<>(Arrays.asList(params))));
    eventLog.setEventType(messageType);
    insertEventLog(eventLog);
  }

  /**
   * Insert a new message as event log entry into the database.
   *
   * @param message the message of the new event log entry to insert
   */
  public static void insertEventLogEntry(String message) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(createJsonEventLogEntry(message, null, null, null));
    insertEventLog(eventLog);
  }

  /**
   * Insert a new event log entry into the database and link it with an inquiry.
   *
   * @param messageType pre-defined event type
   * @param inquiryId   the id of the inquiry to link the event log entry with
   * @param params      parameters that will be substituted via resource bundle and messageformat
   */
  public static void insertEventLogEntryForInquiryId(EventMessageType messageType, int inquiryId,
      String... params) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(
        createJsonEventLogEntry(null, null, null,
            new ArrayList<>(Arrays.asList(params))));
    eventLog.setInquiryId(inquiryId);
    eventLog.setEventType(messageType);
    // For now, just show when new inquiries were downloaded in global log
    eventLog.setShowInGlobal(messageType == EventMessageType.E_NEW_INQUIRY_RECEIVED);
    insertEventLog(eventLog);
  }

  /**
   * Insert a new event log entry into the database and link it with an inquiry.
   *
   * @param message   the new message to insert
   * @param inquiryId the id of the inquiry to link the event log entry with
   */
  public static void insertEventLogEntryForInquiryId(String message, int inquiryId) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(createJsonEventLogEntry(message, null, null, null));
    eventLog.setInquiryId(inquiryId);
    eventLog.setShowInGlobal(false);
    insertEventLog(eventLog);
  }

  /**
   * Insert a new event log entry into the database and link it with an upload.
   *
   * @param messageType pre-defined event type
   * @param uploadId    the id of the upload to link the event log entry with
   * @param params      parameters that will be substituted via resource bundle and messageformat
   */
  public static void insertEventLogEntryForUploadId(EventMessageType messageType, int uploadId,
      String... params) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(
        createJsonEventLogEntry(null, null, null,
            new ArrayList<>(Arrays.asList(params))));
    eventLog.setEventType(messageType);
    eventLog.setUploadId(uploadId);
    insertEventLog(eventLog);
  }

  /**
   * Insert a new event log entry into the database and link it with an upload.
   *
   * @param message  the new message to insert
   * @param uploadId the id of the upload to link the event log entry with
   */
  public static void insertEventLogEntryForUploadId(String message, int uploadId) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(createJsonEventLogEntry(message, null, null, null));
    eventLog.setUploadId(uploadId);
    insertEventLog(eventLog);
  }

  /**
   * Get all event log entries linked with an inquiry.
   *
   * @param inquiryId the id of the inquiry for which the event log entries are wanted
   * @return the list of event log entries
   */
  public static List<EventLog> fetchEventLogForInquiryById(int inquiryId) {
    DSLContext dslContext = ResourceManager.getDslContext();

    return dslContext
        .selectFrom(Tables.EVENT_LOG)
        .where(Tables.EVENT_LOG.INQUIRY_ID.equal(inquiryId))
        .orderBy(Tables.EVENT_LOG.EVENT_TIME.desc())
        .fetchInto(EventLog.class);
  }

  /**
   * Get all event log entries linked with an upload.
   *
   * @param uploadId the id of the upload for which the event log entries are wanted
   * @return the list of event log entries
   */
  public static List<EventLog> fetchEventLogForUploadById(int uploadId) {
    return eventLogDao.fetchByUploadId(uploadId);
  }

  /**
   * Get all event log entries linked with an user.
   *
   * @param userId the id of the user with whom the event log entries are linked
   * @return the list of event log entries
   */
  public static List<EventLog> fetchEventLogForUserById(int userId) {
    return eventLogDao.fetchByUserId(userId);
  }

  /**
   * Get all event log entries that are marked as global. Those entries will be shown in the event
   * log page.
   *
   * @return the list of global event log entries
   */
  public static List<EventLog> fetchEventLogGlobal() {
    return eventLogDao.fetchByShowInGlobal(Boolean.TRUE);
  }

  /**
   * Create a json event log entry.
   *
   * @param message    the message string for custom types
   * @param filename   if the entry refers to any file on the disk, supply the filename here
   * @param url        if the entry refers to any link, supply the url here
   * @param parameters if the entry contains parameters, supply them here
   * @return the json representation of the event log entry
   */
  private static String createJsonEventLogEntry(String message, String filename, String url,
      List<String> parameters) {
    // Don't create empty json objects
    if (SamplyShareUtils.isNullOrEmpty(message)
        && SamplyShareUtils.isNullOrEmpty(filename)
        && SamplyShareUtils.isNullOrEmpty(url)
        && SamplyShareUtils.isNullOrEmpty(parameters)) {
      return null;
    }
    EventLogEntry eventLogMessage = new EventLogEntry();
    eventLogMessage.setMessage(message);
    eventLogMessage.setFileName(filename);
    eventLogMessage.setUrl(url);
    if (!SamplyShareUtils.isNullOrEmpty(parameters)) {
      eventLogMessage.setParameters(parameters);
    }

    Gson gson = new Gson();
    return gson.toJson(eventLogMessage);
  }

  /**
   * Insert a new event log entry into the database This entry is the outcome of the communication
   * with the Mainzelliste in nNGM.
   *
   * @param messageType    pre-defined event type
   * @param fhirEventAudit EventAudit in JSON
   */

  public static void insertEventLogEntryForMainzelliste(EventMessageType messageType,
      String fhirEventAudit) {
    EventLog eventLog = new EventLog();
    eventLog.setEntry(fhirEventAudit);
    eventLog.setEventType(messageType);
    insertEventLog(eventLog);
  }

}
