package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelSheetFreezeFirstRowFactory extends ExcelSheetFactoryWrapper {

  public ExcelSheetFreezeFirstRowFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected XSSFSheet addFunctionalityToSheet(XSSFSheet sheet) {

    sheet.createFreezePane(0, 1);
    return sheet;

  }

}
