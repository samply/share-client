package de.samply.share.client.quality.report.file.id.filename;/*
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

import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerImpl_Test1;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager_001;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager_002;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern_001;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern_002;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager_001;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager_002;

public enum QualityReportFilenameFormat {

    CSV_001("csv", "001", QualityResultCsvLineManager_001.class),
    CSV_002("csv", "002", QualityResultCsvLineManager_002.class),
    CSV_TEST1 ("csv", "TEST1", QualityResultCsvLineManagerImpl_Test1.class),
    META_001("txt", "META_001", MetadataTxtColumnManager_001.class),
    META_002("txt", "META_002", MetadataTxtColumnManager_002.class),
    XLSX_001("xlsx", "001", ExcelPattern_001.class),
    XLSX_002("xlsx", "002", ExcelPattern_002.class);

    private static String META = "META";
    private String extension;
    private String version;
    private Class<? extends QualityReportFilePattern> patternClass;

    QualityReportFilenameFormat(String extension, String version, Class<? extends QualityReportFilePattern> patternClass){

        this.extension = extension;
        this.version = version;
        this.patternClass = patternClass;

    }

    public String getFileName (String fileId){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileId);
        stringBuilder.append('_');
        stringBuilder.append(version);
        stringBuilder.append('.');
        stringBuilder.append(extension);

        return stringBuilder.toString();

    }

    public String getVersion(){
        return version;
    }

    public static QualityReportFilenameFormat getQualityReportFilenameFormat (String extension, String version){

        for (QualityReportFilenameFormat qualityReportFilenameFormat : values()){

            if (qualityReportFilenameFormat.extension.equals(extension) && qualityReportFilenameFormat.version.equals(version)){
                return qualityReportFilenameFormat;
            }
        }

        return null;

    }

    public static QualityReportFilenameFormat getQualityReportFilenameFormat (String filename){

        if (filename != null){

            int index = filename.indexOf('_');
            if (index > 0 && filename.length() > index + 2){
                filename = filename.substring(index + 1);
                String[] split = filename.split("\\.");

                if (split.length == 2){

                    String version = split[0];
                    String extension = split[1];

                    return getQualityReportFilenameFormat(extension, version);
                }
            }

        }

        return null;
    }

    public static String getVersion (String filename){

        QualityReportFilenameFormat qualityReportFilenameFormat = getQualityReportFilenameFormat(filename);
        return (qualityReportFilenameFormat != null) ? qualityReportFilenameFormat.version : null;

    }

    public static String getFileId (String filePath){

        String fileId = null;

        if (filePath != null){
            int index = filePath.indexOf("_");
            fileId = filePath.substring(0, index);
        }

        return fileId;

    }

    public static QualityReportFilenameFormat getQualityReportFilenameFormat (Class<? extends QualityReportFilePattern> patternClass){

        for (QualityReportFilenameFormat qualityReportFilenameFormat : values()){
            if (patternClass.equals(qualityReportFilenameFormat.patternClass)){
                return qualityReportFilenameFormat;
            }
        }

        return null;
    }

    public boolean isMetafile (){
        return version.contains(META);
    }

}
