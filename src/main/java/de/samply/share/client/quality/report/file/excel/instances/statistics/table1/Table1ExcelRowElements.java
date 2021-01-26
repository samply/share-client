package de.samply.share.client.quality.report.file.excel.instances.statistics.table1;

import de.samply.share.client.quality.report.file.excel.cell.element.DoubleExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;


public class Table1ExcelRowElements extends ExcelRowElements {

  public Table1ExcelRowElements() {
    super(ElementOrder.values().length);
  }

  @Override
  public ExcelCellElement getElementTitle(int order) {
    String title =
        (order >= 0 && order < ElementOrder.values().length) ? ElementOrder.values()[order]
            .getTitle() : "";
    return new StringExcelCellElement(title);
  }

  private void addElement(ElementOrder elementOrder, ExcelCellElement element) {
    addElement(elementOrder.ordinal(), element);
  }

  /**
   * Todo.
   *
   * @param dataElementGroup Todo.
   */
  public void addDataElementGroup(String dataElementGroup) {

    StringExcelCellElement stringExcelCellElement = new StringExcelCellElement(dataElementGroup);
    addElement(ElementOrder.DATA_ELEMENTS_GROUP, stringExcelCellElement);

  }

  /**
   * Todo.
   *
   * @param percentage Todo.
   */
  public void addPercentrage(Double percentage) {

    DoubleExcelCellElement doubleExcelCellElement = new DoubleExcelCellElement(percentage);
    addElement(ElementOrder.PERCENTAGE, doubleExcelCellElement);

  }

  public enum ElementOrder {

    DATA_ELEMENTS_GROUP("Data elements group"),
    PERCENTAGE("Percentage");

    private final String title;

    ElementOrder(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
  }

}
