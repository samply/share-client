package de.samply.share.client.quality.report.file.metadata.txtcolumn;/*
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

import de.samply.share.client.quality.report.file.txtcolumn.TxtColumnImpl;
import de.samply.share.client.util.Utils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MetadataTxtColumn extends TxtColumnImpl {

    private enum ELEMENT_ORDER {

        FILE_ID ("file-id"),
        TIMESTAMP ("timestamp"),
        SQL_MAPPING_VERSION ("sql-mapping-version"),
        QUALITY_REPORT_VERSION("quality-report-version");

        private String title;
        private static Map<String, Integer> titleAndOrdinals;

        ELEMENT_ORDER(String title) {
            this.title = title;
        }

        public String getTitle(){
            return title;
        }

        public static Integer getOrdinal (String title){

            if (titleAndOrdinals == null){

                titleAndOrdinals = new HashMap<>();

                for (ELEMENT_ORDER elementOrder : values()){
                    titleAndOrdinals.put(elementOrder.getTitle(), elementOrder.ordinal());
                }

            }

            return titleAndOrdinals.get(title);
        }


    }


    public MetadataTxtColumn() {
        super(ELEMENT_ORDER.values().length);
    }

    @Override
    protected String getElementTitle(int order) {
        return  (order >= 0 && order < ELEMENT_ORDER.values().length) ? ELEMENT_ORDER.values()[order].getTitle() : null;
    }

    @Override
    protected Integer getElementTitleOrder(String elementTitle) {
        return ELEMENT_ORDER.getOrdinal(elementTitle);
    }

    private String getElement (ELEMENT_ORDER order){
        return getElement(order.ordinal());
    }

    private void addElement (ELEMENT_ORDER order, String element){
        addElement(order.ordinal(), element);
    }

    public void setTimestamp (Date date){

        String sDate = Utils.convertDate3(date);
        addElement(ELEMENT_ORDER.TIMESTAMP, sDate);

    }

    public Date getTimestamp () {

        String sDate = getElement(ELEMENT_ORDER.TIMESTAMP);
        return (sDate != null) ? convert(sDate) : null;

    }

    public String getSqlMappingVersion (){
        return getElement(ELEMENT_ORDER.SQL_MAPPING_VERSION);
    }

    public void setSqlMappingVersion(String sqlMappingVersion){
        addElement(ELEMENT_ORDER.SQL_MAPPING_VERSION, sqlMappingVersion);
    }

    private Date convert (String date) {
        try {
            return Utils.convertDate3(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public void setFileId(String fileId){
        addElement(ELEMENT_ORDER.FILE_ID, fileId);
    }

    public String getFileId(){
        return getElement(ELEMENT_ORDER.FILE_ID);
    }

    public void setQualityReportVersion (String qualityReportVersion){
        addElement(ELEMENT_ORDER.QUALITY_REPORT_VERSION, qualityReportVersion);
    }

    public String getQualityReportVersion (){
        return getElement(ELEMENT_ORDER.QUALITY_REPORT_VERSION);
    }

}
