package de.samply.share.client.job;

import de.samply.share.client.job.util.CqlResultFactory;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnectorCql;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.model.cql.CqlResult;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJobCql extends AbstractCheckInquiryStatusJob<LdmConnectorCql> {

  private static final Logger logger = LogManager.getLogger(CheckInquiryStatusJobCql.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    prepareExecute(jobExecutionContext);

    if (!jobParams.isStatsDone()) {
      logger.debug("Stats were not available before. Checking again.");
      checkForStatsResult(jobExecutionContext);
    }
  }

  boolean applyReplyRulesImmediately(boolean isStats) {
    return false;
  }

  InquiryCriteria getInquiryCriteria() {
    return InquiryCriteriaUtil.getFirstCriteriaOriginal(inquiryDetails, QueryLanguageType.QL_CQL,
        jobParams.getEntityType());
  }


  void handleInquiryStatusReady() {
    inquiryDetails.setStatus(InquiryStatusType.IS_PARTIALLY_READY);

    CheckInquiryStatusReadyForMultipleCriteriaJobCql.spawnNewJob(inquiryDetails);
  }

  @Override
  Consumer<BrokerConnector> getProcessReplyRuleMethod() {
    return brokerConnector -> {
      try {
        CqlResult queryResult = new CqlResultFactory(inquiryDetails).createCqlResult();
        brokerConnector.reply(inquiryDetails, queryResult);
      } catch (BrokerConnectorException e) {
        handleBrokerConnectorException(e);
      }
    };
  }
}
