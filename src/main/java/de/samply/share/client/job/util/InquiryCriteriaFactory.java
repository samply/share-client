package de.samply.share.client.job.util;

import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;

public class InquiryCriteriaFactory {

    public InquiryCriteria createForQuery(int detailsId) {
        InquiryCriteria inquiryCriteria = new InquiryCriteria();

        inquiryCriteria.setDetailsId(detailsId);
        inquiryCriteria.setQueryLanguage(QueryLanguageType.QUERY);
        inquiryCriteria.setStatus(InquiryCriteriaStatusType.ICS_NEW);
        inquiryCriteria.setEntityType("Donor + Sample");

        return inquiryCriteria;
    }
}
