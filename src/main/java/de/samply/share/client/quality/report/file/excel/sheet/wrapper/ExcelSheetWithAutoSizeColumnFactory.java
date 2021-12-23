package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ExcelSheetWithAutoSizeColumnFactory extends ExcelSheetFactoryWrapper {

  protected static final Logger logger = LoggerFactory
      .getLogger(ExcelSheetWithAutoSizeColumnFactory.class);
  private final Set<Integer> excludedColumnsForAutoSize = new HashSet<>();

  public ExcelSheetWithAutoSizeColumnFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected SXSSFSheet addFunctionalityToSheet(SXSSFSheet sheet) {

    int lastRowNum = sheet.getLastRowNum();
    for (int i = 0; i < sheet.getRow(lastRowNum).getPhysicalNumberOfCells(); i++) {

      if (!excludedColumnsForAutoSize.contains(i)) {
        //sheet.autoSizeColumn(i);
        autoSizeColumn(sheet, i);
      }

    }

    return sheet;

  }


  private void autoSizeColumn(SXSSFSheet sheet, int column) {
    try {
      sheet.autoSizeColumn(column);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void addExcludedColumn(int column) {
    excludedColumnsForAutoSize.add(column);
  }

}
