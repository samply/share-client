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

import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyle;
import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyleImpl;
import de.samply.share.client.quality.report.file.excel.cell.style.GreenBackgroundCellStyle;

public class FormattedDataElementStats_ExcelRowElements extends DataElementStats_ExcelRowElements {

    @Override
    public void setGeneralRehearsal_A_ContainedInQR (boolean value){

        super.setGeneralRehearsal_A_ContainedInQR(value);

        if (value) {
            ExcelCellStyle excelCellStyle = new ExcelCellStyleImpl();
            excelCellStyle = new GreenBackgroundCellStyle(excelCellStyle);

            addExcelCellStyle(excelCellStyle, ELEMENT_ORDER.GENERAL_REHEARSAL_A_CONTAINED_IN_QR.ordinal());
        }


    }

    @Override
    public void setGeneralRehearsal_B_LowMismatch (boolean value){

        super.setGeneralRehearsal_B_LowMismatch(value);

        if (value) {
            ExcelCellStyle excelCellStyle = new ExcelCellStyleImpl();
            excelCellStyle = new GreenBackgroundCellStyle(excelCellStyle);

            addExcelCellStyle(excelCellStyle, ELEMENT_ORDER.GENERAL_REHEARSAL_B_LOW_MISMATCH.ordinal());
        }

    }

    @Override
    public void setGeneralRehearsal_A_And_B (boolean value){

        super.setGeneralRehearsal_A_And_B(value);

        if (value) {
            ExcelCellStyle excelCellStyle = new ExcelCellStyleImpl();
            excelCellStyle = new GreenBackgroundCellStyle(excelCellStyle);

            addExcelCellStyle(excelCellStyle, ELEMENT_ORDER.GENERAL_REHEARSAL_A_AND_B.ordinal());
        }

    }



}
