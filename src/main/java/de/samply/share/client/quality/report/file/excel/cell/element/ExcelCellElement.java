package de.samply.share.client.quality.report.file.excel.cell.element;

import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public abstract class ExcelCellElement<T> {

  protected T element;
  private ExcelCellStyle excelCellStyle;

  public ExcelCellElement(T element) {
    this.element = element;
  }

  protected abstract Cell setCellValue(Cell cell);

  /**
   * Todo.
   *
   * @param row Todo.
   * @return Todo.
   */
  public Row addAsCell(Row row) {

    int cellNum = row.getLastCellNum();
    if (cellNum < 0) {
      cellNum = 0;
    }

    Cell cell = row.createCell(cellNum);
    if (excelCellStyle != null) {
      excelCellStyle.addCellStyle(cell);
    }

    setCellValue(cell);

    return row;
  }

  public void setExcelCellStyle(ExcelCellStyle excelCellStyle) {
    this.excelCellStyle = excelCellStyle;
  }

  protected String convertElementToString() {
    return element.toString();
  }

}
