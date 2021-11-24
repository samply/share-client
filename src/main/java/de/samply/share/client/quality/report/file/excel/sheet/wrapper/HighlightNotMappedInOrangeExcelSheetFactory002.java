package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.cell.element.MatchElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellReference;


public class HighlightNotMappedInOrangeExcelSheetFactory002 extends
    ExcelSheetWithHighLightFactory {


  public HighlightNotMappedInOrangeExcelSheetFactory002(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  @Override
  protected ExcelSheetFunctionality getExcelSheetFunctionality() {
    return ExcelSheetFunctionality.HIGHLIGHT_NOT_MAPPED_IN_ORANGE;
  }


  @Override
  protected String getRule() {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append('$');
    stringBuilder.append(getColumnToBeHighlighted());
    stringBuilder.append("1=\"");
    stringBuilder.append(MatchElement.NOT_MAPPED);
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

    fontFormatting.setFontColorIndex(IndexedColors.ORANGE.getIndex());
    return fontFormatting;

  }

}
