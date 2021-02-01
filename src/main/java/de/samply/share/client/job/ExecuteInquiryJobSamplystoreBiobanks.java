package de.samply.share.client.job;

import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_ABANDONED;
import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_LDM_ERROR;
import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_PROCESSING;

import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.util.connector.LdmConnectorSamplystoreBiobank;
import de.samply.share.client.util.connector.LdmPostQueryParameterView;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryDetailsUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Query;
import de.samply.share.utils.QueryConverter;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.JobExecutionException;

/**
 * This Job posts an inquiry to the local datamanagement, stores the location and spawns a
 * CheckInquiryStatusJobSamplystoreBiobanks. It is defined and scheduled by either the
 * CheckInquiryStatusJob, the CollectInquiriesJob, the UploadToCentralMdsDbJob or can be spawned
 * user-triggered from the show_inquiry.xhtml page.
 */
public class ExecuteInquiryJobSamplystoreBiobanks extends
    AbstractExecuteInquiryJob<LdmConnectorSamplystoreBiobank> {

  void execute() throws JobExecutionException {
    InquiryCriteria inquiryCriteria = InquiryCriteriaUtil
        .getFirstCriteriaOriginal(inquiryDetails, QueryLanguageType.QL_QUERY);

    executeOneCriteria(inquiryCriteria);
  }

  private void executeOneCriteria(InquiryCriteria inquiryCriteria) throws JobExecutionException {
    List<String> unknownKeys = jobParams.getUnknownKeys();

    try {
      setInquiryDetailsStatusAndUpdateInquiryDetails(IS_PROCESSING);
      Query modifiedQuery = null;
      Query originalQuery = QueryConverter.xmlToQuery(inquiryCriteria.getCriteriaOriginal());

      // TODO remove this "temporary" workaround as soon as possible! This is linked with the
      //  age-old issue of different java date formats in some mdr elements!
      originalQuery = fixDateIssues(originalQuery);

      if (!SamplyShareUtils.isNullOrEmpty(unknownKeys)) {
        log(EventMessageType.E_REPEAT_EXECUTE_INQUIRY_JOB_WITHOUT_UNKNOWN_KEYS,
            unknownKeys.toArray(new String[0]));
        modifiedQuery = QueryConverter.removeAttributesFromQuery(originalQuery, unknownKeys);
        inquiryCriteria.setCriteriaModified(QueryConverter.queryToXml(modifiedQuery));
        InquiryDetailsUtil.updateInquiryDetails(inquiryDetails);
      }

      log(EventMessageType.E_START_EXECUTE_INQUIRY_JOB);
      Query query = ObjectUtils.defaultIfNull(modifiedQuery, originalQuery);
      LdmPostQueryParameterView parameter = new LdmPostQueryParameterView(jobParams.isStatsOnly(),
          unknownKeys, true, !jobParams.isUpload());
      String resultLocation = ldmConnector.postQuery(query, parameter);

      if (resultLocation != null && resultLocation.length() > 0) {
        log(EventMessageType.E_INQUIRY_RESULT_AT, resultLocation);
        int inquiryResultId = createNewInquiryResult(resultLocation, inquiryCriteria.getId());
        spawnNewCheckInquiryStatusJob(inquiryResultId, inquiryCriteria.getEntityType());
      } else {
        log(EventMessageType.E_RESULT_NOT_SET_ABORTING);
        setInquiryDetailsStatusAndUpdateInquiryDetails(IS_LDM_ERROR);
      }

    } catch (JAXBException e) {
      log(EventMessageType.E_FAILED_JAXB_ERROR, e.getMessage());
      setInquiryDetailsStatusAndUpdateInquiryDetails(IS_ABANDONED);
      throw new JobExecutionException(e);
    } catch (LdmConnectorException e) {
      log(EventMessageType.E_RESULT_NOT_SET_ABORTING);
      setInquiryDetailsStatusAndUpdateInquiryDetails(IS_LDM_ERROR);
      throw new JobExecutionException(e);
    }
  }
}
