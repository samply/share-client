package de.samply.share.client.quality.report.file.excel.instances.statistics.table1;/*
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

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table1_ExcelRowContext implements ExcelRowContext {


    private final static String PERCENTAGE_OF_COMPLETELY_MATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS = "Alle OK";
    private final static String PERCENTAGE_OF_NOT_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS = "Mit Fehlern";
    private final static String PERCENTAGE_OF_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS = "Nur Fehlerhaft";
    private final static String PERCENTAGE_OF_NOT_MAPPED_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS = "Nicht vorhanden";

    private Table1_ExcelRowMapper excelRowMapper = new Table1_ExcelRowMapper();
    private List<ExcelRowElements> excelRowElementsList = new ArrayList<>();


    public Table1_ExcelRowContext(QualityResultsStatistics qualityResultsStatistics) {
        fillExcelRowElementsList(qualityResultsStatistics);
    }

    private void fillExcelRowElementsList(QualityResultsStatistics qualityResultsStatistics){

        double percentageOf_completelyMatchingDataelements_outOf_allDataelements = qualityResultsStatistics.getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements();
        ExcelRowElements excelRowElements1 = excelRowMapper.createExcelRowElements(PERCENTAGE_OF_COMPLETELY_MATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS, percentageOf_completelyMatchingDataelements_outOf_allDataelements);

        double percentageOf_notCompletelyMismatchingDataelements_outOf_allDataelements = qualityResultsStatistics.getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements();
        ExcelRowElements excelRowElements2 = excelRowMapper.createExcelRowElements(PERCENTAGE_OF_NOT_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS, percentageOf_notCompletelyMismatchingDataelements_outOf_allDataelements);

        double percentageOf_completelyMismatchingDataelements_outOf_allDataelements = qualityResultsStatistics.getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements();
        ExcelRowElements excelRowElements3 = excelRowMapper.createExcelRowElements(PERCENTAGE_OF_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS, percentageOf_completelyMismatchingDataelements_outOf_allDataelements);

        double percentageOf_notMappedDataelements_outOf_allDataelements = qualityResultsStatistics.getPercentageOf_NotMappedDataelements_outOf_AllDataelements();
        ExcelRowElements excelRowElements4 = excelRowMapper.createExcelRowElements(PERCENTAGE_OF_NOT_MAPPED_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS, percentageOf_notMappedDataelements_outOf_allDataelements);



        excelRowElementsList.add(excelRowElements1);
        excelRowElementsList.add(excelRowElements2);
        excelRowElementsList.add(excelRowElements3);
        excelRowElementsList.add(excelRowElements4);

    }

    @Override
    public ExcelRowElements createEmptyExcelRowElements() {
        return new Table1_ExcelRowElements();
    }

    @Override
    public Integer getNumberOfRows() {
        return excelRowElementsList.size();
    }

    @Override
    public Iterator<ExcelRowElements> iterator() {
        return excelRowElementsList.iterator();
    }

}
