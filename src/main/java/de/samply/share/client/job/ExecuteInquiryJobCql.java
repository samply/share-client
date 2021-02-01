package de.samply.share.client.job;

import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_LDM_ERROR;
import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_PROCESSING;

import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.util.connector.LdmConnectorCql;
import de.samply.share.client.util.connector.LdmPostQueryParameterCql;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionException;

/**
 * This Job posts an inquiry to the local datamanagement, stores the location and spawns a
 * CheckInquiryStatusJobCql. It is defined and scheduled by either the CheckInquiryStatusJobCql, the
 * CollectInquiriesJob or can be spawned user-triggered from the show_inquiry.xhtml page.
 */
public class ExecuteInquiryJobCql extends AbstractExecuteInquiryJob<LdmConnectorCql> {

  void execute() throws JobExecutionException {
    List<InquiryCriteria> inquiryCriteriaList = InquiryCriteriaUtil
        .getInquiryCriteriaForInquiryDetails(inquiryDetails);

    for (InquiryCriteria inquiryCriteria : inquiryCriteriaList) {
      executeOneCriteria(inquiryCriteria);
    }
  }

  private void executeOneCriteria(InquiryCriteria inquiryCriteria) throws JobExecutionException {
    try {
      setInquiryDetailsStatusAndUpdateInquiryDetails(IS_PROCESSING);
      String query = inquiryCriteria.getCriteriaOriginal();

      log(EventMessageType.E_START_EXECUTE_INQUIRY_JOB);
      LdmPostQueryParameterCql parameter = new LdmPostQueryParameterCql(jobParams.isStatsOnly(),
          inquiryCriteria.getEntityType());
      String resultLocation = ldmConnector.postQuery(query, parameter);

      if (!StringUtils.isEmpty(resultLocation)) {
        log(EventMessageType.E_INQUIRY_RESULT_AT, resultLocation);
        int inquiryResultId = createNewInquiryResult(resultLocation, inquiryCriteria.getId());
        spawnNewCheckInquiryStatusJob(inquiryResultId, inquiryCriteria.getEntityType());
      } else {
        log(EventMessageType.E_RESULT_NOT_SET_ABORTING);
        setInquiryDetailsStatusAndUpdateInquiryDetails(IS_LDM_ERROR);
      }

    } catch (LdmConnectorException e) {
      log(EventMessageType.E_RESULT_NOT_SET_ABORTING);
      setInquiryDetailsStatusAndUpdateInquiryDetails(IS_LDM_ERROR);
      throw new JobExecutionException(e);
    }
  }


}
