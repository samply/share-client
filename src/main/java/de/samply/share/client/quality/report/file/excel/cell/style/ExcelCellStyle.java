package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

public interface ExcelCellStyle {

  void addCellStyle(Cell cell);

  public CellStyle getCellStyle(Cell cell);

}
