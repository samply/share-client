package de.samply.share.client.util;

import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.MailSender;
import de.samply.common.mailing.MailSending;
import de.samply.common.mailing.OutgoingEmail;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.client.util.db.UserUtil;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.File;
import java.util.List;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to help with sending of (notification) mails.
 */
public class MailUtils {

  private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);

  /**
   * Check if it is necessary to send out notification mails and send them if it is.
   */
  public static void checkAndSendNotifications() {
    logger.debug("Checking for notifications to send");

    boolean includeEmptyResults = ConfigurationUtil.getConfigurationElementValueAsBoolean(
        EnumConfiguration.DECENTRAL_SEARCH_MAIL_INCLUDE_EMPTY_RESULTS);
    logger.debug("biomaterial...");
    List<InquiryResult> inquiriesBio = InquiryResultUtil
        .getInquiryResultsForNotification(EntityType.E_BIOMATERIAL, includeEmptyResults);
    sendMails(inquiriesBio, EntityType.E_BIOMATERIAL);
    logger.debug("clinical data...");
    List<InquiryResult> inquiriesClinicalData = InquiryResultUtil
        .getInquiryResultsForNotification(EntityType.E_CLINICAL_DATA, includeEmptyResults);
    sendMails(inquiriesClinicalData, EntityType.E_CLINICAL_DATA);
    logger.debug("patients (for studies)...");
    List<InquiryResult> inquiriesStudy = InquiryResultUtil
        .getInquiryResultsForNotification(EntityType.E_PATIENT_FOR_STUDY, includeEmptyResults);
    sendMails(inquiriesStudy, EntityType.E_PATIENT_FOR_STUDY);
    logger.debug("unknown...");
    List<InquiryResult> inquiriesUnknown = InquiryResultUtil
        .getInquiryResultsForNotification(EntityType.UNKNOWN, includeEmptyResults);
    sendMails(inquiriesUnknown, EntityType.UNKNOWN);
  }

  /**
   * Send notification mails for a given entity type.
   *
   * @param inquiryResults list of results to notify about
   * @param type           the requested entity type of these results
   */
  private static void sendMails(List<InquiryResult> inquiryResults, EntityType type) {
    if (SamplyShareUtils.isNullOrEmpty(inquiryResults)) {
      logger.debug("No new notifications to send");
    } else {
      if (sendMail(inquiryResults, type)) {
        logger.debug("Mail sent and notifications deleted");
        InquiryResultUtil.setNotificationSentForInquiryResults(inquiryResults);
      } else {
        logger.debug("Could not send mail.");
      }
    }
  }

  /**
   * Send notification mails for a given entity type.
   *
   * @param inquiryResults list of results to notify about
   * @param type           the requested entity type of these results
   * @return true on success, false on failure or if no receivers were configured
   */
  private static boolean sendMail(List<InquiryResult> inquiryResults, EntityType type) {
    List<User> receivers = UserUtil.getUsersToNotify(type);

    if (receivers == null || receivers.size() < 1) {
      logger.warn("No receiver set for emails. Aborting. (type=" + type + ")");
      return false;
    }

    OutgoingEmail email = new OutgoingEmail();
    for (User receiver : receivers) {
      String receiverMail = receiver.getEmail();
      if (receiverMail == null || receiverMail.length() < 1) {
        continue;
      }
      logger.debug("Adding receiver: " + receiverMail);
      email.addAddressee(receiverMail);
    }

    if (email.getAddressees() == null || email.getAddressees().isEmpty()) {
      logger.warn("No valid receivers found. Aborting.");
      return false;
    }

    String mailSubject = Messages.getString("MAIL_NEW_INQUIRIES_SUBJECT") + " (" + Messages
        .getString(type.getLiteral()) + ")";
    String shareUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.SHARE_URL);
    email.setSubject(mailSubject);
    email.setLocale(ApplicationBean.getLocale().getLanguage());
    email.putParameter("results", generateResultsParameter(shareUrl, inquiryResults));
    String projectName = ProjectInfo.INSTANCE.getProjectName();
    MailSending mailSending = MailSender.loadMailSendingConfig(projectName,
        System.getProperty("catalina.base") + File.separator + "conf",
        ProjectInfo.INSTANCE.getServletContext().getRealPath("/WEB-INF"));

    EmailBuilder builder = initializeBuilder(mailSending);
    builder.addTemplateFile("NewInquiriesContent.soy", "NewInquiriesContent");
    email.setBuilder(builder);

    Thread mailSenderThread = new Thread(new MailSenderThread(mailSending, email));
    mailSenderThread.start();

    return true;
  }

  /**
   * Generate a string with all inquiry links.
   *
   * @param shareUrl       the url of the samply share client
   * @param inquiryResults the list of inquiry results to notify about
   * @return a string with all links, split by newline parameter
   */
  private static String generateResultsParameter(String shareUrl,
      List<InquiryResult> inquiryResults) {
    StringBuilder stringBuilder = new StringBuilder();
    for (InquiryResult inquiryResult : inquiryResults) {
      InquiryDetails inquiryDetails = InquiryDetailsUtil
          .fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
      Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
      stringBuilder.append(inquiry.getLabel())
          .append(": ")
          .append(Messages.getString("E_STATISTICS_READY-SHORT", inquiryResult.getSize()))
          .append(" ")
          .append(SamplyShareUtils.addTrailingSlash(shareUrl))
          .append("user/show_inquiry.xhtml?inquiryId=")
          .append(inquiryDetails.getInquiryId())
          .append("&faces-redirect=true")
          .append("\n");
    }
    return stringBuilder.toString();
  }

  /**
   * Initializes an EmailBuilder with the two default Soy-Templates: main.soy and footer.soy.
   *
   * @param mailSending Configuration object for the mail sending tool
   * @return the email builder
   */
  private static EmailBuilder initializeBuilder(MailSending mailSending) {
    String templateFolder = getRealPath(mailSending.getTemplateFolder());

    EmailBuilder builder = new EmailBuilder(templateFolder, false);
    builder.addTemplateFile("MainMailTemplate.soy", null);
    builder.addTemplateFile("Footer.soy", "Footer");
    return builder;
  }

  /**
   * Get the real path for a relative path.
   *
   * @param relativeWebPath the relative path
   * @return the real path
   */
  private static String getRealPath(String relativeWebPath) {
    ServletContext sc = ProjectInfo.INSTANCE.getServletContext();
    return sc.getRealPath(relativeWebPath);
  }

}
