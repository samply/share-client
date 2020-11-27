package de.samply.share.client.quality.report.file.excel.cell.element;

import org.apache.poi.ss.usermodel.Cell;

public class IntegerExcelCellElement extends ExcelCellElement<Integer> {

  public IntegerExcelCellElement(Integer element) {
    super(element);
  }

  @Override
  protected Cell setCellValue(Cell cell) {

    cell.setCellValue(element);
    return cell;

  }


}
