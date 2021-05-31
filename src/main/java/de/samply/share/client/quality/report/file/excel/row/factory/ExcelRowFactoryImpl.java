package de.samply.share.client.quality.report.file.excel.row.factory;

import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class ExcelRowFactoryImpl implements ExcelRowFactory {

  private final StringExcelCellElement emptyElement = new StringExcelCellElement("");


  @Override
  public SXSSFSheet addRowTitles(SXSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelRowFactoryException {

    ExcelRowElements emptyExcelRowElements = excelRowContext.createEmptyExcelRowElements();

    int rowNum = 0;

    SXSSFRow row = sheet.createRow(rowNum);

    for (int i = 0;
        i < emptyExcelRowElements.getMaxNumberOfElements() && i < SpreadsheetVersion.EXCEL2007
            .getMaxColumns(); i++) {

      ExcelCellElement title = emptyExcelRowElements.getElementTitle(i);
      title.addAsCell(row);

    }

    setTitleRowStyle(row);

    return sheet;

  }

  protected SXSSFRow setTitleRowStyle(SXSSFRow titleRow) {

    CellStyle cellStyle = getWorkbook(titleRow).createCellStyle();
    Font font = getWorkbook(titleRow).createFont();
    font.setBold(true);

    cellStyle.setFont(font);

    for (int i = 0; i < titleRow.getLastCellNum(); i++) {
      SXSSFCell cell = titleRow.getCell(i);
      cell.setCellStyle(cellStyle);
    }

    return titleRow;

  }

  private SXSSFWorkbook getWorkbook(SXSSFRow row) {
    return row.getSheet().getWorkbook();
  }

  @Override
  public SXSSFSheet addRow(SXSSFSheet sheet, ExcelRowElements excelRowElements)
      throws ExcelRowFactoryException {

    int rowNum = sheet.getLastRowNum() + 1;

    SXSSFRow row = sheet.createRow(rowNum);

    addElementsToRow(row, excelRowElements);

    return sheet;
  }

  private SXSSFRow addElementsToRow(SXSSFRow row, ExcelRowElements elements) {

    for (int i = 0;
        i < elements.getMaxNumberOfElements() && i < SpreadsheetVersion.EXCEL2007.getMaxColumns();
        i++) {

      ExcelCellElement element = elements.getElement(i);

      if (element != null) {
        element.addAsCell(row);
      } else {
        emptyElement.addAsCell(row);
      }

    }

    return row;
  }

}
