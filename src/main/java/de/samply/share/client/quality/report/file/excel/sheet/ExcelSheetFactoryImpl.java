package de.samply.share.client.quality.report.file.excel.sheet;/*
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


import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSheetFactoryImpl implements ExcelSheetFactory {

    private ExcelRowFactory excelRowFactory;
    protected static final Logger logger = LogManager.getLogger(ExcelSheetFactoryImpl.class);


    public ExcelSheetFactoryImpl(ExcelRowFactory excelRowFactory) {
        this.excelRowFactory = excelRowFactory;
    }

    @Override
    public XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle, ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

        XSSFSheet sheet = workbook.createSheet(sheetTitle);
        sheet = addRowTitles(sheet, excelRowContext);

        int maxNumberOfRows = SpreadsheetVersion.EXCEL2007.getMaxRows();


        int numberOfRows = excelRowContext.getNumberOfRows();
        PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfRows, "adding rows...");

        for (ExcelRowElements excelRowElements : excelRowContext){

            percentageLogger.incrementCounter();
            addRow(sheet, excelRowElements);

            maxNumberOfRows--;
            if (maxNumberOfRows <= 0) break;
        }

        return workbook;

    }

    private XSSFSheet addRowTitles (XSSFSheet sheet, ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

        try {

            return excelRowFactory.addRowTitles(sheet,excelRowContext);

        } catch (ExcelRowFactoryException e) {
            throw new ExcelSheetFactoryException(e);
        }

    }

    private XSSFSheet addRow (XSSFSheet sheet, ExcelRowElements excelRowElements) throws ExcelSheetFactoryException {

        try {

            return excelRowFactory.addRow(sheet, excelRowElements);

        } catch (ExcelRowFactoryException e) {
            throw new ExcelSheetFactoryException(e);
        }

    }


}
