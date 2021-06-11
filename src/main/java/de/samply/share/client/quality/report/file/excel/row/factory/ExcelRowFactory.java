package de.samply.share.client.quality.report.file.excel.row.factory;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import org.apache.poi.xssf.streaming.SXSSFSheet;

public interface ExcelRowFactory {

  SXSSFSheet addRowTitles(SXSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelRowFactoryException;

  SXSSFSheet addRow(SXSSFSheet sheet, ExcelRowElements excelRowElements)
      throws ExcelRowFactoryException;

}
