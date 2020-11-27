package de.samply.share.client.quality.report.file.excel.row.factory;/*
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

import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.usermodel.*;

public class ExcelRowFactoryImpl implements ExcelRowFactory {

    private StringExcelCellElement emptyElement = new StringExcelCellElement("");




    @Override
    public XSSFSheet addRowTitles(XSSFSheet sheet, ExcelRowContext excelRowContext) throws ExcelRowFactoryException {

        ExcelRowElements emptyExcelRowElements = excelRowContext.createEmptyExcelRowElements();

        int rowNum = 0;

        XSSFRow row = sheet.createRow(rowNum);

        for (int i = 0; i < emptyExcelRowElements.getMaxNumberOfElements() && i < SpreadsheetVersion.EXCEL2007.getMaxColumns(); i++){

            ExcelCellElement title = emptyExcelRowElements.getElementTitle(i);
            title.addAsCell(row);

        }

        setTitleRowStyle(row);

        return sheet;

    }

    protected XSSFRow setTitleRowStyle (XSSFRow titleRow){

        XSSFCellStyle cellStyle = getWorkbook(titleRow).createCellStyle();
        XSSFFont font = getWorkbook(titleRow).createFont();
        font.setBold(true);

        cellStyle.setFont(font);

        for (int i=0; i< titleRow.getLastCellNum(); i++){
            XSSFCell cell = titleRow.getCell(i);
            cell.setCellStyle(cellStyle);
        }

        return titleRow;

    }

    private XSSFWorkbook getWorkbook (XSSFRow row){
        return row.getSheet().getWorkbook();
    }

    @Override
    public XSSFSheet addRow(XSSFSheet sheet, ExcelRowElements excelRowElements) throws ExcelRowFactoryException {

        int rowNum = sheet.getLastRowNum() + 1;

        XSSFRow row = sheet.createRow(rowNum);

        addElementsToRow(row, excelRowElements);

        return sheet;
    }

    private XSSFRow addElementsToRow (XSSFRow row, ExcelRowElements elements){

        for (int i = 0; i < elements.getMaxNumberOfElements() && i < SpreadsheetVersion.EXCEL2007.getMaxColumns(); i++){

            ExcelCellElement element = elements.getElement(i);

            if (element != null){
                element.addAsCell(row);
            } else{
                emptyElement.addAsCell(row);
            }

        }

        return row;
    }

}
