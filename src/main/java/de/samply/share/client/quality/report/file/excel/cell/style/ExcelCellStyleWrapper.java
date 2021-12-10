package de.samply.share.client.quality.report.file.excel.cell.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;


public abstract class ExcelCellStyleWrapper implements ExcelCellStyle {

  private final ExcelCellStyle excelCellStyle;
  private Workbook workbook;

  protected abstract void setCellStyle(CellStyle cellStyle);

  public ExcelCellStyleWrapper(
      ExcelCellStyle excelCellStyle) {
    this.excelCellStyle = excelCellStyle;
  }

  @Override
  public void addCellStyle(Cell cell) {
    getCellStyle(cell);
    excelCellStyle.addCellStyle(cell);
  }

  @Override
  public CellStyle getCellStyle(Cell cell) {

    CellStyle cellStyle = excelCellStyle.getCellStyle(cell);
    if (hasWorkbookChanged(cell)) {
      setCellStyle(cellStyle);
    }

    return cellStyle;

  }

  private boolean hasWorkbookChanged(Cell cell) {

    boolean hasChanged = false;

    Workbook tempWorkbook = cell.getSheet().getWorkbook();

    if (workbook == null || workbook != tempWorkbook) {

      workbook = tempWorkbook;
      hasChanged = true;

    }

    return hasChanged;

  }

}
