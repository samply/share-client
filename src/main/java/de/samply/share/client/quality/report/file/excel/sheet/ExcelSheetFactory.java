package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.ExcelSheetFunctionality;
import java.util.Set;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public interface ExcelSheetFactory {

  SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException;

  SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext,
      Set<ExcelSheetFunctionality> deactivatedExcelSheetFunctionalities)
      throws ExcelSheetFactoryException;

  void setMaxNumberOfRowsPerSheet(int maxNumberOfRowsPerSheet);

}
