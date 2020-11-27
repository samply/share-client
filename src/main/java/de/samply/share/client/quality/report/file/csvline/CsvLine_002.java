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

import de.samply.share.common.utils.MdrIdDatatype;

public final class CsvLine_002 extends CsvLineImpl {


    private enum ELEMENT_ORDER {
        MDR_ID, ATTRIBUTE_VALUE, IS_VALID, NUMBER_OF_PATIENTS
    }

    public CsvLine_002() {
        super(ELEMENT_ORDER.values().length);
    }

    private String getElement (ELEMENT_ORDER order){
        return getElement(order.ordinal());
    }

    private void addElement (ELEMENT_ORDER order, String element){
        addElement(order.ordinal(), element);
    }


    public MdrIdDatatype getMdrId() {

        String element = getElement(ELEMENT_ORDER.MDR_ID);
        return (element != null) ? new MdrIdDatatype(element) : null;

    }

    public void setMdrId(MdrIdDatatype mdrId) {
        addElement(ELEMENT_ORDER.MDR_ID, mdrId.toString());
    }

    public String getAttributeValue() {
        return getElement(ELEMENT_ORDER.ATTRIBUTE_VALUE);
    }

    public void setAttributeValue(String attributeValue) {
        addElement(ELEMENT_ORDER.ATTRIBUTE_VALUE, attributeValue );
    }

    public Boolean isValid() {

        String element = getElement(ELEMENT_ORDER.IS_VALID);
        return (element != null) ? Boolean.valueOf(element) : null;

    }

    public void setValid(Boolean valid) {
        addElement(ELEMENT_ORDER.IS_VALID, valid.toString());
    }

    public Integer getNumberOfPatients() {

        String element = getElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS);
        return (element != null) ? Integer.valueOf(element) : null;

    }

    public void setNumberOfPatients(Integer numberOfPatients) {
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS, numberOfPatients.toString());
    }


}
