package de.samply.share.client.quality.report.file.excel.instances.statistics.table2;/*
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

public class Table2_ExcelRowElements extends ExcelRowElements {

    public enum ELEMENT_ORDER {

        MDR_LINK ("id"),
        DKTK_ID ("DKTK-id"),
        MDR_DATEN_ELEMENT("dataelement MDR"),
        CXX_DATEN_ELEMENT ("dataelement CXX"),
        PATIENTS_FOR_VALIDATION ("Patienten zu Validierung"),
        PATIENTS_FOR_ID ("Patienten zu ID"),
        RATIO ("Anteil"),
        PERCENTAGE_OF_TOTAL_PATIENTS ("Percentage of total patients"),
        NUMBER_OF_PATIENTS_WITH_MATCH ("Number of patients with match"),
        NUMBER_OF_PATIENTS_WITH_MISMATCH ("Number of patients with mismatch"),
        PERCENTAGE_OF_PATIENTS_WITH_MATCH ("Percentage of patients with match"),
        PERCENTAGE_OF_PATIENTS_WITH_MISMATCH ("Percentage of patients with mismatch");


        private final String title;

        ELEMENT_ORDER(String title) {
            this.title = title;
        }

        public String getTitle(){
            return title;
        }
    }


    public Table2_ExcelRowElements() {
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


    public void setPatientsForValidation (Integer patientsForValidation){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(patientsForValidation);
        addElement(ELEMENT_ORDER.PATIENTS_FOR_VALIDATION, excelCellElement);

    }

    public void setPatientsForId (Integer patientsForId){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(patientsForId);
        addElement(ELEMENT_ORDER.PATIENTS_FOR_ID, excelCellElement);

    }

    public void setRatio (Double ratio){

        DoubleExcelCellElement doubleExcelCellElement = new DoubleExcelCellElement(ratio);
        addElement(ELEMENT_ORDER.RATIO, doubleExcelCellElement);

    }

    public void setNumberOfPatientsWithMismatch (Integer numberOfPatientsWithMismatch){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(numberOfPatientsWithMismatch);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS_WITH_MISMATCH, excelCellElement);

    }

    public void setNumberOfPatientsWithMatch (Integer numberOfPatientsWithMatch){

        IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(numberOfPatientsWithMatch);
        addElement(ELEMENT_ORDER.NUMBER_OF_PATIENTS_WITH_MATCH, excelCellElement);

    }

    public void setPercentageOfPatientsWithMismatch (Double percentageOfPatientsWithMismatch){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfPatientsWithMismatch);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_MISMATCH, excelCellElement);

    }

    public void setPercentageOfPatientsWithMatch (Double percentageOfPatientsWithMatch){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfPatientsWithMatch);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_PATIENTS_WITH_MATCH, excelCellElement);

    }

    public void setPercentageOfTotalPatients (Double percentageOfTotalPatients){

        DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfTotalPatients);
        addElement(ELEMENT_ORDER.PERCENTAGE_OF_TOTAL_PATIENTS, excelCellElement);

    }


}
