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

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextImpl;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DataElementStats_ExcelRowContext extends ExcelRowContextImpl<DataElementStats_ExcelRowParameters> {


    protected static final Logger logger = LogManager.getLogger(DataElementStats_ExcelRowContext.class);
    private DataElementStats_ExcelRowMapper excelRowMapper;


    public DataElementStats_ExcelRowContext(QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics, DataElementStats_ExcelRowMapper excelRowMapper) {

        this.excelRowMapper = excelRowMapper;
        fillOutExcelRowParametersList(qualityResults, qualityResultsStatistics);

    }

    private void fillOutExcelRowParametersList (QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics){

        PercentageLogger percentageLogger = new PercentageLogger(logger, qualityResults.getMdrIds().size(), "analyzing quality results");

        for (MdrIdDatatype mdrId : qualityResults.getMdrIds()){

            percentageLogger.incrementCounter();
            DataElementStats_ExcelRowParameters excelRowParameters = createRowParameters(mdrId, qualityResultsStatistics);
            excelRowParametersList.add(excelRowParameters);

        }

    }

    private DataElementStats_ExcelRowParameters createRowParameters(MdrIdDatatype mdrId, QualityResultsStatistics qualityResultStatistics){

        DataElementStats_ExcelRowParameters excelRowParameters = new DataElementStats_ExcelRowParameters();

        excelRowParameters.setMdrId(mdrId);
        excelRowParameters.setQualityResultsStatistics(qualityResultStatistics);

        return excelRowParameters;

    }


    @Override
    protected ExcelRowElements convert(DataElementStats_ExcelRowParameters table2_excelRowParameters) throws Exception {
        return excelRowMapper.convert(table2_excelRowParameters);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public ExcelRowElements createEmptyExcelRowElements() {
        return new DataElementStats_ExcelRowElements();
    }

}
