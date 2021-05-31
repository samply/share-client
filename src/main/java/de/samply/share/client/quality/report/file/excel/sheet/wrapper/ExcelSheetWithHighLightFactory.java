package de.samply.share.client.quality.report.file.excel.sheet.wrapper;

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingRule;
import org.apache.poi.xssf.usermodel.XSSFFontFormatting;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheetConditionalFormatting;

public abstract class ExcelSheetWithHighLightFactory extends ExcelSheetFactoryWrapper {


  public ExcelSheetWithHighLightFactory(ExcelSheetFactory excelSheetFactory) {
    super(excelSheetFactory);
  }

  protected abstract String getRule();

  protected abstract FontFormatting setHighlightFontFormatting(
      FontFormatting fontFormatting);

  @Override
  protected SXSSFSheet addFunctionalityToSheet(SXSSFSheet sheet) {

    SheetConditionalFormatting sheetConditionalFormatting = sheet
        .getSheetConditionalFormatting();
    ConditionalFormattingRule rule = sheetConditionalFormatting
        .createConditionalFormattingRule(getRule());
    rule = addHighlightFontFormatting(rule);
    CellRangeAddress[] ranges = {ExcelUtils.getAllSheetRange(sheet)};

    sheetConditionalFormatting.addConditionalFormatting(ranges, rule);

    return sheet;
  }

  private ConditionalFormattingRule addHighlightFontFormatting(
      ConditionalFormattingRule rule) {

    FontFormatting fontFormatting = rule.createFontFormatting();
    setHighlightFontFormatting(fontFormatting);

    return rule;

  }


}
