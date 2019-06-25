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

package de.samply.share.client.util;

import java.io.File;
import java.util.List;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.MailSender;
import de.samply.common.mailing.MailSending;
import de.samply.common.mailing.OutgoingEmail;
import de.samply.share.common.utils.ProjectInfo;

import javax.servlet.ServletContext;

/**
 * Utility class to help with sending of (notification) mails
 */
public class MailUtils {

    private static final Logger logger = LogManager.getLogger(MailUtils.class);

    /**
     * Check if it is necessary to send out notification mails and send them if it is
     */
    public static void checkAndSendNotifications() {
        logger.debug("Checking for notifications to send");

        boolean includeEmptyResults = ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.DECENTRAL_SEARCH_MAIL_INCLUDE_EMPTY_RESULTS);

        List<InquiryResult> inquiriesBio = InquiryResultUtil.getInquiryResultsForNotification(EntityType.E_BIOMATERIAL, includeEmptyResults);
        List<InquiryResult> inquiriesClinicalData = InquiryResultUtil.getInquiryResultsForNotification(EntityType.E_CLINICAL_DATA, includeEmptyResults);
        List<InquiryResult> inquiriesStudy = InquiryResultUtil.getInquiryResultsForNotification(EntityType.E_PATIENT_FOR_STUDY, includeEmptyResults);
        List<InquiryResult> inquiriesUnknown = InquiryResultUtil.getInquiryResultsForNotification(EntityType.UNKNOWN, includeEmptyResults);


        logger.debug("biomaterial...");
        sendMails(inquiriesBio, EntityType.E_BIOMATERIAL);
        logger.debug("clinical data...");
        sendMails(inquiriesClinicalData, EntityType.E_CLINICAL_DATA);
        logger.debug("patients (for studies)...");
        sendMails(inquiriesStudy, EntityType.E_PATIENT_FOR_STUDY);
        logger.debug("unknown...");
        sendMails(inquiriesUnknown, EntityType.UNKNOWN);
    }

    /**
     * Send notification mails for a given entity type
     *
     * @param inquiryResults list of results to notify about
     * @param type the requested entity type of these results
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
     * Send notification mails for a given entity type
     *
     * @param inquiryResults list of results to notify about
     * @param type the requested entity type of these results
     * @return true on success, false on failure or if no receivers were configured
     */
    private static boolean sendMail(List<InquiryResult> inquiryResults, EntityType type) {
        List<User> receivers = UserUtil.getUsersToNotify(type);
        String shareUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.SHARE_URL);

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

        String mailSubject = Messages.getString("MAIL_NEW_INQUIRIES_SUBJECT") + " (" +  Messages.getString(type.getLiteral()) + ")";

        email.setSubject(mailSubject);
        email.setLocale(ApplicationBean.getLocale().getLanguage());
        email.putParameter("results", generateResultsParameter(shareUrl, inquiryResults));
        String projectName = ProjectInfo.INSTANCE.getProjectName();
        MailSending mailSending = MailSender.loadMailSendingConfig(projectName, System.getProperty("catalina.base") + File.separator + "conf", ProjectInfo.INSTANCE.getServletContext().getRealPath("/WEB-INF"));

        EmailBuilder builder = initializeBuilder(mailSending);
        builder.addTemplateFile("NewInquiriesContent.soy", "NewInquiriesContent");
        email.setBuilder(builder);

        Thread mailSenderThread = new Thread(new MailSenderThread(mailSending, email));
        mailSenderThread.start();

        return true;
    }

    /**
     * Generate a string with all inquiry links
     *
     * @param shareUrl the url of the samply share client
     * @param inquiryResults the list of inquiry results to notify about
     * @return a string with all links, split by newline parameter
     */
    private static String generateResultsParameter(String shareUrl, List<InquiryResult> inquiryResults) {
        StringBuilder stringBuilder = new StringBuilder();
        for (InquiryResult inquiryResult : inquiryResults) {
            InquiryDetails inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
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
     * Get the real path for a relative path
     *
     * @param relativeWebPath the relative path
     * @return the real path
     */
    private static String getRealPath(String relativeWebPath) {
        ServletContext sc = ProjectInfo.INSTANCE.getServletContext();
        return sc.getRealPath(relativeWebPath);
    }

}
