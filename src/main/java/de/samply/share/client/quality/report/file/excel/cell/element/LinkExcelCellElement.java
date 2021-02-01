package de.samply.share.client.quality.report.file.excel.cell.element;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

public class LinkExcelCellElement<T> extends ExcelCellElement<T> {

  private final String link;


  public LinkExcelCellElement(String link, T element) {
    super(element);
    this.link = link;
  }

  protected HyperlinkType getHyperlinkType() {
    return HyperlinkType.URL;
  }

  @Override
  protected Cell setCellValue(Cell cell) {

    Hyperlink link = createHyperlink(cell);

    String title = convertElementToString();

    cell.setHyperlink(link);
    cell.setCellValue(title);

    return cell;

  }

  private Hyperlink createHyperlink(Cell cell) {

    CreationHelper creationHelper = cell.getSheet().getWorkbook().getCreationHelper();
    Hyperlink hyperlink = creationHelper.createHyperlink(getHyperlinkType());
    hyperlink.setAddress(link);

    return hyperlink;

  }


}
