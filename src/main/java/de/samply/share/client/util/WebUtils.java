package de.samply.share.client.util;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumInquiryStatus;
import de.samply.share.client.model.EventLogEntry;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UserUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Case;
import de.samply.share.model.common.Container;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class WebUtils offers methods to be used directly from xhtml via the corresponding taglib
 * (webutils.taglib.xml).
 */
public final class WebUtils {

  private static final Logger logger = LogManager.getLogger(WebUtils.class);

  /**
   * Prohibit class instantiation.
   */
  private WebUtils() {
  }

  /**
   * Gets the designation for an dataelement in the mdr.
   *
   * @param dataElement  the data element id
   * @param languageCode the language code
   * @return the designation
   */
  public static String getDesignation(String dataElement, String languageCode) {
    return MdrUtils.getDesignation(dataElement, languageCode);
  }

  /**
   * Gets the designation of a certain value of a dataelement.
   *
   * @param dataElement  the data element
   * @param value        the value
   * @param languageCode the language code
   * @return the designation
   */
  public static String getValueDesignation(String dataElement, Object value, String languageCode) {
    return MdrUtils.getValueDesignation(dataElement, value, languageCode);
  }

  /**
   * Convert a given timestamp to a String in dd.MM.yyyy HH:mm:ss format.
   *
   * @param time the time
   * @return the converted timestamp.
   */
  public static String convertTime(Timestamp time) {
    return SamplyShareUtils.convertSqlTimestampToString(time, "dd.MM.yyyy HH:mm:ss");
  }

  /**
   * Gets the project name.
   *
   * @return the project name
   */
  public static String getProjectName() {
    return ProjectInfo.INSTANCE.getProjectName();
  }

  /**
   * Gets the version string of this Samply Share instance to show it on the login screen.
   *
   * @return the version string
   */
  public static String getVersionString() {
    return ProjectInfo.INSTANCE.getVersionString();
  }

  public static String getBuildDate() {
    return ProjectInfo.INSTANCE.getBuildDateString();
  }

  /**
   * Gets the case date string.
   *
   * @param commomCase the Case
   * @return the case date string
   */
  public static String getCaseDateString(Case commomCase) {
    for (Attribute a : commomCase.getAttribute()) {
      MdrIdDatatype mdrId = new MdrIdDatatype(a.getMdrKey());
      if (mdrId.equalsIgnoreVersion(
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CASE_DATE))) {
        return (Messages.getString("WebUtils_caseDateYear") + " " + a.getValue().getValue());
      }
    }
    return Messages.getString("WebUtils_caseDateUnknown");
  }

  /**
   * Gets the execution date.
   *
   * @param inquiryResult the inquiry result
   * @return the execution date
   */
  public static String getExecutionDate(InquiryResult inquiryResult) {
    if (inquiryResult != null) {
      Timestamp time = inquiryResult.getExecutedAt();
      if (time != null) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(time);
      }
    }
    return "";
  }

  /**
   * Gets the id of the server's time zone. This can be used for {@code <f:convertDateTime>} tags
   * that must display the date in the server's time zone.
   *
   * @return
   */
  public static String getServerTimeZone() {
    return TimeZone.getDefault().getID();
  }

  /**
   * Gets the parent node index.
   *
   * @param id the id of the node to check
   * @return the parent node index
   */
  public static String getParentNodeIndex(String id) {
    final String separator = "_";
    if (SamplyShareUtils.isNullOrEmpty(id)) {
      return "";
    } else if (!id.contains(separator)) {
      return id;
    } else {
      return (id.substring(0, id.lastIndexOf(separator)));
    }
  }

  /**
   * Gets the gender of a given patient (-container).
   *
   * @param container the patient container
   * @return the gender of the given patient
   */
  public static String getGender(Container container) {
    MdrIdDatatype genderMdrId = new MdrIdDatatype(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_GENDER));
    MdrIdDatatype genderMdrId2 = new MdrIdDatatype("urn:mdr16:dataelement:23:1");
    MdrIdDatatype genderMdrId3 = new MdrIdDatatype("urn:bbmri:dataelement:8:1");
    List<MdrIdDatatype> mdr = new ArrayList<>();
    mdr.add(genderMdrId);
    mdr.add(genderMdrId2);
    mdr.add(genderMdrId3);

    for (Attribute attribute : container.getAttribute()) {
      MdrIdDatatype mdrId = new MdrIdDatatype(attribute.getMdrKey());
      if (mdrId.equalsIgnoreVersion(genderMdrId) || mdrId.equalsIgnoreVersion(genderMdrId2) || mdrId
          .equalsIgnoreVersion(genderMdrId3)) {

        return attribute.getValue().getValue().replace("\"", "");
      }
    }
    // Return unknown if nothing was found
    return "U";
  }

  /**
   * Count on how many brokers this instance of samply share is registered.
   *
   * @return the amount of brokers
   */
  public static long getBrokerCount() {
    return BrokerUtil.getCount();
  }

  /**
   * Count how many inquiries of the given status are in the database.
   *
   * @param status to differentiate between active, archived and erroneous
   * @return the amount of inquiries
   */
  public static long getInquiryCount(EnumInquiryStatus status) {
    switch (status) {
      case INQUIRY_ARCHIVE:
        return InquiryUtil.countInquiries(InquiryStatusType.IS_ARCHIVED);
      case INQUIRY_ERROR:
        return InquiryUtil
            .countInquiries(InquiryStatusType.IS_LDM_ERROR, InquiryStatusType.IS_ABANDONED);
      case INQUIRY_ACTIVE:
      default:
        return InquiryUtil.countInquiries(InquiryStatusType.IS_NEW, InquiryStatusType.IS_PROCESSING,
            InquiryStatusType.IS_READY);
    }
  }

  /**
   * Get a formatted entry line for the log to display on show_inquiry.xhtml.
   *
   * @param logEntry the event log entry
   * @return the formatted entry for the log
   */
  public static String formatInquiryLogEntry(
      de.samply.share.client.model.db.tables.pojos.EventLog logEntry) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SamplyShareUtils
        .convertSqlTimestampToString(logEntry.getEventTime(), "dd.MM.yyyy HH:mm:ss"));
    stringBuilder.append(" - ");

    EventMessageType eventType = logEntry.getEventType();
    if (eventType != null) {
      if (!SamplyShareUtils.isNullOrEmpty(logEntry.getEntry())) {
        Gson gson = new Gson();
        EventLogEntry entry = gson.fromJson(logEntry.getEntry(), EventLogEntry.class);
        // Special case: Unknown Keys entry. Since JSF Messages don't support a variable amount of
        // parameters, join them to a comma-separated string
        switch (eventType) {
          case E_INQUIRY_RESULT_AT:
            try {
              String url = entry.getParameters().get(0); // This should only be the url right now...
              stringBuilder.append("<a href=\"");
              stringBuilder.append(url);
              stringBuilder.append("\" target=\"_blank\">");
              stringBuilder.append(Messages.getString("si_showQuery"));
              stringBuilder.append("</a>");

              stringBuilder.append(" / ");

              stringBuilder.append("<a href=\"");
              stringBuilder.append(url);
              stringBuilder.append("/stats\" target=\"_blank\">");
              stringBuilder.append(Messages.getString("si_showStats"));
              stringBuilder.append("</a>");

            } catch (Exception e) {
              logger.trace("Exception caught while formatting inquiry log entry", e);
            }
            break;
          case E_REPEAT_EXECUTE_INQUIRY_JOB_WITHOUT_UNKNOWN_KEYS:
            stringBuilder.append(Messages.getString(eventType.getLiteral() + "-SHORT"));
            stringBuilder.append(" <i class=\"fa fa-lg fa-info-circle\" title=\"");
            stringBuilder.append(Joiner.on(", ").join(entry.getParameters()));
            stringBuilder.append("\"></i>");
            break;
          default:
            stringBuilder.append(Messages.getString(eventType.getLiteral() + "-SHORT",
                entry.getParameters().toArray(new Object[entry.getParameters().size()])));
            break;
        }
      } else {
        stringBuilder.append(Messages.getString(eventType.getLiteral() + "-SHORT"));
      }
    } else {
      stringBuilder.append(logEntry.getEntry());
    }

    return stringBuilder.toString();
  }

  /**
   * Convert timestamp for better readability on the web page.
   *
   * @param timestamp the timestamp to convert
   * @return the formatted timestamp string
   */
  public static String convertTimestamp(Timestamp timestamp) {
    if (timestamp == null) {
      return "";
    }
    return SamplyShareUtils.convertSqlTimestampToString(timestamp,
        "dd.MM.yyyy HH:mm");
  }

  /**
   * Config files may contain "localhost" addresses for components. In this case, replace the links.
   * If the port is also the same, just use the path otherwise, replace the host part with the
   * requestURL host part.
   *
   * @param in Todo.
   * @return Todo.
   */
  public static String replaceLocalhostInUri(String in) {
    try {
      HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
          .getExternalContext().getRequest();
      URI uri = new URI(in);
      URI requestUri = new URI(origRequest.getRequestURL().toString());

      if ("localhost".equalsIgnoreCase(uri.getHost()) || "127.0.0.1".equals(uri.getHost())) {
        if (requestUri.getPort() == uri.getPort()) {
          return uri.getPath();
        } else {
          StringBuilder sb = new StringBuilder();
          sb.append(uri.getScheme());
          sb.append("://");
          sb.append(requestUri.getHost());
          if (uri.getPort() > 0) {
            sb.append(":");
            sb.append(uri.getPort());
          }
          sb.append(uri.getPath());

          return sb.toString();
        }
      }
    } catch (Exception e) {
      return in;
    }
    return in;
  }

  /**
   * Convert a date to German standard format.
   *
   * @param date the date to format
   * @return dd.MM.yyyy representation of the given date as a string
   */
  public static String dateToGermanFormatString(Date date) {
    if (date == null) {
      return "";
    }
    return new SimpleDateFormat("dd.MM.yyyy").format(date);
  }

  /**
   * Get the user name for a user id.
   *
   * @param userId the id of the user
   * @return the name of the user or "-" if none found
   */
  public static String getUsernameById(int userId) {
    try {
      return UserUtil.fetchUserById(userId).getUsername();
    } catch (Exception e) {
      return "-";
    }
  }

  /**
   * Check if a site-specific path and credentials are set for central search.
   *
   * @return true if a path is set in the config, false otherwise
   */
  public static boolean isCentralSearchPathSet() {
    if (SamplyShareUtils.isNullOrEmpty(ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.CENTRAL_MDS_DATABASE_PATH))) {
      return false;
    }
    return !SamplyShareUtils
        .isNullOrEmpty(CredentialsUtil.getCredentialsByTarget(TargetType.TT_CENTRALSEARCH));
  }

  public static String getQueryLanguage() {
    return ApplicationBean.getBridgeheadInfos().getQueryLanguage();
  }
}
