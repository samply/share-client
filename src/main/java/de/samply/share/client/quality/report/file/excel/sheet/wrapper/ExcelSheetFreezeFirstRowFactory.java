package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import org.apache.poi.xssf.streaming.SXSSFSheet;

public class ExcelSheetFreezeFirstRowFactory extends ExcelSheetFactoryWrapper {

  public ExcelSheetFreezeFirstRowFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected SXSSFSheet addFunctionalityToSheet(SXSSFSheet sheet) {

    sheet.createFreezePane(0, 1);
    return sheet;

  }

  @Override
  protected ExcelSheetFunctionality getExcelSheetFunctionality() {
    return ExcelSheetFunctionality.FREEZE_FIRST_ROW;
  }

}
