package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;


public class ExcelSheetWithAutoSizeColumnFactory extends ExcelSheetFactoryWrapper {

  protected static final Logger logger = LogManager
      .getLogger(ExcelSheetWithAutoSizeColumnFactory.class);
  private final Set<Integer> excludedColumnsForAutoSize = new HashSet<>();

  public ExcelSheetWithAutoSizeColumnFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected XSSFSheet addFunctionalityToSheet(XSSFSheet sheet) {

    for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {

      if (!excludedColumnsForAutoSize.contains(i)) {
        //sheet.autoSizeColumn(i);
        autoSizeColumn(sheet, i);
      }

    }

    return sheet;
  }


  private void autoSizeColumn(XSSFSheet sheet, int column) {
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
