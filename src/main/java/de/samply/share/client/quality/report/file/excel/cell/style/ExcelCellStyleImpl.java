package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;


public class ExcelCellStyleImpl implements ExcelCellStyle {

  @Override
  public void addCellStyle(Cell cell) {

    CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
    cell.setCellStyle(cellStyle);

  }

}
