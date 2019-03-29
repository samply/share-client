package de.samply.share.client.quality.report.file.excel.instances.patientids;/*
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

import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.excel.instances.basic.BasicExcelColumnMetaInfo;
import de.samply.share.client.quality.report.file.excel.instances.basic.BasicExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class PatientIdsExcelRowContext implements ExcelRowContext {

    private String mdrLinkPrefix;
    private Integer maxNumberOfPatientIdsToBeShown;

    private PatientIdsList patientIdsList = new PatientIdsList();
    private List<BasicExcelColumnMetaInfo> metaInfos = new ArrayList<>();

    protected static final Logger logger = LogManager.getLogger(PatientIdsExcelRowContext.class);

    protected abstract Collection<String> getPatientIds (QualityResult qualityResult);

    public Integer getNumberOfRows(){
        return patientIdsList.getMaxNumberOfPatientsOfAllPatientLists();
    }

    public PatientIdsExcelRowContext(AlphabeticallySortedMismatchedQualityResults qualityResults) {

        this.mdrLinkPrefix = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MDR_LINK_PREFIX);
        String sMaxNumberOfPatientIdsToBeShown = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MAX_NUMBER_OF_PATIENT_IDS_TO_BE_SHOWN);
        maxNumberOfPatientIdsToBeShown = convert(sMaxNumberOfPatientIdsToBeShown);

        createQualityResultList(qualityResults);

    }

    private Integer convert (String number){

        try {
            return Integer.valueOf(number);
        }catch (Exception e){
            return null;
        }

    }

    private void createQualityResultList (AlphabeticallySortedMismatchedQualityResults qualityResults){


        int counter = 0;

        int numberOfQualityResults = getNumberOfQualityResults(qualityResults);
        PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfQualityResults, "analyzing quality results");

        for (QualityResult qualityResult : qualityResults){

            percentageLogger.incrementCounter();

            MdrIdDatatype mdrId = qualityResults.getMdrId(counter);
            String value = qualityResults.getValue(counter);

            addMetaInfo(mdrId, value);
            addPatientIdsToList(qualityResult);

            counter++;

        }

    }

    private int getNumberOfQualityResults(QualityResults qualityResults){

        int counter = 0;

        for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){
            counter += qualityResults.getValues(mdrId).size();
        }

        return counter;

    }

    private void addPatientIdsToList (QualityResult qualityResult){

        Collection<String> patientIds = getPatientIds(qualityResult);

        if (maxNumberOfPatientIdsToBeShown != null && maxNumberOfPatientIdsToBeShown > 0) {

            List<String> reducedPatientIds = new ArrayList<>();

            int counter = 0;
            for (String patientId : patientIds){
                reducedPatientIds.add(patientId);
                counter ++;
                if (counter == maxNumberOfPatientIdsToBeShown){
                    break;
                }
            }

            patientIds = reducedPatientIds;

        }

        patientIdsList.addList(patientIds);

    }

    private void addMetaInfo (MdrIdDatatype mdrId, String value){

        BasicExcelColumnMetaInfo metaInfo = new BasicExcelColumnMetaInfo();
        String title = getTitle(mdrId, value);
        String link = getMdrLink(mdrId);

        metaInfo.setTitle(title);
        metaInfo.setLink(link);

        metaInfos.add(metaInfo);

    }

    private String getTitle (MdrIdDatatype mdrId, String value){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(mdrId.getNamespace());
        stringBuilder.append(':');
        stringBuilder.append(mdrId.getId());
        stringBuilder.append(':');
        stringBuilder.append(mdrId.getVersion());
        stringBuilder.append(':');
        stringBuilder.append(value);

        return stringBuilder.toString();

    }

    private String getMdrLink(MdrIdDatatype mdrId){
        return mdrLinkPrefix + mdrId;
    }




    @Override
    public ExcelRowElements createEmptyExcelRowElements() {
        return new BasicExcelRowElements(metaInfos);
    }

    private class PatientIdsExcelContextIterator implements Iterator<ExcelRowElements> {

        private Iterator<List<String>> iterator;

        public PatientIdsExcelContextIterator() {
            iterator = patientIdsList.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public ExcelRowElements next() {

            List<String> next = iterator.next();

            return createExcelRowElements(next);

        }

        private ExcelRowElements createExcelRowElements (List<String> myList){

            BasicExcelRowElements excelRowElements = new BasicExcelRowElements(metaInfos);

            for (String element : myList){
                excelRowElements.addElement(element);
            }

            return excelRowElements;

        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    @Override
    public Iterator<ExcelRowElements> iterator() {
        return new PatientIdsExcelContextIterator();
    }


}
