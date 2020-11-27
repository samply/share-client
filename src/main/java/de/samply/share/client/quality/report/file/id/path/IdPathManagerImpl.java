package de.samply.share.client.quality.report.file.id.path;/*
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

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.id.filename.QualityReportFilePattern;
import de.samply.share.client.quality.report.file.id.filename.QualityReportFilenameFormat;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager;
import de.samply.share.client.util.db.ConfigurationUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class IdPathManagerImpl<I extends QualityResultCsvLineManager, J extends ExcelPattern, K extends MetadataTxtColumnManager> implements IdPathManager {

    private QualityReportFilenameFormat csvFormat;
    private QualityReportFilenameFormat excelFormat;
    private QualityReportFilenameFormat metadataFormat;

    public abstract Class<I> getQualityResultCsvLineManagerClass();
    public abstract Class<J> getExcelPatternClass();
    public abstract Class<K> getMetadataTxtColumnManager();

    private String mainDirectory;
    {
        mainDirectory = getMainDirectory();

        csvFormat = getQualityReportFilenameFormat(getQualityResultCsvLineManagerClass());
        excelFormat = getQualityReportFilenameFormat(getExcelPatternClass());
        metadataFormat = getQualityReportFilenameFormat(getMetadataTxtColumnManager());

    }

    private QualityReportFilenameFormat getQualityReportFilenameFormat (Class<? extends QualityReportFilePattern> patternClass){
        return QualityReportFilenameFormat.getQualityReportFilenameFormat(patternClass);
    }

    private String getMainDirectory(){

        String mainDirectory = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
        if (mainDirectory == null){
            mainDirectory = ".";
        }

        return mainDirectory;

    }

    @Override
    public String getCsvFilePath(String fileId) {
        return getFilePath (fileId, csvFormat);
    }

    @Override
    public String getExcelFilePath(String fileId) {
        return getFilePath (fileId, excelFormat);
    }

    @Override
    public String getMetadataFilePath (String fileId){
        return getFilePath(fileId, metadataFormat);
    }

    @Override
    public String getFileId(String filePath) {

        if (filePath != null){

            int index = filePath.lastIndexOf(File.separator);
            filePath = filePath.substring(index + 1);

            return QualityReportFilenameFormat.getFileId(filePath);

        }

        return null;

    }

    private String getFilePath(String fileId, QualityReportFilenameFormat qualityFileFormat){


        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(mainDirectory);
        stringBuilder.append(File.separator);
        stringBuilder.append(qualityFileFormat.getFileName(fileId));

        return stringBuilder.toString();

    }

    @Override
    public List<String> getAllMetadataFilePaths() {

        List<String> filePaths = new ArrayList<>();

        File mainDirectory = new File(this.mainDirectory);

        for (File file : mainDirectory.listFiles()){

            String filename = file.getName();
            QualityReportFilenameFormat qualityReportFilenameFormat = QualityReportFilenameFormat.getQualityReportFilenameFormat(filename);

            if (qualityReportFilenameFormat != null && qualityReportFilenameFormat.isMetafile()){
                filePaths.add(file.getAbsolutePath());
            }
        }

        return filePaths;

    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    @Override
    public String getCurrentQualityReportVersion() {
        return excelFormat.getVersion();
    }
}
