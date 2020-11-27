package de.samply.share.client.quality.report.file.csvline;/*
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


import org.apache.commons.collections.FastHashMap;

import java.util.Map;

public class CsvLineImpl implements CsvLine {


    private char csvFileSeparator = '\t';
    private int maxNumberOfElements;

    private Map<Integer, String> elements = new FastHashMap();


    public CsvLineImpl(int maxNumberOfElements) {
        this.maxNumberOfElements = maxNumberOfElements;
    }

    protected void addElement (int order, String element){
        if (element != null && order < maxNumberOfElements) {
            elements.put(order, element);
        }
    }

    protected String getElement (int order){
        return (order < maxNumberOfElements) ?  elements.get(order) : null;
    }

    @Override
    public String createLine() {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < maxNumberOfElements; i++){

            String element = elements.get(i);
            if (element != null){
                stringBuilder.append(element);
            }
            if (i + 1 < maxNumberOfElements) {
                stringBuilder.append(csvFileSeparator);
            }

        }

        stringBuilder.append('\n');

        return stringBuilder.toString();

    }

    @Override
    public void parseValuesOfLine(String line) {

        if (line != null){

            line = line.substring(0, line.length() - 1); // ignore new line character
            String[] splitLine = line.split(String.valueOf(csvFileSeparator));

            for (int i = 0; i < maxNumberOfElements; i++){

                if (i < splitLine.length) {

                    String element = splitLine[i];
                    if (element != null && element.length() > 0) {
                        elements.put(i, element);
                    }

                }

            }

        }
    }

    public void setCsvFileSeparator(char csvFileSeparator) {
        this.csvFileSeparator = csvFileSeparator;
    }
}
