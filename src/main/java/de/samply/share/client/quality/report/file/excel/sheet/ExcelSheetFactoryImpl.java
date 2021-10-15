package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryException;
import de.samply.share.common.utils.PercentageLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class ExcelSheetFactoryImpl implements ExcelSheetFactory {

  protected static final Logger logger = LogManager.getLogger(ExcelSheetFactoryImpl.class);
  private final ExcelRowFactory excelRowFactory;
  private int maxNumberOfRowsPerSheet = -1;


  public ExcelSheetFactoryImpl(ExcelRowFactory excelRowFactory) {
    this.excelRowFactory = excelRowFactory;
  }

  @Override
  public SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

    int numberOfRows = excelRowContext.getNumberOfRows();
    if (maxNumberOfRowsPerSheet < 0) {
      maxNumberOfRowsPerSheet = numberOfRows;
    }

    String sheetTitleTemp = sheetTitle;
    int counter = 1;

    while (numberOfRows > 0) {

      workbook = addSheet(workbook, sheetTitleTemp, excelRowContext, maxNumberOfRowsPerSheet);
      sheetTitleTemp = sheetTitle + "-" + (++counter);
      numberOfRows -= maxNumberOfRowsPerSheet;

    }

    return workbook;

  }

  private SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext, int numberOfRows) throws ExcelSheetFactoryException {

    SXSSFSheet sheet = workbook.createSheet(sheetTitle);
    sheet = addRowTitles(sheet, excelRowContext);
    sheet.trackAllColumnsForAutoSizing();

    int maxNumberOfRows = SpreadsheetVersion.EXCEL2007.getMaxRows();

    PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfRows,
        "adding rows...");

    while (numberOfRows-- > 0 && excelRowContext.iterator().hasNext()) {

      ExcelRowElements excelRowElements = excelRowContext.iterator().next();
      percentageLogger.incrementCounter();
      addRow(sheet, excelRowElements);

      maxNumberOfRows--;
      if (maxNumberOfRows <= 0) {
        break;
      }
    }

    return workbook;

  }

  private SXSSFSheet addRowTitles(SXSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelSheetFactoryException {

    try {

      return excelRowFactory.addRowTitles(sheet, excelRowContext);

    } catch (ExcelRowFactoryException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }

  private SXSSFSheet addRow(SXSSFSheet sheet, ExcelRowElements excelRowElements)
      throws ExcelSheetFactoryException {

    try {

      return excelRowFactory.addRow(sheet, excelRowElements);

    } catch (ExcelRowFactoryException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }

  @Override
  public void setMaxNumberOfRowsPerSheet(int maxNumberOfRowsPerSheet) {
    this.maxNumberOfRowsPerSheet = maxNumberOfRowsPerSheet;
  }

}
