package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryException;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSheetFactoryImpl implements ExcelSheetFactory {

  protected static final Logger logger = LogManager.getLogger(ExcelSheetFactoryImpl.class);
  private final ExcelRowFactory excelRowFactory;


  public ExcelSheetFactoryImpl(ExcelRowFactory excelRowFactory) {
    this.excelRowFactory = excelRowFactory;
  }

  @Override
  public XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

    XSSFSheet sheet = workbook.createSheet(sheetTitle);
    sheet = addRowTitles(sheet, excelRowContext);

    int maxNumberOfRows = SpreadsheetVersion.EXCEL2007.getMaxRows();

    int numberOfRows = excelRowContext.getNumberOfRows();
    PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfRows,
        "adding rows...");

    for (ExcelRowElements excelRowElements : excelRowContext) {

      percentageLogger.incrementCounter();
      addRow(sheet, excelRowElements);

      maxNumberOfRows--;
      if (maxNumberOfRows <= 0) {
        break;
      }
    }

    return workbook;

  }

  private XSSFSheet addRowTitles(XSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelSheetFactoryException {

    try {

      return excelRowFactory.addRowTitles(sheet, excelRowContext);

    } catch (ExcelRowFactoryException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }

  private XSSFSheet addRow(XSSFSheet sheet, ExcelRowElements excelRowElements)
      throws ExcelSheetFactoryException {

    try {

      return excelRowFactory.addRow(sheet, excelRowElements);

    } catch (ExcelRowFactoryException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }


}
