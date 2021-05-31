package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.utils.ExcelUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;

public class ExcelSheetWithAutoFilterFactory extends ExcelSheetFactoryWrapper {

  public ExcelSheetWithAutoFilterFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected SXSSFSheet addFunctionalityToSheet(SXSSFSheet sheet) {

    CellRangeAddress range = ExcelUtils.getAllSheetRange(sheet);
    sheet.setAutoFilter(range);

    return sheet;
  }


}
