package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;/*
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


public class DataElementStats_ExcelRowMapper {


    private ExcelRowMapperUtils excelRowMapperUtils;
    private DktkId_MdrId_Converter dktkIdManager;
    private CentraXxMapper centraXxMapper;

    public DataElementStats_ExcelRowMapper(DktkId_MdrId_Converter dktkIdManager, ExcelRowMapperUtils excelRowMapperUtils, CentraXxMapper centraXxMapper) {

        this.excelRowMapperUtils = excelRowMapperUtils;
        this.dktkIdManager = dktkIdManager;
        this.centraXxMapper = centraXxMapper;

    }

    public DataElementStats_ExcelRowElements convert (DataElementStats_ExcelRowParameters excelRowParameters) throws ExcelRowMapperException {

        DataElementStats_ExcelRowElements excelRowElements = new DataElementStats_ExcelRowElements();


        QualityResultsStatistics qualityResultsStatistics = excelRowParameters.getQualityResultsStatistics();
        MdrIdDatatype mdrId = excelRowParameters.getMdrId();

        String dktkId = dktkIdManager.getDktkId(mdrId);;
        String mdrDataElement = excelRowMapperUtils.getMdrDatenElement(mdrId);
        String cxxDatenElement = centraXxMapper.getCentraXxAttribute(mdrId);
        String mdrLink = excelRowMapperUtils.getMdrLink(mdrId);

        int numberOf_patientsWithMdrId = qualityResultsStatistics.getNumberOf_PatientsWithMdrId(mdrId);
        double percentageOf_patientsWithMdrId_outOf_totalPatients = qualityResultsStatistics.getPercentageOf_PatientsWithMdrId_outOf_TotalPatients(mdrId);
        int numberOf_patientsWithMatchOnlyWithMdrId = qualityResultsStatistics.getNumberOf_PatientsWithMatchOnlyWithMdrId(mdrId);
        double percentageOf_patientsWithMatchOnlyWithMdrId_outOf_patientsWithMdrId = qualityResultsStatistics.getPercentageOf_PatientsWithMatchOnlyWithMdrId_outOf_PatientsWithMdrId(mdrId);
        double percentageOf_patitentsWithMatchOnlyWithMdrId_outOf_totalPatients = qualityResultsStatistics.getPercentageOf_PatitentsWithMatchOnlyWithMdrId_outOf_TotalPatients(mdrId);
        int numberOf_patientsWithAnyMismatchWithMdrId = qualityResultsStatistics.getNumberOf_PatientsWithAnyMismatchWithMdrId(mdrId);
        double percentageOf_patientsWithAnyMismatchWithMdrId_outOf_patientsWithMdrId = qualityResultsStatistics.getPercentageOf_PatientsWithAnyMismatchWithMdrId_outOf_PatientsWithMdrId(mdrId);
        double percentageOf_patientsWithAnyMismatchWithMdrId_outOf_totalPatients = qualityResultsStatistics.getPercentageOf_PatientsWithAnyMismatchWithMdrId_outOf_TotalPatients(mdrId);

        excelRowElements.setDktkId(dktkId);
        excelRowElements.setMdrDatenElement(mdrDataElement);
        excelRowElements.setCxxDatenElement(cxxDatenElement);
        excelRowElements.setMdrLink(mdrLink, mdrId);

        excelRowElements.setNumberOf_PatientsWithDataElement(numberOf_patientsWithMdrId);
        excelRowElements.setPercentageOf_PatientsWithDataElement_OutOf_TotalPatients(percentageOf_patientsWithMdrId_outOf_totalPatients);
        excelRowElements.setNumberOf_PatientsWithMatchOnlyForDataElement(numberOf_patientsWithMatchOnlyWithMdrId);
        excelRowElements.setPercentageOf_PatientsWithMatchOnlyForDataElement_OutOf_PatientsWithDataElement(percentageOf_patientsWithMatchOnlyWithMdrId_outOf_patientsWithMdrId);
        excelRowElements.setPercentageOf_PatientsWithMatchOnlyForDataElement_OutOf_TotalPatients(percentageOf_patitentsWithMatchOnlyWithMdrId_outOf_totalPatients);
        excelRowElements.setNumberOf_PatientsWithAnyMismatchForDataElement(numberOf_patientsWithAnyMismatchWithMdrId);
        excelRowElements.setPercentageOf_PatientsWithAnyMismatchForDataElement_OutOf_PatientsWithDataElement(percentageOf_patientsWithAnyMismatchWithMdrId_outOf_patientsWithMdrId);
        excelRowElements.setPercentageOf_PatientsWithAnyMismatchForDataElement_outOf_totalPatients(percentageOf_patientsWithAnyMismatchWithMdrId_outOf_totalPatients);


        return excelRowElements;

    }




}
