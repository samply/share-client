package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;


public class ExcelCellStyleImpl implements ExcelCellStyle {

  private CellStyle cellStyle;
  private Workbook workbook;

  @Override
  public void addCellStyle(Cell cell) {
    CellStyle cellStyle = getCellStyle(cell);
    cell.setCellStyle(cellStyle);
  }

  @Override
  public CellStyle getCellStyle(Cell cell) {

    if (cellStyle == null || workbook != getWorkbook(cell)) {
      workbook = getWorkbook(cell);
      cellStyle = workbook.createCellStyle();
    }

    return cellStyle;

  }

  private Workbook getWorkbook(Cell cell) {
    return cell.getSheet().getWorkbook();
  }

}
