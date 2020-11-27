package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.utils.ExcelUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingRule;
import org.apache.poi.xssf.usermodel.XSSFFontFormatting;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheetConditionalFormatting;

public abstract class ExcelSheetWithHighLightFactory extends ExcelSheetFactoryWrapper {


  public ExcelSheetWithHighLightFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  protected abstract String getRule();

  protected abstract XSSFFontFormatting setHighlightFontFormatting(
      XSSFFontFormatting fontFormatting);

  @Override
  protected XSSFSheet addFunctionalityToSheet(XSSFSheet sheet) {

    XSSFSheetConditionalFormatting sheetConditionalFormatting = sheet
        .getSheetConditionalFormatting();
    XSSFConditionalFormattingRule rule = sheetConditionalFormatting
        .createConditionalFormattingRule(getRule());
    rule = addHighlightFontFormatting(rule);
    CellRangeAddress[] ranges = {ExcelUtils.getAllSheetRange(sheet)};

    sheetConditionalFormatting.addConditionalFormatting(ranges, rule);

    return sheet;
  }

  private XSSFConditionalFormattingRule addHighlightFontFormatting(
      XSSFConditionalFormattingRule rule) {

    XSSFFontFormatting fontFormatting = rule.createFontFormatting();
    setHighlightFontFormatting(fontFormatting);

    return rule;

  }


}
