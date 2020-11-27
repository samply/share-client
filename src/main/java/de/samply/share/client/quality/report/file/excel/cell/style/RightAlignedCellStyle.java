package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class RightAlignedCellStyle extends ExcelCellStyleWrapper {

  public RightAlignedCellStyle(ExcelCellStyle excelCellStyle) {
    super(excelCellStyle);
  }

  @Override
  public void addCellStyle(Cell cell) {

    super.addCellStyle(cell);
    CellStyle cellStyle = cell.getCellStyle();
    cellStyle.setAlignment(HorizontalAlignment.RIGHT);

  }

}
