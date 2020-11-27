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

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.downloader.FileDownloaderException;
import de.samply.share.client.quality.report.file.downloader.ExplanatoryExcelFileDownloader;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;


public class ExplanatoryExcelSheetFactory implements ExcelSheetFactory {


    protected static final Logger logger = LogManager.getLogger(ExplanatoryExcelSheetFactory.class);
    //private ExplanatoryExcelFileDownloader explanatoryExcelFileDownloader = new ExplanatoryExcelFileDownloader();


    @Override
    public XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle, ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {
        return addSheet();
    }

    private XSSFWorkbook addSheet() throws ExcelSheetFactoryException {

        //downloadExcelInfoFile();
        //File explanatoryExcelFile = new File (explanatoryExcelFileDownloader.getFilePath());
        File explanatoryExcelFile = getExplanatoryExcelFile ();
        return readWorkbook(explanatoryExcelFile);

    }

    private File getExplanatoryExcelFile () throws ExcelSheetFactoryException {
        try {

            return getExplanatoryExcelFile_withoutExceptionManagement();

        }catch (Exception e){
            throw new ExcelSheetFactoryException(e);
        }
    }

    private File getExplanatoryExcelFile_withoutExceptionManagement () throws URISyntaxException {

        String filename = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_EXCEL_INFO_FILENAME);

        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(filename).toURI());
    }


//    private void downloadExcelInfoFile(){
//
//        try {
//
//            logger.info("downloading explanatory excel file");
//            explanatoryExcelFileDownloader.download();
//
//        } catch (FileDownloaderException e) {
//            logger.error(e);
//        }
//
//    }


    private XSSFWorkbook readWorkbook(File explanatoryExcelFile) throws ExcelSheetFactoryException {

        try (FileInputStream fileInputStream = new FileInputStream(explanatoryExcelFile)){

            logger.info("reading explanatory excel file");
            return new XSSFWorkbook(fileInputStream);

        } catch (IOException e) {
            throw new ExcelSheetFactoryException(e);
        }

    }


}
