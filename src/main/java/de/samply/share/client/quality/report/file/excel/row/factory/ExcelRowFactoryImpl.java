package de.samply.share.client.quality.report.file.excel.row.factory;

import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRowFactoryImpl implements ExcelRowFactory {

  private final StringExcelCellElement emptyElement = new StringExcelCellElement("");


  @Override
  public XSSFSheet addRowTitles(XSSFSheet sheet, ExcelRowContext excelRowContext)
      throws ExcelRowFactoryException {

    ExcelRowElements emptyExcelRowElements = excelRowContext.createEmptyExcelRowElements();

    int rowNum = 0;

    XSSFRow row = sheet.createRow(rowNum);

    for (int i = 0;
        i < emptyExcelRowElements.getMaxNumberOfElements() && i < SpreadsheetVersion.EXCEL2007
            .getMaxColumns(); i++) {

      ExcelCellElement title = emptyExcelRowElements.getElementTitle(i);
      title.addAsCell(row);

    }

    setTitleRowStyle(row);

    return sheet;

  }

  protected XSSFRow setTitleRowStyle(XSSFRow titleRow) {

    XSSFCellStyle cellStyle = getWorkbook(titleRow).createCellStyle();
    XSSFFont font = getWorkbook(titleRow).createFont();
    font.setBold(true);

    cellStyle.setFont(font);

    for (int i = 0; i < titleRow.getLastCellNum(); i++) {
      XSSFCell cell = titleRow.getCell(i);
      cell.setCellStyle(cellStyle);
    }

    return titleRow;

  }

  private XSSFWorkbook getWorkbook(XSSFRow row) {
    return row.getSheet().getWorkbook();
  }

  @Override
  public XSSFSheet addRow(XSSFSheet sheet, ExcelRowElements excelRowElements)
      throws ExcelRowFactoryException {

    int rowNum = sheet.getLastRowNum() + 1;

    XSSFRow row = sheet.createRow(rowNum);

    addElementsToRow(row, excelRowElements);

    return sheet;
  }

  private XSSFRow addElementsToRow(XSSFRow row, ExcelRowElements elements) {

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
