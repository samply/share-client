package de.samply.share.client.job.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.model.common.result.Stratification;
import de.samply.share.model.cql.CqlResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CqlResultFactory {

  private static final Logger logger = LoggerFactory.getLogger(CqlResultFactory.class);

  private final InquiryDetails inquiryDetails;

  public CqlResultFactory(InquiryDetails inquiryDetails) {
    this.inquiryDetails = inquiryDetails;
  }

  /**
   * Todo.
   *
   * @return CqlResult
   */
  public CqlResult createCqlResult() {
    List<InquiryCriteria> inquiryCriteriaList = InquiryCriteriaUtil
        .getInquiryCriteriaForInquiryDetails(inquiryDetails);
    CqlResult queryResult = new CqlResult();

    for (InquiryCriteria inquiryCriteria : inquiryCriteriaList) {
      InquiryResult inquiryResult = InquiryResultUtil
          .fetchLatestInquiryResultForInquiryCriteriaById(inquiryCriteria.getId());
      if (InquiryCriteriaEntityType.PATIENT.getName().equals(inquiryCriteria.getEntityType())) {
        queryResult.setNumberOfPatients(inquiryResult.getSize());
        List<Stratification> stratifications = readStratifications(inquiryResult);
        queryResult.setStratificationsOfPatients(stratifications);
      } else if (InquiryCriteriaEntityType.SPECIMEN.getName()
          .equals(inquiryCriteria.getEntityType())) {
        queryResult.setNumberOfSpecimens(inquiryResult.getSize());
        List<Stratification> stratifications = readStratifications(inquiryResult);
        queryResult.setStratificationsOfSpecimens(stratifications);
      }
    }

    return queryResult;
  }

  private List<Stratification> readStratifications(InquiryResult inquiryResult) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      return mapper
          .readValue(inquiryResult.getStratifications(), new TypeReference<List<Stratification>>() {
          });
    } catch (IOException e) {
      logger.error(e.getMessage(),e);
      return new ArrayList<>();
    }
  }

}
