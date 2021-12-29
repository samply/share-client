package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class RightAlignedCellStyle extends ExcelCellStyleWrapper {

  public RightAlignedCellStyle() {
    super(new ExcelCellStyleImpl());
  }

  public RightAlignedCellStyle(
      ExcelCellStyle excelCellStyle) {
    super(excelCellStyle);
  }

  @Override
  protected void setCellStyle(CellStyle cellStyle) {
    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
  }

}
