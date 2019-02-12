package de.samply.share.client.quality.report.file.csvline.manager;/*
* Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
* Deutsches Krebsforschungszentrum in Heidelberg
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU Affero General Public License as published by the Free
* Software Foundation; either version 3 of the License, or (at your option) any
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program; if not, see http://www.gnu.org/licenses.
*
* Additional permission under GNU GPL version 3 section 7:
*
* If you modify this Program, or any covered work, by linking or combining it
* with Jersey (https://jersey.java.net) (or a modified version of that
* library), containing parts covered by the terms of the General Public
* License, version 2.0, the licensors of this Program grant you additional
* permission to convey the resulting work.
*/

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Definition;
import de.samply.common.mdrclient.domain.Record;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.file.csvline.PatientDataCsvLinePattern_Test1;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class QualityResultCsvLineManagerImpl_Test1 implements QualityResultCsvLineManager {

    private final static String MDR_LINK_PREFIX = "https://mdr.ccp-it.dktk.dkfz.de/detail.xhtml?urn=";
    private final static String languageCode = "de";
    private final static String EXCEL_HYPERLINK_PREFIX = "=HYPERLINK(\"";
    private final static String EXCEL_HYPERLINK_SUFFIX = "\")";

    private ModelSearcher modelSearcher;
    private MdrClient mdrClient;
    private Map<MdrIdDatatype, String> mdrNames = new HashMap<>();

    public QualityResultCsvLineManagerImpl_Test1(Model model, MdrClient mdrClient) {
        this.modelSearcher = new ModelSearcher(model);
        this.mdrClient = mdrClient;
    }

    @Override
    public String createLine(MdrIdDatatype mdrId, String value, QualityResult qualityResult) throws QualityResultCsvLineManagerException {

        PatientDataCsvLinePattern_Test1 csvLine = new PatientDataCsvLinePattern_Test1();

        csvLine.setMdrId(mdrId);
        csvLine.setAttributeValue(value);
        csvLine.setValid(qualityResult.isValid());
        csvLine.setNumberOfPatients(qualityResult.getNumberOfPatients());
        csvLine.setMdrLink(getExcelHyperlink(getMdrLink(mdrId)));
        csvLine.setMdrName(getMdrName(mdrId));
        csvLine.setMdrType(getMdrType(mdrId));

        return csvLine.createLine();

    }

    private String getMdrName (MdrIdDatatype mdrId) throws QualityResultCsvLineManagerException {

        String mdrName = mdrNames.get(mdrId);
        if (mdrName == null){
            mdrName = createMdrName(mdrId);
            mdrNames.put(mdrId, mdrName);
        }

        return mdrName;

    }

    private String createMdrName (MdrIdDatatype mdrId) throws QualityResultCsvLineManagerException {

        try {

            return createMdrNameWithoutExceptions(mdrId);

        } catch (MdrConnectionException | ExecutionException | MdrInvalidResponseException e) {
            throw new QualityResultCsvLineManagerException(e);
        }

    }

    private String createMdrNameWithoutExceptions(MdrIdDatatype mdrId) throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        Definition definition = mdrClient.getDataElementDefinition(mdrId.toString(), languageCode);
        ArrayList<Record> designations = definition.getDesignations();
        if (designations != null && designations.size() > 0){
            Record record = designations.get(0);
            return getMdrName(record);
        }

        return null;

    }

    private String getMdrName (Record record){
        return (record != null) ? record.getDesignation() : null;
    }

    private String getMdrType (MdrIdDatatype mdrId){

        Validations validations = modelSearcher.getValidations(mdrId);
        return  (validations != null) ? validations.getDatatype(): null;

    }

    private String getExcelHyperlink (String link){
        return EXCEL_HYPERLINK_PREFIX + link + EXCEL_HYPERLINK_SUFFIX;
    }

    private String getMdrLink(MdrIdDatatype mdrId){
        return MDR_LINK_PREFIX + mdrId;
    }

    @Override
    public QualityResults parseLineAndAddToQualityResults(String line, QualityResults qualityResults) {

        PatientDataCsvLinePattern_Test1 csvLine = new PatientDataCsvLinePattern_Test1();

        csvLine.parseValuesOfLine(line);

        MdrIdDatatype mdrId = csvLine.getMdrId();
        String attributeValue = csvLine.getAttributeValue();
        Integer numberOfPatients = csvLine.getNumberOfPatients();
        Boolean valid = csvLine.isValid();

        if (mdrId != null && attributeValue != null && valid != null && numberOfPatients != null){

            QualityResult qualityResult = new QualityResult();

            qualityResult.setValid(valid);
            qualityResult.setNumberOfPatients(numberOfPatients);

            qualityResults.put(mdrId, attributeValue, qualityResult);

        }

        return qualityResults;

    }

}
