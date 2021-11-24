package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryException;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.ExcelSheetFunctionality;
import de.samply.share.common.utils.PercentageLogger;
import java.util.Iterator;
import java.util.Set;
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
    int tempMaxNumberOfRowsPerSheet =
        (maxNumberOfRowsPerSheet <= 0) ? numberOfRows : maxNumberOfRowsPerSheet;

    String sheetTitleTemp = sheetTitle;
    int counter = 1;
    Iterator<ExcelRowElements> excelRowElementsIterator = excelRowContext.iterator();

    while (numberOfRows > 0) {

      workbook = addSheet(workbook, sheetTitleTemp, excelRowContext, excelRowElementsIterator,
          tempMaxNumberOfRowsPerSheet);
      sheetTitleTemp = sheetTitle + "-" + (++counter);
      numberOfRows -= tempMaxNumberOfRowsPerSheet;

    }

    return workbook;

  }

  @Override
  public SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext,
      Set<ExcelSheetFunctionality> deactivatedExcelSheetFunctionalities)
      throws ExcelSheetFactoryException {
    return addSheet(workbook, sheetTitle, excelRowContext);
  }

  private SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext,
      Iterator<ExcelRowElements> excelRowElementsIterator, int numberOfRows)
      throws ExcelSheetFactoryException {

    SXSSFSheet sheet = workbook.createSheet(sheetTitle);
    sheet = addRowTitles(sheet, excelRowContext);
    sheet.trackAllColumnsForAutoSizing();

    int maxNumberOfRows = SpreadsheetVersion.EXCEL2007.getMaxRows();

    PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfRows,
        "adding rows...");

    while (numberOfRows-- > 0 && excelRowElementsIterator.hasNext()) {

      ExcelRowElements excelRowElements = excelRowElementsIterator.next();
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
