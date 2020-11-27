package de.samply.share.client.quality.report.file.excel.cell.element;

import org.apache.poi.ss.usermodel.Cell;

public class NaturalLanguageBooleanExcelCellElement extends ExcelCellElement<Boolean> {

  private NaturalLanguageBoolean booleanLanguage = NaturalLanguageBoolean.DE;

  public NaturalLanguageBooleanExcelCellElement(Boolean element) {
    super(element);
  }

  @Override
  protected Cell setCellValue(Cell cell) {

    String value = booleanLanguage.getValue(element);
    cell.setCellValue(value);

    return cell;

  }

  public void setBooleanLanguage(NaturalLanguageBoolean booleanLanguage) {
    this.booleanLanguage = booleanLanguage;
  }

}
