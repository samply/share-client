package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactoryException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelSheetFactoryWrapper implements ExcelSheetFactory {

  private final ExcelSheetFactory excelSheetFactory;

  public ExcelSheetFactoryWrapper(ExcelSheetFactory excelSheetFactory) {
    this.excelSheetFactory = excelSheetFactory;
  }

  protected abstract XSSFSheet addFunctionalityToSheet(XSSFSheet sheet);

  private XSSFWorkbook addFunctionalityToSheet(XSSFWorkbook workbook, String sheetTitle) {
    XSSFSheet sheet = workbook.getSheet(sheetTitle);
    addFunctionalityToSheet(sheet);
    return workbook;
  }

  @Override
  public XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {

    workbook = excelSheetFactory.addSheet(workbook, sheetTitle, excelRowContext);
    workbook = addFunctionalityToSheet(workbook, sheetTitle);

    return workbook;
  }


}
