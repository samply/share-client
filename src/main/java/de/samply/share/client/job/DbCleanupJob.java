package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.util.List;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Do some housekeeping in the database (e.g. mark inquiries as archived after a certain amount of
 * time).
 */
@DisallowConcurrentExecution
public class DbCleanupJob implements Job {

  private static final Logger logger = LoggerFactory.getLogger(DbCleanupJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    moveOldInquiries();
    checkResultAvailability();
  }

  /**
   * Mark all inquiries, that are older than the configured threshold, as archived.
   */
  private void moveOldInquiries() {
    int daysThreshold = ConfigurationUtil.getConfigurationTimingsElementValue(
        EnumConfigurationTimings.JOB_MOVE_INQUIRIES_TO_ARCHIVE_AFTER_DAYS);
    logger.debug("Archiving all inquiries older than " + daysThreshold + " days.");
    List<InquiryDetails> inquiryDetailsList = InquiryDetailsUtil
        .getInquiryDetailsOlderThanDays(daysThreshold);

    for (InquiryDetails inquiryDetails : inquiryDetailsList) {
      EventLogUtil
          .insertEventLogEntryForInquiryId(EventMessageType.E_ARCHIVE_INQUIRY_AFTER_THRESHOLD,
              inquiryDetails.getInquiryId(), Integer.toString(daysThreshold));
      Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
      inquiry.setArchivedAt(SamplyShareUtils.getCurrentSqlTimestamp());
      Utils.setStatus(inquiryDetails, InquiryStatusType.IS_ARCHIVED);
      InquiryUtil.updateInquiry(inquiry);
    }
    InquiryDetailsUtil.updateInquiryDetails(inquiryDetailsList);
  }

  /**
   * Iterate through all inquiry results and check if they can still be accessed.
   */
  private void checkResultAvailability() {
    LdmConnector ldmConnector = ApplicationBean.getLdmConnector();

    // First, check if local datamanagement is reachable. If not - don't fiddle with the results
    try {
      ldmConnector.getUserAgentInfo();
    } catch (LdmConnectorException e) {
      logger.debug(
          "Local Datamangagement not reachable at this moment. Skip checking result availability.");
      return;
    }

    List<InquiryResult> inquiryResults = InquiryResultUtil.fetchInquiryResults();

    for (InquiryResult inquiryResult : inquiryResults) {
      InquiryDetails inquiryDetails = InquiryDetailsUtil
          .fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
      if (!(inquiryDetails.getStatus().equals(InquiryStatusType.IS_LDM_ERROR))) {
        try {
          ldmConnector.getPageCount(inquiryResult.getLocation());
        } catch (LdmConnectorException e) {
          inquiryResult.setIsError(true);
          if (!(checkInquiryID(inquiryResults, inquiryResult))) {
            removeResult(inquiryResult);
          }
        }
      }

    }

  }

  /**
   * Remove the location of an inquiry result and mark the inquiry (details) as archived.
   *
   * @param inquiryResult the inquiry result to "remove"
   */
  private void removeResult(InquiryResult inquiryResult) {
    InquiryDetails inquiryDetails = InquiryDetailsUtil
        .fetchInquiryDetailsById(inquiryResult.getInquiryDetailsId());
    EventLogUtil
        .insertEventLogEntryForInquiryId(EventMessageType.E_ARCHIVE_INQUIRY_RESULT_UNAVAILABLE,
            inquiryDetails.getInquiryId());
    inquiryResult.setValidUntil(SamplyShareUtils.getCurrentSqlTimestamp());
    inquiryResult.setLocation("");
    InquiryResultUtil.updateInquiryResult(inquiryResult);
    Utils.setStatus(inquiryDetails, InquiryStatusType.IS_ARCHIVED);
    InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
  }

  /**
   * Check if there is an other inquiry with the same id and with no error result.
   *
   * @param inquiryResults the list of the inquiries
   * @param inquiryResult  the current inquiry
   * @return if there is an other inquiry with the same id and with no error result
   */

  private boolean checkInquiryID(List<InquiryResult> inquiryResults, InquiryResult inquiryResult) {
    return inquiryResults.stream()
        .anyMatch(inquiryResultTmp
            -> inquiryResultTmp.getInquiryDetailsId().equals(inquiryResult.getInquiryDetailsId())
            && !inquiryResultTmp.getIsError());
  }
}
