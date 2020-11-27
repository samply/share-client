package de.samply.share.client.quality.report.file.excel.cell.reference;

import de.samply.share.client.quality.report.file.excel.cell.element.LinkExcelCellElement;
import org.apache.poi.common.usermodel.HyperlinkType;

public class CellReferenceExcelCellElement<T> extends LinkExcelCellElement<T> {

  public CellReferenceExcelCellElement(CellReference cellReference, T element) {
    super(cellReference.getLink(), element);
  }

  @Override
  protected HyperlinkType getHyperlinkType() {
    return HyperlinkType.DOCUMENT;
  }

}
