package de.samply.share.client.quality.report.file.excel.workbook;/*
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


import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientDktkIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientLocalIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats.DataElementStats_ExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextFactory_002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactoryException;
import de.samply.share.client.quality.report.file.excel.sheet.ExplanatoryExcelSheetFactory;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.QualityResultsSortedMdrIdsByDktkIdFilter;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidDateFilter;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidIntegerFilter;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidStringFilter;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatisticsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWorkbookFactoryImpl_002 implements ExcelWorkbookFactory {


    private ExcelRowContextFactory_002 excelRowContextFactory;

    private DataElementStats_ExcelRowContextFactory dataElementStats_excelRowContextFactory;

    private PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory;
    private PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory;


    private ExcelSheetFactory excelSheetFactory;
    private ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory;
    private ModelSearcher modelSearcher;
    private DktkId_MdrId_Converter dktkIdManager;
    private MdrMappedElements mdrMappedElements;

    public final static String allElements_sheetTitle = "all elements";
    public final static String filteredElements_sheetTitle = "filtered elements";
    public final static String patientLocalIds_sheetTitle = "patient local ids";
    public final static String patientDktkIds_sheetTitle = "patient dktk ids";
    public final static String dataElementStatistics = "data element stats";

    protected static final Logger logger = LogManager.getLogger(ExcelWorkbookFactoryImpl_002.class);




    public ExcelWorkbookFactoryImpl_002(ExcelWorkbookFactoryParameters_002 parameters) {

        this.excelSheetFactory = parameters.getExcelSheetFactory();
        this.patientLocalIdsExcelRowContextFactory = parameters.getPatientLocalIdsExcelRowContextFactory();
        this.patientDktkIdsExcelRowContextFactory= parameters.getPatientDktkIdsExcelRowContextFactory();

        this.explanatoryExcelSheetFactory = parameters.getExplanatoryExcelSheetFactory();
        this.modelSearcher = parameters.getModelSearcher();

        this.dktkIdManager = parameters.getDktkIdManager();
        this.excelRowContextFactory = parameters.getExcelRowContextFactory();
        this.dataElementStats_excelRowContextFactory = parameters.getDataElementStats_excelRowContextFactory();

        this.mdrMappedElements = parameters.getMdrMappedElements();

    }

    @Override
    public XSSFWorkbook createWorkbook(QualityResults qualityResults) throws ExcelWorkbookFactoryException {

        XSSFWorkbook workbook = new XSSFWorkbook();

        QualityResults filteredQualityResults = applyFiltersToQualityResults(qualityResults);
        QualityResults sortedQualityResults = sortQualityResults(qualityResults);
        AlphabeticallySortedMismatchedQualityResults asmQualityResults = new AlphabeticallySortedMismatchedQualityResults(qualityResults);

        logger.info("Adding explanatory sheet to Excel quality report file");
        if (explanatoryExcelSheetFactory != null){
            XSSFWorkbook workbook2 = addExplanatorySheet(workbook);
            if (workbook2 != null){
                workbook = workbook2;
            }
        }

        logger.info("Adding filtered elements to quality report file");

        QualityResultsStatistics filteredQualityResultsStatistics = getQualityResultStatistics(filteredQualityResults);
        workbook = addSheet(workbook, filteredElements_sheetTitle, filteredQualityResults, asmQualityResults, filteredQualityResultsStatistics);

        logger.info("Adding all elements to quality report file");

        QualityResultsStatistics qualityResultsStatistics = getQualityResultStatistics(qualityResults);
        workbook = addSheet(workbook, allElements_sheetTitle, sortedQualityResults, asmQualityResults, qualityResultsStatistics);

        logger.info("Adding mismatching patient local ids");
        workbook = addPatientLocalIdsSheet(workbook, asmQualityResults);

        //logger.info("Adding mismatching patient dktk ids");
        //workbook = addPatientDktkIdsSheet(workbook, asmQualityResults);


        logger.info("Adding data element statistics");
        workbook = addDataElementStatistics(workbook, dataElementStatistics, sortedQualityResults, qualityResultsStatistics);

        return workbook;

    }

    private QualityResultsStatistics getQualityResultStatistics (QualityResults qualityResults){
        return new QualityResultsStatisticsImpl(qualityResults, mdrMappedElements);
    }

    private XSSFWorkbook addSheet (XSSFWorkbook workbook, String sheetTitle, QualityResults qualityResults) throws ExcelWorkbookFactoryException {

        ExcelRowContext excelRowContext = createExcelRowContext(qualityResults);
        return addSheet(workbook, sheetTitle, excelRowContext);

    }

    private XSSFWorkbook addSheet (XSSFWorkbook workbook, String sheetTitle, QualityResults qualityResults, AlphabeticallySortedMismatchedQualityResults asmQualityResults, QualityResultsStatistics qualityResultsStatistics) throws ExcelWorkbookFactoryException {

        ExcelRowContext excelRowContext = createExcelRowContext(qualityResults, asmQualityResults, qualityResultsStatistics);
        return addSheet(workbook, sheetTitle, excelRowContext);

    }

    private XSSFWorkbook addPatientLocalIdsSheet (XSSFWorkbook workbook, AlphabeticallySortedMismatchedQualityResults qualityResults) throws ExcelWorkbookFactoryException {

        ExcelRowContext excelRowContext = patientLocalIdsExcelRowContextFactory.createExcelRowContext(qualityResults);
        return addSheet(workbook, patientLocalIds_sheetTitle, excelRowContext);

    }

    private XSSFWorkbook addPatientDktkIdsSheet (XSSFWorkbook workbook, AlphabeticallySortedMismatchedQualityResults qualityResults) throws ExcelWorkbookFactoryException {

        ExcelRowContext excelRowContext = patientDktkIdsExcelRowContextFactory.createExcelRowContext(qualityResults);
        return addSheet(workbook, patientDktkIds_sheetTitle, excelRowContext);

    }

    private XSSFWorkbook addDataElementStatistics (XSSFWorkbook workbook, String sheetTitle, QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics) throws ExcelWorkbookFactoryException {

        ExcelRowContext excelRowContext = dataElementStats_excelRowContextFactory.createExcelRowContext(qualityResults, qualityResultsStatistics);
        return addSheet(workbook, sheetTitle, excelRowContext);

    }

    private XSSFWorkbook addSheet (XSSFWorkbook workbook, String sheetTitle, ExcelRowContext excelRowContext) throws ExcelWorkbookFactoryException {

        try {

            return excelSheetFactory.addSheet(workbook, sheetTitle, excelRowContext);

        } catch (ExcelSheetFactoryException e) {
            throw new ExcelWorkbookFactoryException(e);
        }

    }

    private ExcelRowContext createExcelRowContext (QualityResults qualityResults){
        return excelRowContextFactory.createExcelRowContext(qualityResults);
    }

    private ExcelRowContext createExcelRowContext (QualityResults qualityResults, AlphabeticallySortedMismatchedQualityResults asmQualityResults, QualityResultsStatistics qualityResultsStatistics){
        return excelRowContextFactory.createExcelRowContext(qualityResults, asmQualityResults, qualityResultsStatistics);
    }

    private XSSFWorkbook addExplanatorySheet(XSSFWorkbook workbook) throws ExcelWorkbookFactoryException {

        try {
            return explanatoryExcelSheetFactory.addSheet(workbook, null, null);
        } catch (ExcelSheetFactoryException e) {
            throw new ExcelWorkbookFactoryException(e);
        }

    }

    private QualityResults applyFiltersToQualityResults (QualityResults qualityResults){

        qualityResults = new QualityResultsValidDateFilter(qualityResults, modelSearcher);
        qualityResults = new QualityResultsValidIntegerFilter(qualityResults, modelSearcher);
        qualityResults = new QualityResultsValidStringFilter(qualityResults, modelSearcher);
        qualityResults = new QualityResultsSortedMdrIdsByDktkIdFilter(qualityResults, dktkIdManager);

        return qualityResults;

    }

    private QualityResults sortQualityResults (QualityResults qualityResults){
        return new QualityResultsSortedMdrIdsByDktkIdFilter(qualityResults, dktkIdManager);
    }


}
