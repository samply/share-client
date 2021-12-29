package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.CheckInquiryStatusReadyForMultipleCriteriaJobParams;
import de.samply.share.client.job.util.CqlResultFactory;
import de.samply.share.client.job.util.ReplyRulesApplier;
import de.samply.share.client.job.util.ReplyRulesApplierUtil;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.model.cql.CqlResult;
import java.util.function.Consumer;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckInquiryStatusReadyForMultipleCriteriaJobCql implements Job {

  private static final Logger logger = LoggerFactory
      .getLogger(CheckInquiryStatusReadyForMultipleCriteriaJobCql.class);

  private static final int DELAY_RESCHEDULING = 10;
  private static final int DELAY_FIRST = 1;

  @SuppressWarnings("FieldCanBeLocal")
  private CheckInquiryStatusReadyForMultipleCriteriaJobParams jobParams;
  private InquiryDetails inquiryDetails;

  static void spawnNewJob(InquiryDetails inquiryDetails) {
    spawnNewJob(inquiryDetails, DELAY_FIRST);
  }

  private static void spawnNewJob(InquiryDetails inquiryDetails, int initialDelay) {
    try {
      Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());

      JobKey jobKey = JobKey.jobKey(
          CheckInquiryStatusReadyForMultipleCriteriaJobParams.JOBNAME_CQL,
          CheckInquiryStatusReadyForMultipleCriteriaJobParams.JOBGROUP);

      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(CheckInquiryStatusReadyForMultipleCriteriaJobParams.INQUIRY_DETAILS_ID,
          inquiryDetails.getId());

      logger.info(
          "Give CheckInquiryStatusReadyForMultipleCriteria Job to scheduler for inquiry with id "
              + inquiry.getId());

      Trigger trigger = TriggerBuilder.newTrigger()
          .startAt(DateBuilder.futureDate(initialDelay, DateBuilder.IntervalUnit.SECOND))
          .forJob(jobKey)
          .usingJobData(jobDataMap)
          .build();

      ApplicationBean.getScheduler().scheduleJob(trigger);
    } catch (SchedulerException e) {
      logger.error("Error spawning CheckInquiryStatusReadyForMultipleCriteria Job", e);
    }
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    prepareExecute(jobExecutionContext);

    if (inquiryDetails.getStatus() == InquiryStatusType.IS_READY) {
      return;
    }

    for (InquiryCriteria inquiryCriteria : InquiryCriteriaUtil
        .getInquiryCriteriaForInquiryDetails(inquiryDetails)) {
      if (inquiryCriteria.getStatus() != InquiryCriteriaStatusType.ICS_READY) {
        spawnNewJob(inquiryDetails, DELAY_RESCHEDULING);
        return;
      }
    }

    inquiryDetails.setStatus(InquiryStatusType.IS_READY);
    InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);

    new ReplyRulesApplier(getProcessReplyRuleMethod()).processReplyRules(inquiryDetails);
  }

  private Consumer<BrokerConnector> getProcessReplyRuleMethod() {
    return brokerConnector -> {
      if (!ApplicationUtils.isLanguageCql() || !ApplicationUtils.isSamply()) {
        logger.error("Job " + getClass().getSimpleName()
            + " can only be applied in the context of CQL and Samply");
        return;
      }

      try {
        CqlResult queryResult = new CqlResultFactory(inquiryDetails).createCqlResult();
        brokerConnector.reply(inquiryDetails, queryResult);
      } catch (BrokerConnectorException e) {
        ReplyRulesApplierUtil.handleBrokerConnectorException(e, inquiryDetails.getId());
      }
    };
  }

  private void prepareExecute(JobExecutionContext jobExecutionContext) {
    JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
    JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
    jobParams = new CheckInquiryStatusReadyForMultipleCriteriaJobParams(dataMap);
    logger.debug(jobKey.toString() + " " + jobParams);

    inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(jobParams.getInquiryDetailsId());
  }

}
