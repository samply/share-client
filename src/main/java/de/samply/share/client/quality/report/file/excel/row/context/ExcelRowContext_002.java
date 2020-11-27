package de.samply.share.client.quality.report.file.excel.row.context;/*
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

import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperException;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements_002;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapper_002;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExcelRowContext_002 extends ExcelRowContextImpl<ExcelRowParameters_002>{

    protected static final Logger logger = LogManager.getLogger(ExcelRowContext_002.class);

    private ExcelRowMapper_002 excelRowMapper;
    private AlphabeticallySortedMismatchedQualityResults asmQualityResults;


    public ExcelRowContext_002(ExcelRowMapper_002 excelRowMapper, QualityResults qualityResults) {
        this (excelRowMapper, qualityResults, null, null);
    }

    public ExcelRowContext_002(ExcelRowMapper_002 excelRowMapper, QualityResults qualityResults, AlphabeticallySortedMismatchedQualityResults asmQualityResults, QualityResultsStatistics qualityResultsStatistics) {

        this.excelRowMapper = excelRowMapper;
        this.asmQualityResults = asmQualityResults;
        fillOutExcelRowParametersList(qualityResults, qualityResultsStatistics);

    }

    private void fillOutExcelRowParametersList (QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics){



        for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){

            for (String value : qualityResults.getValues(mdrId)){

                QualityResult qualityResult = qualityResults.getResult(mdrId, value);

                ExcelRowParameters_002 excelRowParameters = createRowParameters(mdrId, value, qualityResult, qualityResultsStatistics);
                excelRowParametersList.add(excelRowParameters);

            }

        }

    }

    private ExcelRowParameters_002 createRowParameters (MdrIdDatatype mdrId, String value, QualityResult qualityResult, QualityResultsStatistics qualityResultsStatistics){

        ExcelRowParameters_002 rowParameters = new ExcelRowParameters_002();
        Integer mismatchOrdinal = getMismatchOrdinal(mdrId, value);
        double percentageOf_patientsWithValue_outOf_patientsWithMdrId = qualityResultsStatistics.getPercentageOf_PatientsWithValue_outOf_PatientsWithMdrId(mdrId, value);
        double percentageOf_patientsWithValue_outOf_totalPatients = qualityResultsStatistics.getPercentageOf_PatientsWithValue_outOf_TotalPatients(mdrId, value);


        rowParameters.setMdrId(mdrId);
        rowParameters.setValue(value);
        rowParameters.setQualityResult(qualityResult);
        rowParameters.setMismatchOrdinal(mismatchOrdinal);
        rowParameters.setPercentageOutOfPatientWithDataElement(percentageOf_patientsWithValue_outOf_patientsWithMdrId);
        rowParameters.setPercentageOutOfTotalPatients(percentageOf_patientsWithValue_outOf_totalPatients);

        return rowParameters;

    }

    private Integer getMismatchOrdinal (MdrIdDatatype mdrId, String value){

        if (asmQualityResults == null) return null;
        Integer ordinal = asmQualityResults.getOrdinal(mdrId, value);
        return (ordinal < 0) ? null : ordinal;

    }

    @Override
    public ExcelRowElements createEmptyExcelRowElements() {
        return new ExcelRowElements_002();
    }


    @Override
    protected ExcelRowElements convert(ExcelRowParameters_002 excelRowParameters_002) throws Exception {
        try {
            return excelRowMapper.createExcelRowElements(excelRowParameters_002);
        } catch (ExcelRowMapperException e) {
            throw new Exception (e);
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
