package de.samply.share.client.job.util;

import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.model.cql.CqlResult;

import java.util.List;

public class CqlResultFactory {

    private final InquiryDetails inquiryDetails;

    public CqlResultFactory(InquiryDetails inquiryDetails) {
        this.inquiryDetails = inquiryDetails;
    }

    public CqlResult createCqlResult() {
        List<InquiryCriteria> inquiryCriteriaList = InquiryCriteriaUtil.getInquiryCriteriaForInquiryDetails(inquiryDetails);
        int patientCount = 0;
        int specimenCount = 0;
        for (InquiryCriteria inquiryCriteria : inquiryCriteriaList) {
            if (InquiryCriteriaEntityType.PATIENT.getName().equals(inquiryCriteria.getEntityType())) {
                patientCount = InquiryResultUtil.fetchLatestInquiryResultForInquiryCriteriaById(inquiryCriteria.getId()).getSize();
            } else if (InquiryCriteriaEntityType.SPECIMEN.getName().equals(inquiryCriteria.getEntityType())) {
                specimenCount = InquiryResultUtil.fetchLatestInquiryResultForInquiryCriteriaById(inquiryCriteria.getId()).getSize();
            }
        }
        CqlResult queryResult = new CqlResult();
        queryResult.setNumberOfPatients(patientCount);
        queryResult.setNumberOfSpecimens(specimenCount);
        return queryResult;
    }

}
