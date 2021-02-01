package de.samply.share.client.job;

import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnectorSamplystoreBiobank;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.model.bbmri.BbmriResult;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJobSamplystoreBiobanks extends
    AbstractCheckInquiryStatusJob<LdmConnectorSamplystoreBiobank> {

  private static final Logger logger = LogManager
      .getLogger(CheckInquiryStatusJobSamplystoreBiobanks.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    prepareExecute(jobExecutionContext);

    if (!jobParams.isStatsDone()) {
      logger.debug("Stats were not available before. Checking again.");
      checkForStatsResult(jobExecutionContext);
    }
  }

  boolean applyReplyRulesImmediately(boolean isStats) {
    return isStats && jobParams.isStatsOnly();
  }

  InquiryCriteria getInquiryCriteria() {
    return InquiryCriteriaUtil.getFirstCriteriaOriginal(inquiryDetails, QueryLanguageType.QL_QUERY);
  }

  void handleInquiryStatusReady() {
    Utils.setStatus(inquiryDetails, InquiryStatusType.IS_READY);
  }

  @Override
  Consumer<BrokerConnector> getProcessReplyRuleMethod() {
    return brokerConnector -> {
      try {
        BbmriResult queryResult = ldmConnector.getResults(
            InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId())
                .getLocation());
        brokerConnector.reply(inquiryDetails, queryResult);
      } catch (LdmConnectorException e) {
        e.printStackTrace();
      } catch (BrokerConnectorException e) {
        handleBrokerConnectorException(e);
      }
    };
  }
}
