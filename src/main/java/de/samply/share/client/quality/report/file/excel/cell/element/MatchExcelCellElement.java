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

import org.apache.poi.ss.usermodel.Cell;

public class MatchExcelCellElement extends ExcelCellElement<Boolean> {

    public final static String MATCH = "match";
    public final static String MISMATCH = "mismatch";
    public final static String NOT_MAPPED = "not mapped";
    public final static String NOT_FOUND = "not found";

    private boolean isNotMapped = false;
    private int numberOfElements = 0;


    public MatchExcelCellElement(Boolean element, int numberOfElements) {
        super(element);
        this.numberOfElements = numberOfElements;
    }

    @Override
    protected Cell setCellValue(Cell cell) {

        String value = getCellValue(element);
        cell.setCellValue(value);

        return cell;
    }

    private String getCellValue (boolean element){

        String result = null;

        if (isNotMapped){
            result = NOT_MAPPED;
        } else if (numberOfElements <= 0){
            result = NOT_FOUND;
        } else if (element){
            result = MATCH;
        } else{
            result = MISMATCH;
        }

        return result;

    }

    public void setNotMapped() {
        isNotMapped = true;
    }

}
