package de.samply.share.client.quality.report.file.manager;/*
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

import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactory;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryException;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.results.QualityResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelQualityReportFileManager<I extends ExcelPattern> extends QualityReportFileManagerImpl{

    private ExcelWorkbookFactory excelWorkbookFactory;
    protected static final Logger logger = LogManager.getLogger(ExcelQualityReportFileManager.class);

    public ExcelQualityReportFileManager(I excelPattern, IdPathManagerImpl<?,I,?> idPathManager) {

        super(idPathManager);
        this.excelWorkbookFactory = excelPattern.createExcelWorkbookFactory();

    }


    @Override
    public void writeFile(QualityResults qualityResults, String fileId) throws QualityReportFileManagerException {

        try {
            writeFileWithoutExceptions(qualityResults, fileId);
        } catch (ExcelWorkbookFactoryException e) {
            throw new QualityReportFileManagerException(e);
        }

    }

    private void writeFileWithoutExceptions(QualityResults qualityResults, String fileId) throws QualityReportFileManagerException, ExcelWorkbookFactoryException {


        logger.info("Getting file path");
        String filePath = idPathManager.getExcelFilePath(fileId);
        logger.info ("creating workbook");
        XSSFWorkbook workbook = excelWorkbookFactory.createWorkbook(qualityResults);
        logger.info("writing workbook");
        writeWorkbook(workbook, filePath);
        logger.info("workbook was written");

    }

    private void writeWorkbook (XSSFWorkbook workbook, String filePath) throws QualityReportFileManagerException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath))){

            workbook.write(fileOutputStream);

        } catch (IOException e) {
            throw new QualityReportFileManagerException(e);
        }

    }

    @Override
    public QualityResults readFile(String fileId) throws QualityReportFileManagerException {

        //TODO
        throw new QualityReportFileManagerException(new UnsupportedOperationException());

    }

}
