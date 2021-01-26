package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;


public class ExcelCellStyleWrapper implements ExcelCellStyle {

  private final ExcelCellStyle excelCellStyle;


  public ExcelCellStyleWrapper(ExcelCellStyle excelCellStyle) {
    this.excelCellStyle = excelCellStyle;
  }


  @Override
  public void addCellStyle(Cell cell) {
    excelCellStyle.addCellStyle(cell);
  }
}
