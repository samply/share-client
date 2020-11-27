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

public class PatientDataCsvLinePattern_Test1 extends CsvLineImpl {

    public static final String MATCH = "match";
    public static final String MISMATCH = "basic";


    private enum ELEMENT_ORDER {
        MDR_ID, MDR_NAME, MDR_LINK, ATTRIBUTE_VALUE, MDR_TYPE, IS_VALID, NUMBER_OF_PATIENTS
    }

    public PatientDataCsvLinePattern_Test1() {
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
        return (element != null) ? convertMatchToIsValid(element) : null;

    }

    public void setValid(Boolean valid) {
        addElement(ELEMENT_ORDER.IS_VALID, convertIsValidToMatch(valid));
    }

    private String convertIsValidToMatch (boolean isValid){
        return (isValid) ? MATCH : MISMATCH;
    }

    private boolean convertMatchToIsValid (String match){
        return match.equals(MATCH);
    }

    public Integer getNumberOfPatients() {

        String element = getElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS);
        return (element != null) ? Integer.valueOf(element) : null;

    }

    public void setNumberOfPatients(Integer numberOfPatients) {
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS, numberOfPatients.toString());
    }

    public void setMdrName (String mdrName){
        addElement(ELEMENT_ORDER.MDR_NAME, mdrName);
    }

    public String getMdrName (){
        return getElement(ELEMENT_ORDER.MDR_NAME);
    }

    public void setMdrLink (String mdrLink){
        addElement(ELEMENT_ORDER.MDR_LINK, mdrLink);
    }

    public String getMdrLink (){
        return getElement(ELEMENT_ORDER.MDR_LINK);
    }

    public void setMdrType (String mdrType){
        addElement(ELEMENT_ORDER.MDR_TYPE, mdrType);
    }

    public String getMdrType (){
        return getElement(ELEMENT_ORDER.MDR_TYPE);
    }


}
