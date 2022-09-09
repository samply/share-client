package de.samply.share.client.job;

import de.samply.common.ldmclient.LdmClientException;
import de.samply.share.client.job.util.CqlResultFactory;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnectorCql;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.model.cql.CqlResult;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJobCql extends AbstractCheckInquiryStatusJob<LdmConnectorCql> {

  private static final Logger logger = LoggerFactory.getLogger(CheckInquiryStatusJobCql.class);

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
    createSublist();
    CheckInquiryStatusReadyForMultipleCriteriaJobCql.spawnNewJob(inquiryDetails);
  }

  private void createSublist() {
    try {
      String location = ldmConnector.createSubjectList(new URI(inquiryResult.getLocation()));
      InquiryResult inquiryResultSubList = new InquiryResult();
      inquiryResultSubList.setInquiryCriteriaId(inquiryResult.getInquiryCriteriaId());
      inquiryResultSubList.setInquiryDetailsId(inquiryResult.getInquiryDetailsId());
      inquiryResultSubList.setSize(inquiryResult.getSize());
      inquiryResultSubList.setStatisticsOnly(false);
      inquiryResultSubList.setIsError(false);
      inquiryResultSubList.setNotificationSent(false);
      inquiryResultSubList.setLocation(location);
      InquiryResultUtil.insertInquiryResult(inquiryResultSubList);
    } catch (LdmClientException | URISyntaxException e) {
      logger.error(e.getMessage());
    }

  }


  @Override
  Consumer<BrokerConnector> getProcessReplyRuleMethod() {
    return brokerConnector -> {
      try {
        CqlResult queryResult = new CqlResultFactory(inquiryDetails).createCqlResult();
        brokerConnector.reply(inquiryDetails, queryResult);
      } catch (BrokerConnectorException e) {
        logger.error(e.getMessage(), e);
        handleBrokerConnectorException(e);
      }
    };
  }
}
