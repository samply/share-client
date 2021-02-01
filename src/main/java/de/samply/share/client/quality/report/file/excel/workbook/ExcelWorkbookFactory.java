package de.samply.share.client.quality.report.file.excel.workbook;

import de.samply.share.client.quality.report.results.QualityResults;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface ExcelWorkbookFactory {

  XSSFWorkbook createWorkbook(QualityResults qualityResults)
      throws ExcelWorkbookFactoryException;

}
