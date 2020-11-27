package de.samply.share.client.quality.report.file.excel.cell.element;

import org.apache.poi.ss.usermodel.Cell;

public class StringExcelCellElement extends ExcelCellElement<String> {

  public StringExcelCellElement(String element) {
    super(element);
  }

  @Override
  protected Cell setCellValue(Cell cell) {

    cell.setCellValue(element);
    return cell;

  }

}
