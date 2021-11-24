package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactoryException;
import java.util.Set;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public abstract class ExcelSheetFactoryWrapper implements ExcelSheetFactory {

  private final ExcelSheetFactory excelSheetFactory;

  public ExcelSheetFactoryWrapper(ExcelSheetFactory excelSheetFactory) {
    this.excelSheetFactory = excelSheetFactory;
  }

  protected abstract ExcelSheetFunctionality getExcelSheetFunctionality();

  protected abstract SXSSFSheet addFunctionalityToSheet(SXSSFSheet sheet);

  private SXSSFWorkbook addFunctionalityToSheet(SXSSFWorkbook workbook, String sheetTitle) {
    SXSSFSheet sheet = workbook.getSheet(sheetTitle);
    addFunctionalityToSheet(sheet);
    return workbook;
  }

  @Override
  public SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {
    return addSheet(workbook, sheetTitle, excelRowContext, null);
  }

  @Override
  public SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext,
      Set<ExcelSheetFunctionality> deactivatedExcelSheetFunctionalities)
      throws ExcelSheetFactoryException {

    workbook = excelSheetFactory.addSheet(workbook, sheetTitle, excelRowContext,
        deactivatedExcelSheetFunctionalities);
    if (!isExcelSheetFunctionalityToBeDeactivated(deactivatedExcelSheetFunctionalities)) {
      workbook = addFunctionalityToSheet(workbook, sheetTitle);
    }

    return workbook;

  }


  @Override
  public void setMaxNumberOfRowsPerSheet(int maxNumberOfRowsPerSheet) {
    excelSheetFactory.setMaxNumberOfRowsPerSheet(maxNumberOfRowsPerSheet);
  }

  private boolean isExcelSheetFunctionalityToBeDeactivated(
      Set<ExcelSheetFunctionality> deactivatedExcelSheetFunctionalities) {
    return (deactivatedExcelSheetFunctionalities != null)
        ? deactivatedExcelSheetFunctionalities.contains(getExcelSheetFunctionality()) : false;
  }


}
