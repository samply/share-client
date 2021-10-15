package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public interface ExcelSheetFactory {

  SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException;

  void setMaxNumberOfRowsPerSheet(int maxNumberOfRowsPerSheet);

}
