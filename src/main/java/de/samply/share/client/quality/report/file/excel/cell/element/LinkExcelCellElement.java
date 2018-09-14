package de.samply.share.client.quality.report.file.excel.cell.element;/*
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

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

public class LinkExcelCellElement<T> extends ExcelCellElement<T> {

    private String link;


    protected HyperlinkType getHyperlinkType (){
        return HyperlinkType.URL;
    }

    public LinkExcelCellElement(String link, T element) {
        super(element);
        this.link = link;
    }

    @Override
    protected Cell setCellValue(Cell cell) {

        Hyperlink link = createHyperlink(cell);

        String title = convertElementToString();

        cell.setHyperlink(link);
        cell.setCellValue(title);

        return cell;

    }

    private Hyperlink createHyperlink(Cell cell){

        CreationHelper creationHelper = cell.getSheet().getWorkbook().getCreationHelper();
        Hyperlink hyperlink = creationHelper.createHyperlink(getHyperlinkType());
        hyperlink.setAddress(link);

        return hyperlink;

    }


}
