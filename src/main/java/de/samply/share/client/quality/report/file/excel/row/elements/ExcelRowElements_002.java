package de.samply.share.client.quality.report.file.excel.row.elements;/*
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

import de.samply.share.client.quality.report.file.excel.cell.element.*;
import de.samply.share.client.quality.report.file.excel.cell.reference.CellReference;
import de.samply.share.client.quality.report.file.excel.cell.reference.CellReferenceExcelCellElement;
import de.samply.share.common.utils.MdrIdDatatype;

public class ExcelRowElements_002 extends ExcelRowElements {

    public enum ELEMENT_ORDER {

        MDR_LINK ("id"),
        DKTK_ID ("DKTK-id"),
        MDR_DATEN_ELEMENT("dataelement MDR"),
        CXX_DATEN_ELEMENT ("dataelement CXX"),
        MDR_TYPE ("datatype MDR"),
        MDR_ATTRIBUTE_VALUE("value MDR"),
        CXX_ATTRIBUTE_VALUE("value CXX"),
        IS_VALID ("validation"),
        NUMBER_OF_PATIENTS ("number of patients CXX"),
        PERCENTAGE_OUT_OF_PATIENTS_WITH_DATA_ELEMENT("% of patients with entry for this element"),
        PERCENTAGE_OUT_OF_TOTAL_PATIENTS("% of patients - total")
        ;

        private final String title;

        ELEMENT_ORDER(String title) {
            this.title = title;
        }

        public String getTitle(){
            return title;
        }
    }


    public ExcelRowElements_002() {
        super(ELEMENT_ORDER.values().length);
    }

    @Override
    public ExcelCellElement getElementTitle(int order) {

        String title =  (order >= 0 && order < ELEMENT_ORDER.values().length) ?  ELEMENT_ORDER.values()[order].getTitle() : "";
        return new StringExcelCellElement(title);

    }

    private void addElement (ELEMENT_ORDER elementOrder, ExcelCellElement element){
        addElement(elementOrder.ordinal(), element);
    }

    private ExcelCellElement getElement (ELEMENT_ORDER elementOrder){
        return getElement(elementOrder.ordinal());
    }


    public void setMdrDatenElement(String mdrDatenElement){

        StringExcelCellElement cellElement = new StringExcelCellElement(mdrDatenElement);
        addElement(ELEMENT_ORDER.MDR_DATEN_ELEMENT, cellElement);

    }

    public void setCxxDatenElement(String cxxDatenElement){

        StringExcelCellElement cellElement = new StringExcelCellElement(cxxDatenElement);
        addElement(ELEMENT_ORDER.CXX_DATEN_ELEMENT, cellElement);

    }

    public void setDktkId(String dktkId){

        StringExcelCellElement cellElement = new StringExcelCellElement(dktkId);
        addElement(ELEMENT_ORDER.DKTK_ID, cellElement);

    }

    public void setMdrLink (String link, MdrIdDatatype mdrId){

        String title = getLinkTitle(mdrId);
        LinkExcelCellElement cellElement = new LinkExcelCellElement(link, title);
        addElement(ELEMENT_ORDER.MDR_LINK, cellElement);

    }

    private String getLinkTitle (MdrIdDatatype mdrId){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(mdrId.getNamespace());
        stringBuilder.append(':');
        stringBuilder.append(mdrId.getId());
        stringBuilder.append(':');
        stringBuilder.append(mdrId.getVersion());

        return stringBuilder.toString();

    }

    public void setMdrAttributeValue(String mdrAttributeValue){

        StringExcelCellElement cellElement = new StringExcelCellElement(mdrAttributeValue);
        addElement(ELEMENT_ORDER.MDR_ATTRIBUTE_VALUE, cellElement);

    }

    public void setCxxAttributeValue(String cxxAttributeValue){

        StringExcelCellElement cellElement = new StringExcelCellElement(cxxAttributeValue);
        addElement(ELEMENT_ORDER.CXX_ATTRIBUTE_VALUE, cellElement);

    }

    public void setMdrType (String mdrType){

        StringExcelCellElement cellElement = new StringExcelCellElement(mdrType);
        addElement(ELEMENT_ORDER.MDR_TYPE, cellElement);

    }

    public void setValid (boolean isValid, int numberOfPatients){

        MatchExcelCellElement cellElement = new MatchExcelCellElement(isValid, numberOfPatients);
        addElement(ELEMENT_ORDER.IS_VALID, cellElement);

    }

    public void setNotMapped (){

        MatchExcelCellElement cellElement = new MatchExcelCellElement(true, 0);
        cellElement.setNotMapped();

        addElement(ELEMENT_ORDER.IS_VALID, cellElement);

    }

    public void setNumberOfPatients (int numberOfPatients){
        IntegerExcelCellElement cellElement = new IntegerExcelCellElement(numberOfPatients);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS, cellElement);
    }

    public void setNumberOfPatients (CellReference cellReference, int numberOfPatients){

        CellReferenceExcelCellElement cellElement = new CellReferenceExcelCellElement(cellReference, "" + numberOfPatients);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS, cellElement);

    }

    public void setPercentageOutOfPatientsWithDataElement(Double percentageOfPatientsWithDataElement){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfPatientsWithDataElement);
        addElement(ELEMENT_ORDER.PERCENTAGE_OUT_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

    }

    public void setPercentageOutOfTotalPatients(Double percentageOfTotalPatients){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfTotalPatients);
        addElement(ELEMENT_ORDER.PERCENTAGE_OUT_OF_TOTAL_PATIENTS, excelCellElement);

    }



}
