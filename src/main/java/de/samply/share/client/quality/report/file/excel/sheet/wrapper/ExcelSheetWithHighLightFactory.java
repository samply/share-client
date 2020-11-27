package de.samply.share.client.quality.report.file.excel.sheet.wrapper;/*
* Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
* Deutsches Krebsforschungszentrum in Heidelberg
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU Affero General Public License as published by the Free
* Software Foundation; either version 3 of the License, or (at your option) any
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program; if not, see http://www.gnu.org/licenses.
*
* Additional permission under GNU GPL version 3 section 7:
*
* If you modify this Program, or any covered work, by linking or combining it
* with Jersey (https://jersey.java.net) (or a modified version of that
* library), containing parts covered by the terms of the General Public
* License, version 2.0, the licensors of this Program grant you additional
* permission to convey the resulting work.
*/

import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.utils.ExcelUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingRule;
import org.apache.poi.xssf.usermodel.XSSFFontFormatting;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheetConditionalFormatting;

public abstract class ExcelSheetWithHighLightFactory extends ExcelSheetFactoryWrapper {


    protected abstract String getRule();
    protected abstract XSSFFontFormatting setHighlightFontFormatting(XSSFFontFormatting fontFormatting);


    public ExcelSheetWithHighLightFactory(ExcelSheetFactory excelSheetFactory) {
        super(excelSheetFactory);
    }

    @Override
    protected XSSFSheet addFunctionalityToSheet(XSSFSheet sheet) {

        XSSFSheetConditionalFormatting sheetConditionalFormatting = sheet.getSheetConditionalFormatting();
        XSSFConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(getRule());
        rule = addHighlightFontFormatting(rule);
        CellRangeAddress[] ranges = {ExcelUtils.getAllSheetRange(sheet)};

        sheetConditionalFormatting.addConditionalFormatting(ranges, rule);

        return sheet;
    }

    private XSSFConditionalFormattingRule addHighlightFontFormatting(XSSFConditionalFormattingRule rule){

        XSSFFontFormatting fontFormatting = rule.createFontFormatting();
        setHighlightFontFormatting(fontFormatting);

        return rule;

    }


}
