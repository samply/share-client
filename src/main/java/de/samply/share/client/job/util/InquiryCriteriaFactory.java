package de.samply.share.client.job.util;

import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;

public class InquiryCriteriaFactory {

  /**
   * Create a new InquiryCriteria for a View Query and set the inquiryDetailsId.
   *
   * @param detailsId the InquiryDetailsId
   * @return InquiryCriteria
   */
  public InquiryCriteria createForViewQuery(int detailsId) {
    InquiryCriteria inquiryCriteria = new InquiryCriteria();

    inquiryCriteria.setDetailsId(detailsId);
    inquiryCriteria.setQueryLanguage(QueryLanguageType.QL_QUERY);
    inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_NEW);
    inquiryCriteria.setEntityType(InquiryCriteriaEntityType.ALL.getName());

    return inquiryCriteria;
  }

  /**
   * Create a new InquiryCriteria for a CQL Query and set the inquiryDetailsId and the entityType.
   *
   * @param detailsId  the InquiryDetailsId
   * @param entityType the entityType
   * @return InquiryCriteria
   */
  public InquiryCriteria createForCqlQuery(int detailsId, InquiryCriteriaEntityType entityType) {
    InquiryCriteria inquiryCriteria = new InquiryCriteria();

    inquiryCriteria.setDetailsId(detailsId);
    inquiryCriteria.setQueryLanguage(QueryLanguageType.QL_CQL);
    inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_NEW);
    inquiryCriteria.setEntityType(entityType.getName());

    return inquiryCriteria;
  }
}
