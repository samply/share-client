package de.samply.share.client.quality.report.file.excel.cell.element;

import org.apache.poi.ss.usermodel.Cell;

public class MatchExcelCellElement extends ExcelCellElement<MatchElement> {

  public MatchExcelCellElement(MatchElement element) {
    super(element);
  }

  @Override
  protected Cell setCellValue(Cell cell) {

    String value = convertElementToString();
    cell.setCellValue(value);

    return cell;
  }


}
