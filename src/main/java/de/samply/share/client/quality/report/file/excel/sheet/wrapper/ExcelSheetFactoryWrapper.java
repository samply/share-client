package de.samply.share.client.quality.report.file.excel.sheet.wrapper;/*
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

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactoryException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelSheetFactoryWrapper implements ExcelSheetFactory {

    private ExcelSheetFactory excelSheetFactory;

    public ExcelSheetFactoryWrapper(ExcelSheetFactory excelSheetFactory) {
        this.excelSheetFactory = excelSheetFactory;
    }

    protected abstract XSSFSheet addFunctionalityToSheet(XSSFSheet sheet);

    @Override
    public XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle, ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

        workbook = excelSheetFactory.addSheet(workbook, sheetTitle, excelRowContext);
        workbook = addFunctionalityToSheet(workbook, sheetTitle);

        return workbook;
    }

    private XSSFWorkbook addFunctionalityToSheet(XSSFWorkbook workbook, String sheetTitle){

        XSSFSheet sheet = workbook.getSheet(sheetTitle);
        addFunctionalityToSheet(sheet);

        return workbook;

    }






}
