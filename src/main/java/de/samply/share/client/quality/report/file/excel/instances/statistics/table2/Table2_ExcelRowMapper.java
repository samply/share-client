package de.samply.share.client.quality.report.file.excel.instances.statistics.table2;/*
 * Copyright (C) 2018 Medizinische Informatik in der Translationalen Onkologie,
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

import de.samply.share.client.quality.report.centraxx.CentraXxMapper;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperException;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;




public class Table2_ExcelRowMapper {


    private ExcelRowMapperUtils excelRowMapperUtils;
    private DktkId_MdrId_Converter dktkIdManager;
    private CentraXxMapper centraXxMapper;

    public Table2_ExcelRowMapper(DktkId_MdrId_Converter dktkIdManager, ExcelRowMapperUtils excelRowMapperUtils, CentraXxMapper centraXxMapper) {

        this.excelRowMapperUtils = excelRowMapperUtils;
        this.dktkIdManager = dktkIdManager;
        this.centraXxMapper = centraXxMapper;

    }

    public Table2_ExcelRowElements convert (Table2_ExcelRowParameters excelRowParameters) throws ExcelRowMapperException {

        Table2_ExcelRowElements excelRowElements = new Table2_ExcelRowElements();


        QualityResultsStatistics qualityResultsStatistics = excelRowParameters.getQualityResultsStatistics();
        MdrIdDatatype mdrId = excelRowParameters.getMdrId();

        String dktkId = dktkIdManager.getDktkId(mdrId);;
        String mdrDataElement = excelRowMapperUtils.getMdrDatenElement(mdrId);
        String cxxDatenElement = centraXxMapper.getCentraXxAttribute(mdrId);
        String mdrLink = excelRowMapperUtils.getMdrLink(mdrId);


        int patientsForId = qualityResultsStatistics.getNumberOf_PatientsWithMdrId(mdrId);
        int patientsForValidation = qualityResultsStatistics.getNumberOf_PatientsForValidation(mdrId);
        Double ratio = (patientsForValidation > 0) ? 100.0d * (double) patientsForValidation / (double) patientsForId : 0;

        int numberOf_matchingPatientsWithMdrId = qualityResultsStatistics.getNumberOf_MatchingPatientsWithMdrId(mdrId);
        int numberOf_mismatchingPatientsWithMdrId = qualityResultsStatistics.getNumberOf_MismatchingPatientsWithMdrId(mdrId);

        double percentageOf_matchingPatientsWithMdrId_outOf_patientsWithMdrId = qualityResultsStatistics.getPercentageOf_MatchingPatientsWithMdrId_outOf_PatientsWithMdrId(mdrId);
        double percentageOf_mismatchingPatientsWithMdrId_outOf_patientsWithMdrId = qualityResultsStatistics.getPercentageOf_MismatchingPatientsWithMdrId_outOf_PatientsWithMdrId(mdrId);

        double percentageOfPatientsOutOfTotalNumberOfPatientsForADataelement = qualityResultsStatistics.getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement(mdrId);


        excelRowElements.setDktkId(dktkId);
        excelRowElements.setMdrDatenElement(mdrDataElement);
        excelRowElements.setCxxDatenElement(cxxDatenElement);
        excelRowElements.setMdrLink(mdrLink, mdrId);
        excelRowElements.setPatientsForId(patientsForId);
        excelRowElements.setPatientsForValidation(patientsForValidation);
        excelRowElements.setRatio(ratio);
        excelRowElements.setNumberOfPatientsWithMatch(numberOf_matchingPatientsWithMdrId);
        excelRowElements.setNumberOfPatientsWithMismatch(numberOf_mismatchingPatientsWithMdrId);
        excelRowElements.setPercentageOfPatientsWithMatch(percentageOf_matchingPatientsWithMdrId_outOf_patientsWithMdrId);
        excelRowElements.setPercentageOfPatientsWithMismatch(percentageOf_mismatchingPatientsWithMdrId_outOf_patientsWithMdrId);
        excelRowElements.setPercentageOfTotalPatients(percentageOfPatientsOutOfTotalNumberOfPatientsForADataelement);


        return excelRowElements;

    }




}
