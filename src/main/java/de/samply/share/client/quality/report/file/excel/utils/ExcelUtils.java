package de.samply.share.client.quality.report.file.excel.utils;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;


public class ExcelUtils {

  /**
   * Get range of "all" sheet.
   *
   * @param sheet excel sheet.
   * @return Excel cell range address.
   */
  public static CellRangeAddress getAllSheetRange(SXSSFSheet sheet) {

    int firstRow = 0;
    int firstCol = 0;

    int lastRow = sheet.getLastRowNum();
    SXSSFRow row = sheet.getRow(lastRow);
    int lastCol = row.getLastCellNum();

    if (lastRow <= firstRow) {
      lastRow = firstRow + 1;
    }
    if (lastCol <= firstCol) {
      lastCol = firstCol + 1;
    }

    CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
    cellRangeAddress = controlLimits(cellRangeAddress);

    return cellRangeAddress;

  }

  private static CellRangeAddress controlLimits(CellRangeAddress cellRangeAddress) {

    if (cellRangeAddress != null) {

      int lastColumn = cellRangeAddress.getLastColumn();
      int lastRow = cellRangeAddress.getLastRow();

      if (lastColumn >= SpreadsheetVersion.EXCEL2007.getMaxColumns()) {
        cellRangeAddress.setLastColumn(SpreadsheetVersion.EXCEL2007.getMaxColumns() - 1);
      }

      if (lastRow >= SpreadsheetVersion.EXCEL2007.getMaxRows()) {
        cellRangeAddress.setLastRow(SpreadsheetVersion.EXCEL2007.getMaxRows() - 1);
      }

    }

    return cellRangeAddress;

  }


}
