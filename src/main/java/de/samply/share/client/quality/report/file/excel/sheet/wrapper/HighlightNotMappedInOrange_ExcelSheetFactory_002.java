package de.samply.share.client.quality.report.file.excel.sheet.wrapper;/*
 * Copyright (C) 2018 Medizinische Informatik in der Translationalen Onkologie,
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

import de.samply.share.client.quality.report.file.excel.cell.element.MatchElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements_002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFontFormatting;

public class HighlightNotMappedInOrange_ExcelSheetFactory_002 extends ExcelSheetWithHighLightFactory {


    public HighlightNotMappedInOrange_ExcelSheetFactory_002(ExcelSheetFactory excelSheetFactory) {
        super(excelSheetFactory);
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

    private String getColumnToBeHighlighted(){

        int columnNumber = getColumnNumberToBeHighlighted();
        return CellReference.convertNumToColString(columnNumber);

    }

    private int getColumnNumberToBeHighlighted(){
        return ExcelRowElements_002.ELEMENT_ORDER.IS_VALID.ordinal();
    }

    @Override
    protected XSSFFontFormatting setHighlightFontFormatting(XSSFFontFormatting fontFormatting) {

        fontFormatting.setFontColorIndex(IndexedColors.ORANGE.getIndex());
        return fontFormatting;

    }

}
