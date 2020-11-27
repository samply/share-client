package de.samply.share.client.quality.report.file.excel.row.factory;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public interface ExcelRowFactory {

  public XSSFSheet addRowTitles(XSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelRowFactoryException;

  public XSSFSheet addRow(XSSFSheet sheet, ExcelRowElements excelRowElements)
      throws ExcelRowFactoryException;

}
