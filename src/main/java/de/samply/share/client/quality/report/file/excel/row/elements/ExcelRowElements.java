package de.samply.share.client.quality.report.file.excel.row.elements;

import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyle;
import java.util.Map;
import org.apache.commons.collections.FastHashMap;

public abstract class ExcelRowElements {

  protected int maxNumberOfElements;

  private final Map<Integer, ExcelCellElement> elements = new FastHashMap();


  public ExcelRowElements(int maxNumberOfElements) {
    this.maxNumberOfElements = maxNumberOfElements;
  }


  public abstract ExcelCellElement getElementTitle(int ordinal);

  protected void addElement(int ordinal, ExcelCellElement element) {
    if (element != null && ordinal < maxNumberOfElements) {
      elements.put(ordinal, element);
    }
  }

  public ExcelCellElement getElement(int ordinal) {
    return (ordinal < maxNumberOfElements) ? elements.get(ordinal) : null;
  }

  /**
   * Add excel cell style.
   *
   * @param excelCellStyle excel cell style to row.
   * @param ordinal        number of column.
   */
  public void addExcelCellStyle(ExcelCellStyle excelCellStyle, int ordinal) {

    ExcelCellElement element = getElement(ordinal);
    if (element != null) {
      element.setExcelCellStyle(excelCellStyle);
    }

  }

  public int getMaxNumberOfElements() {
    return maxNumberOfElements;
  }

}
