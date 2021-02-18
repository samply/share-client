package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface ExcelSheetFactory {

  XSSFWorkbook addSheet(XSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException;

}
