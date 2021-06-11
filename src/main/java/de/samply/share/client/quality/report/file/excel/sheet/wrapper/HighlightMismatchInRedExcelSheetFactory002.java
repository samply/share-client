package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.cell.element.MatchElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellReference;


public class HighlightMismatchInRedExcelSheetFactory002 extends ExcelSheetWithHighLightFactory {


  public HighlightMismatchInRedExcelSheetFactory002(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }


  @Override
  protected String getRule() {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append('$');
    stringBuilder.append(getColumnToBeHighlighted());
    stringBuilder.append("1=\"");
    stringBuilder.append(MatchElement.MISMATCH);
    stringBuilder.append('\"');

    return stringBuilder.toString();

  }

  private String getColumnToBeHighlighted() {

    int columnNumber = getColumnNumberToBeHighlighted();
    return CellReference.convertNumToColString(columnNumber);

  }

  private int getColumnNumberToBeHighlighted() {
    return ExcelRowElements002.ElementOrder.IS_VALID.ordinal();
  }

  @Override
  protected FontFormatting setHighlightFontFormatting(FontFormatting fontFormatting) {

    fontFormatting.setFontColorIndex(IndexedColors.RED.getIndex());
    return fontFormatting;

  }

}
