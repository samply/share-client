package de.samply.share.client.quality.report.file.excel.workbook;

import de.samply.share.client.quality.report.results.QualityResults;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface ExcelWorkbookFactory {

  SXSSFWorkbook createWorkbook(QualityResults qualityResults)
      throws ExcelWorkbookFactoryException;

}
