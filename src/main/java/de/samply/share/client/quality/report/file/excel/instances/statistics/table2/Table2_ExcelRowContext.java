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

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextImpl;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Table2_ExcelRowContext extends ExcelRowContextImpl<Table2_ExcelRowParameters> {


    protected static final Logger logger = LogManager.getLogger(Table2_ExcelRowContext.class);
    private Table2_ExcelRowMapper excelRowMapper;


    public Table2_ExcelRowContext(QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics, Table2_ExcelRowMapper excelRowMapper) {

        this.excelRowMapper = excelRowMapper;
        fillOutExcelRowParametersList(qualityResults, qualityResultsStatistics);

    }

    private void fillOutExcelRowParametersList (QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics){

        for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){

            Table2_ExcelRowParameters excelRowParameters = createRowParameters(mdrId, qualityResultsStatistics);
            excelRowParametersList.add(excelRowParameters);

        }

    }

    private Table2_ExcelRowParameters createRowParameters(MdrIdDatatype mdrId, QualityResultsStatistics qualityResultStatistics){

        Table2_ExcelRowParameters excelRowParameters = new Table2_ExcelRowParameters();

        excelRowParameters.setMdrId(mdrId);
        excelRowParameters.setQualityResultsStatistics(qualityResultStatistics);

        return excelRowParameters;

    }


    @Override
    protected ExcelRowElements convert(Table2_ExcelRowParameters table2_excelRowParameters) throws Exception {
        return excelRowMapper.convert(table2_excelRowParameters);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public ExcelRowElements createEmptyExcelRowElements() {
        return new Table2_ExcelRowElements();
    }

}
