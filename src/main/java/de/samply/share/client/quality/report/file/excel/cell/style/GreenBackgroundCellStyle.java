package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class GreenBackgroundCellStyle extends ExcelCellStyleWrapper {

  public GreenBackgroundCellStyle() {
    super(new ExcelCellStyleImpl());
  }

  public GreenBackgroundCellStyle(
      ExcelCellStyle excelCellStyle) {
    super(excelCellStyle);
  }

  @Override
  protected void setCellStyle(CellStyle cellStyle) {
    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
  }

}
