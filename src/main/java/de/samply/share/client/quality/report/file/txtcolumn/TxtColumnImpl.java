package de.samply.share.client.quality.report.file.txtcolumn;/*
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

public abstract class TxtColumnImpl implements TxtColumn{

    private int maxNumberOfElements;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");


    private Map<Integer, String> elements = new FastHashMap();

    public TxtColumnImpl(int maxNumberOfElements) {
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

    protected abstract String getElementTitle (int order);
    protected abstract Integer getElementTitleOrder (String elementTitle);

    @Override
    public String createColumn(){

        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i < maxNumberOfElements; i++){

            stringBuilder.append(getElementTitle(i));
            stringBuilder.append('=');

            String element = getElement(i);
            if (element != null){
                stringBuilder.append(element);
            }

            stringBuilder.append(LINE_SEPARATOR);

        }

        return stringBuilder.toString();

    }

    @Override
    public void parseValuesOfColumn(String column) {

        String[] split = column.split(LINE_SEPARATOR);

        for (String line : split){
            parseValuesOfLine(line);
        }

    }

    private void parseValuesOfLine (String line) {

        if (line != null && line.length() > 0){

            String[] split = line.split("=");

            if (split.length == 2 && split[1].length() > 0){

                String title = split[0];
                String value = split[1];

                Integer elementTitleOrder = getElementTitleOrder(title);

                if (elementTitleOrder != null) {
                    addElement(elementTitleOrder, value);
                }
            }

        }

    }

}
