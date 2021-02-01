package de.samply.share.client.quality.report.file.excel.pattern;

import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactory;
import de.samply.share.client.quality.report.file.id.filename.QualityReportFilePattern;

public interface ExcelPattern extends QualityReportFilePattern {

  ExcelWorkbookFactory createExcelWorkbookFactory();

}
