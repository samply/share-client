package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class GreenBackgroundCellStyle extends ExcelCellStyleWrapper {

  public GreenBackgroundCellStyle(ExcelCellStyle excelCellStyle) {
    super(excelCellStyle);
  }

  @Override
  public void addCellStyle(Cell cell) {

    super.addCellStyle(cell);
    CellStyle cellStyle = cell.getCellStyle();

    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

  }

}
