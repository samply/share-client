package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;/*
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

import de.samply.share.client.quality.report.file.excel.cell.element.*;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.common.utils.MdrIdDatatype;

public class DataElementStats_ExcelRowElements extends ExcelRowElements {

    public enum ELEMENT_ORDER {

        MDR_LINK ("id"),
        DKTK_ID ("DKTK-id"),
        MDR_DATEN_ELEMENT("dataelement MDR"),
        CXX_DATEN_ELEMENT ("dataelement CXX"),
        NUMBER_OF_PATIENTS_WITH_DATA_ELEMENT ("Number of patients with entry for this element"),
        PERCENTAGE_OF_PATIENTS_WITH_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS ("% of patients - total"),
        NUMBER_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT ("Number of patients with match only"),
        PERCENTAGE_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT_OUT_OF_PATIENTS_WITH_DATA_ELEMENT ("% of patients with entry for this element"),
        PERCENTAGE_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS ("% of patients - total"),
        NUMBER_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT ("Number of patients with any mismatch"),
        PERCENTAGE_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT_OUT_OF_PATIENTS_WITH_DATA_ELEMENT ("% of patients with entry for this element"),
        PERCENTAGE_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS ("% of patients - total");


        private final String title;

        ELEMENT_ORDER(String title) {
            this.title = title;
        }

        public String getTitle(){
            return title;
        }
    }


    public DataElementStats_ExcelRowElements() {
        super(ELEMENT_ORDER.values().length);
    }

    private void addElement (ELEMENT_ORDER elementOrder, ExcelCellElement element){
        addElement(elementOrder.ordinal(), element);
    }

    @Override
    public ExcelCellElement getElementTitle(int order) {

        String title =  (order >= 0 && order < ELEMENT_ORDER.values().length) ?  ELEMENT_ORDER.values()[order].getTitle() : "";
        return new StringExcelCellElement(title);

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

    public void setDktkId(String dktkId){

        StringExcelCellElement cellElement = new StringExcelCellElement(dktkId);
        addElement(ELEMENT_ORDER.DKTK_ID, cellElement);

    }

    public void setMdrDatenElement(String mdrDatenElement){

        StringExcelCellElement cellElement = new StringExcelCellElement(mdrDatenElement);
        addElement(ELEMENT_ORDER.MDR_DATEN_ELEMENT, cellElement);

    }

    public void setCxxDatenElement(String cxxDatenElement){

        StringExcelCellElement cellElement = new StringExcelCellElement(cxxDatenElement);
        addElement(ELEMENT_ORDER.CXX_DATEN_ELEMENT, cellElement);

    }

    public void setNumberOf_PatientsWithDataElement (Integer numberOf_PatientsWithDataElement){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(numberOf_PatientsWithDataElement);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

    }

    public void setPercentageOf_PatientsWithDataElement_OutOf_TotalPatients ( Double percentageOf_PatientsWithDataElement_OutOf_TotalPatients ){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOf_PatientsWithDataElement_OutOf_TotalPatients);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS, excelCellElement);

    }

    public void setNumberOf_PatientsWithMatchOnlyForDataElement (Integer numberOf_PatientsWithMatchOnlyForDataElement){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(numberOf_PatientsWithMatchOnlyForDataElement);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT, excelCellElement);


    }

    public void setPercentageOf_PatientsWithMatchOnlyForDataElement_OutOf_PatientsWithDataElement (Double percentageOf_PatientsWithMatchOnlyForDataElement_OutOf_PatientsWithDataElement){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOf_PatientsWithMatchOnlyForDataElement_OutOf_PatientsWithDataElement);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT_OUT_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

    }

    public void setPercentageOf_PatientsWithMatchOnlyForDataElement_OutOf_TotalPatients (Double percentageOf_PatientsWithMatchOnlyForDataElement_OutOf_TotalPatients){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOf_PatientsWithMatchOnlyForDataElement_OutOf_TotalPatients);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS, excelCellElement);

    }

    public void setNumberOf_PatientsWithAnyMismatchForDataElement (Integer numberOf_PatientsWithAnyMismatchForDataElement){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(numberOf_PatientsWithAnyMismatchForDataElement);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT, excelCellElement);

    }

    public void setPercentageOf_PatientsWithAnyMismatchForDataElement_OutOf_PatientsWithDataElement (Double percentageOf_PatientsWithAnyMismatchForDataElement_OutOf_PatientsWithDataElement){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOf_PatientsWithAnyMismatchForDataElement_OutOf_PatientsWithDataElement);
        addElement (ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT_OUT_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

    }

    public void setPercentageOf_PatientsWithAnyMismatchForDataElement_outOf_totalPatients (Double percentageOf_PatientsWithAnyMismatchForDataElement_outOf_totalPatients){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOf_PatientsWithAnyMismatchForDataElement_outOf_totalPatients);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS, excelCellElement);

    }

}
