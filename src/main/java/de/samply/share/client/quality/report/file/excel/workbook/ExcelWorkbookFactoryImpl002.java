package de.samply.share.client.quality.report.file.excel.workbook;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientDktkIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientLocalIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats.DataElementStatsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextFactory002;
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
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWorkbookFactoryImpl002 implements ExcelWorkbookFactory {


  public static final String ALL_ELEMENTS_SHEET_TITLE = "all elements";
  public static final String FILTERED_ELEMENTS_SHEET_TITLE = "filtered elements";
  public static final String PATIENT_LOCAL_IDS_SHEET_TITLE = "patient local ids";
  public static final String PATIENT_DKTK_IDS_SHEET_TITLE = "patient dktk ids";
  public static final String DATA_ELEMENT_STATISTICS = "data element stats";
  protected static final Logger logger = LogManager.getLogger(ExcelWorkbookFactoryImpl002.class);
  private final ExcelRowContextFactory002 excelRowContextFactory;
  private final DataElementStatsExcelRowContextFactory dataElementStatsExcelRowContextFactory;
  private final PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory;
  private final PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory;
  private final ExcelSheetFactory excelSheetFactory;
  private final ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory;
  private final ModelSearcher modelSearcher;
  private final DktkIdMdrIdConverter dktkIdManager;
  private final MdrMappedElements mdrMappedElements;


  /**
   * Todo.
   *
   * @param parameters Todo.
   */
  public ExcelWorkbookFactoryImpl002(ExcelWorkbookFactoryParameters002 parameters) {

    this.excelSheetFactory = parameters.getExcelSheetFactory();
    this.patientLocalIdsExcelRowContextFactory = parameters
        .getPatientLocalIdsExcelRowContextFactory();
    this.patientDktkIdsExcelRowContextFactory = parameters
        .getPatientDktkIdsExcelRowContextFactory();

    this.explanatoryExcelSheetFactory = parameters.getExplanatoryExcelSheetFactory();
    this.modelSearcher = parameters.getModelSearcher();

    this.dktkIdManager = parameters.getDktkIdManager();
    this.excelRowContextFactory = parameters.getExcelRowContextFactory();
    this.dataElementStatsExcelRowContextFactory = parameters
        .getDataElementStats_excelRowContextFactory();

    this.mdrMappedElements = parameters.getMdrMappedElements();

  }

  @Override
  public XSSFWorkbook createWorkbook(QualityResults qualityResults)
      throws ExcelWorkbookFactoryException {

    XSSFWorkbook workbook = new XSSFWorkbook();

    QualityResults filteredQualityResults = applyFiltersToQualityResults(qualityResults);
    QualityResults sortedQualityResults = sortQualityResults(qualityResults);
    AlphabeticallySortedMismatchedQualityResults asmQualityResults =
        new AlphabeticallySortedMismatchedQualityResults(qualityResults);

    if (isSheetSelectedToBeWritten(EnumConfiguration.QUALITY_REPORT_SHOW_INFO_SHEET)) {
      logger.info("Adding explanatory sheet to Excel quality report file");
      if (explanatoryExcelSheetFactory != null) {
        XSSFWorkbook workbook2 = addExplanatorySheet(workbook);
        if (workbook2 != null) {
          workbook = workbook2;
        }
      }
    }

    QualityResultsStatistics qualityResultsStatistics = null;
    if (isSheetSelectedToBeWritten(EnumConfiguration.QUALITY_REPORT_SHOW_FILTERED_ELEMENTS_SHEET)) {

      logger.info("Adding filtered elements to quality report file");

      qualityResultsStatistics = getQualityResultStatistics(filteredQualityResults);
      workbook = addSheet(workbook, FILTERED_ELEMENTS_SHEET_TITLE, filteredQualityResults,
          asmQualityResults, qualityResultsStatistics);

    }

    if (isSheetSelectedToBeWritten(EnumConfiguration.QUALITY_REPORT_SHOW_ALL_ELEMENTS_SHEET)) {

      logger.info("Adding all elements to quality report file");

      qualityResultsStatistics = getQualityResultStatistics(qualityResults);
      workbook = addSheet(workbook, ALL_ELEMENTS_SHEET_TITLE, sortedQualityResults,
          asmQualityResults, qualityResultsStatistics);

    }

    if (isSheetSelectedToBeWritten(EnumConfiguration.QUALITY_REPORT_SHOW_PATIENT_IDS_SHEET)) {

      logger.info("Adding mismatching patient local ids");
      workbook = addPatientLocalIdsSheet(workbook, asmQualityResults);

    }

    if (isSheetSelectedToBeWritten(EnumConfiguration.QUALITY_REPORT_SHOW_STATISTICS_SHEET)
        && qualityResultsStatistics != null) {

      logger.info("Adding data element statistics");
      workbook = addDataElementStatistics(workbook, DATA_ELEMENT_STATISTICS, sortedQualityResults,
          qualityResultsStatistics);

    }

    return workbook;

  }

  private boolean isSheetSelectedToBeWritten(EnumConfiguration enumConfiguration) {
    return ConfigurationUtil.getConfigurationElementValueAsBoolean(enumConfiguration);
  }

  private QualityResultsStatistics getQualityResultStatistics(QualityResults qualityResults) {
    return new QualityResultsStatisticsImpl(qualityResults, mdrMappedElements);
  }

  private XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle,
      QualityResults qualityResults) throws ExcelWorkbookFactoryException {

    ExcelRowContext excelRowContext = createExcelRowContext(qualityResults);
    return addSheet(workbook, sheetTitle, excelRowContext);

  }

  private XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle,
      QualityResults qualityResults, AlphabeticallySortedMismatchedQualityResults asmQualityResults,
      QualityResultsStatistics qualityResultsStatistics) throws ExcelWorkbookFactoryException {

    ExcelRowContext excelRowContext = createExcelRowContext(qualityResults, asmQualityResults,
        qualityResultsStatistics);
    return addSheet(workbook, sheetTitle, excelRowContext);

  }

  private XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelWorkbookFactoryException {
    try {
      return excelSheetFactory.addSheet(workbook, sheetTitle, excelRowContext);
    } catch (ExcelSheetFactoryException e) {
      throw new ExcelWorkbookFactoryException(e);
    }
  }

  private XSSFWorkbook addPatientLocalIdsSheet(XSSFWorkbook workbook,
      AlphabeticallySortedMismatchedQualityResults qualityResults)
      throws ExcelWorkbookFactoryException {

    ExcelRowContext excelRowContext = patientLocalIdsExcelRowContextFactory
        .createExcelRowContext(qualityResults);
    return addSheet(workbook, PATIENT_LOCAL_IDS_SHEET_TITLE, excelRowContext);

  }

  private XSSFWorkbook addPatientDktkIdsSheet(XSSFWorkbook workbook,
      AlphabeticallySortedMismatchedQualityResults qualityResults)
      throws ExcelWorkbookFactoryException {

    ExcelRowContext excelRowContext = patientDktkIdsExcelRowContextFactory
        .createExcelRowContext(qualityResults);
    return addSheet(workbook, PATIENT_DKTK_IDS_SHEET_TITLE, excelRowContext);

  }

  private XSSFWorkbook addDataElementStatistics(XSSFWorkbook workbook, String sheetTitle,
      QualityResults qualityResults, QualityResultsStatistics qualityResultsStatistics)
      throws ExcelWorkbookFactoryException {

    ExcelRowContext excelRowContext = dataElementStatsExcelRowContextFactory
        .createExcelRowContext(qualityResults, qualityResultsStatistics);
    return addSheet(workbook, sheetTitle, excelRowContext);

  }

  private ExcelRowContext createExcelRowContext(QualityResults qualityResults) {
    return excelRowContextFactory.createExcelRowContext(qualityResults);
  }

  private ExcelRowContext createExcelRowContext(QualityResults qualityResults,
      AlphabeticallySortedMismatchedQualityResults asmQualityResults,
      QualityResultsStatistics qualityResultsStatistics) {
    return excelRowContextFactory
        .createExcelRowContext(qualityResults, asmQualityResults, qualityResultsStatistics);
  }

  private XSSFWorkbook addExplanatorySheet(XSSFWorkbook workbook)
      throws ExcelWorkbookFactoryException {

    try {
      return explanatoryExcelSheetFactory.addSheet(workbook, null, null);
    } catch (ExcelSheetFactoryException e) {
      throw new ExcelWorkbookFactoryException(e);
    }

  }

  private QualityResults applyFiltersToQualityResults(QualityResults qualityResults) {

    qualityResults = new QualityResultsValidDateFilter(qualityResults, modelSearcher);
    qualityResults = new QualityResultsValidIntegerFilter(qualityResults, modelSearcher);
    qualityResults = new QualityResultsValidStringFilter(qualityResults, modelSearcher);
    qualityResults = new QualityResultsSortedMdrIdsByDktkIdFilter(qualityResults, dktkIdManager);

    return qualityResults;

  }

  private QualityResults sortQualityResults(QualityResults qualityResults) {
    return new QualityResultsSortedMdrIdsByDktkIdFilter(qualityResults, dktkIdManager);
  }


}
