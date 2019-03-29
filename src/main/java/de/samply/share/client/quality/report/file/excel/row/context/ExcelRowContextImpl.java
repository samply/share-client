package de.samply.share.client.quality.report.file.excel.row.context;/*
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

import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ExcelRowContextImpl<EXCEL_ROW_PARAMETERS> implements ExcelRowContext {


    protected abstract ExcelRowElements convert (EXCEL_ROW_PARAMETERS excelRowParameters) throws Exception;
    protected abstract org.apache.logging.log4j.Logger getLogger();

    protected List<EXCEL_ROW_PARAMETERS> excelRowParametersList = new ArrayList<>();

    public Integer getNumberOfRows(){
        return excelRowParametersList.size();
    }

    protected class ExcelRowContextIterator implements Iterator<ExcelRowElements>{

        private Iterator<EXCEL_ROW_PARAMETERS> excelRowParametersIterator;

        public ExcelRowContextIterator(Iterator<EXCEL_ROW_PARAMETERS> excelRowParametersIterator) {
            this.excelRowParametersIterator = excelRowParametersIterator;
        }

        @Override
        public boolean hasNext() {
            return excelRowParametersIterator.hasNext();
        }

        @Override
        public ExcelRowElements next() {

            EXCEL_ROW_PARAMETERS next = excelRowParametersIterator.next();
            return convertParametersToElements (next);

        }

        private ExcelRowElements convertParametersToElements (EXCEL_ROW_PARAMETERS excelRowParameters){
            try {

                return convert (excelRowParameters);

            } catch (Exception e) {

                getLogger().error(e.getMessage(), e);
                return createEmptyExcelRowElements();// If there is an exception, the exception is logged and the row is left clean in QB.

            }
        }

        @Override
        public void remove() {
            excelRowParametersIterator.remove();
        }

    }



    @Override
    public Iterator<ExcelRowElements> iterator() {
        return new ExcelRowContextIterator(excelRowParametersList.iterator());
    }

}
