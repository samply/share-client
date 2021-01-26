package de.samply.share.client.quality.report.file.excel.instances.basic;

import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.LinkExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import java.util.List;


public class BasicExcelRowElements extends ExcelRowElements {

  private int nextElementToBeInsertedPosition = 0;
  private final List<BasicExcelColumnMetaInfo> metaInfos;

  /**
   * Todo.
   *
   * @param metaInfos Todo.
   */
  public BasicExcelRowElements(List<BasicExcelColumnMetaInfo> metaInfos) {

    super(metaInfos.size());
    this.metaInfos = metaInfos;

  }

  @Override
  public ExcelCellElement getElementTitle(int order) {

    BasicExcelColumnMetaInfo metaInfo = metaInfos.get(order);
    if (metaInfo != null) {
      String title = metaInfo.getTitle();
      String link = metaInfo.getLink();

      return (link != null) ? new LinkExcelCellElement(link, title)
          : new StringExcelCellElement(title);

    }

    return null;
  }

  public void addElement(String element) {
    addElement(nextElementToBeInsertedPosition, new StringExcelCellElement(element));
    nextElementToBeInsertedPosition++;
  }


}
