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
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;

public class Table2_ExcelRowContextFactory {


    private Table2_ExcelRowMapper excelRowMapper;


    public Table2_ExcelRowContextFactory(ExcelRowMapperUtils excelRowMapperUtils, DktkId_MdrId_Converter dktkIdManager, CentraXxMapper centraXxMapper) {

        this.excelRowMapper = new Table2_ExcelRowMapper(dktkIdManager, excelRowMapperUtils, centraXxMapper);

    }


    public Table2_ExcelRowContext createExcelRowContext (QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics){

        return new Table2_ExcelRowContext(qualityResults, qualityResultsStatistics, excelRowMapper);

    }

}
