package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.utils.ExcelUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelSheetWithAutoFilterFactory extends ExcelSheetFactoryWrapper {

  public ExcelSheetWithAutoFilterFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected XSSFSheet addFunctionalityToSheet(XSSFSheet sheet) {

    CellRangeAddress range = ExcelUtils.getAllSheetRange(sheet);
    sheet.setAutoFilter(range);

    return sheet;
  }


}
