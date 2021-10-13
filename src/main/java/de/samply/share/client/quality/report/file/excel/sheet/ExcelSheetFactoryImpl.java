package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryException;
import de.samply.share.common.utils.PercentageLogger;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class ExcelSheetFactoryImpl implements ExcelSheetFactory {

  protected static final Logger logger = LogManager.getLogger(ExcelSheetFactoryImpl.class);
  private final ExcelRowFactory excelRowFactory;
  private Integer sheetWindow = 300;


  public ExcelSheetFactoryImpl(ExcelRowFactory excelRowFactory) {
    this.excelRowFactory = excelRowFactory;
  }

  @Override
  public SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

    SXSSFSheet sheet = workbook.createSheet(sheetTitle);
    sheet = addRowTitles(sheet, excelRowContext);
    sheet.trackAllColumnsForAutoSizing();

    int maxNumberOfRows = SpreadsheetVersion.EXCEL2007.getMaxRows();

    int numberOfRows = excelRowContext.getNumberOfRows();
    PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfRows,
        "adding rows...");

    SheetFlusher sheetFlusher = new SheetFlusher(sheet);
    for (ExcelRowElements excelRowElements : excelRowContext) {

      percentageLogger.incrementCounter();
      addRow(sheet, excelRowElements, sheetFlusher);

      maxNumberOfRows--;
      if (maxNumberOfRows <= 0) {
        break;
      }
    }

    return workbook;

  }

  private class SheetFlusher {

    SXSSFSheet sheet;
    int counter = 0;

    public SheetFlusher(SXSSFSheet sheet) {
      this.sheet = sheet;
    }

    public void addRow() throws ExcelSheetFactoryException {
      counter++;
      if (counter >= sheetWindow) {
        flush();
        counter = 0;
      }

    }

    private void flush() throws ExcelSheetFactoryException {
      try {
        sheet.flushBufferedData();
      } catch (IOException e) {
        throw new ExcelSheetFactoryException(e);
      }
    }

  }

  private SXSSFSheet addRowTitles(SXSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelSheetFactoryException {

    try {

      return excelRowFactory.addRowTitles(sheet, excelRowContext);

    } catch (ExcelRowFactoryException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }

  private SXSSFSheet addRow(SXSSFSheet sheet, ExcelRowElements excelRowElements,
      SheetFlusher sheetFlusher)
      throws ExcelSheetFactoryException {

    try {

      SXSSFSheet rows = excelRowFactory.addRow(sheet, excelRowElements);
      sheetFlusher.addRow();
      return rows;

    } catch (ExcelRowFactoryException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }

  public void setSheetWindow(Integer sheetWindow) {
    this.sheetWindow = sheetWindow;
  }

}
