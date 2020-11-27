package de.samply.share.client.quality.report.file.excel.utils;/*
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

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelUtils {

    public static CellRangeAddress getAllSheetRange(XSSFSheet sheet){

        int firstRow = 0;
        int firstCol = 0;

        int lastRow = sheet.getLastRowNum();
        XSSFRow row = sheet.getRow(lastRow);
        int lastCol = row.getLastCellNum();

        if (lastRow <= firstRow){
            lastRow = firstRow + 1;
        }
        if (lastCol <= firstCol){
            lastCol = firstCol + 1;
        }

        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        cellRangeAddress = controlLimits(cellRangeAddress);

        return cellRangeAddress;

    }

    private static CellRangeAddress controlLimits (CellRangeAddress cellRangeAddress){

        if (cellRangeAddress != null){

            int lastColumn = cellRangeAddress.getLastColumn();
            int lastRow = cellRangeAddress.getLastRow();

            if (lastColumn >= SpreadsheetVersion.EXCEL2007.getMaxColumns()){
                cellRangeAddress.setLastColumn(SpreadsheetVersion.EXCEL2007.getMaxColumns() - 1);
            }

            if (lastRow >= SpreadsheetVersion.EXCEL2007.getMaxRows()){
                cellRangeAddress.setLastRow(SpreadsheetVersion.EXCEL2007.getMaxRows() - 1);
            }

        }

        return cellRangeAddress;

    }


}
