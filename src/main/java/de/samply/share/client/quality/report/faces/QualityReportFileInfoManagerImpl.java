package de.samply.share.client.quality.report.faces;/*
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
import de.samply.share.client.quality.report.file.id.path.IdPathManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import de.samply.share.client.util.db.ConfigurationUtil;
import org.apache.commons.io.FilenameUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class QualityReportFileInfoManagerImpl implements QualityReportFileInfoManager {

    private QualityReportMetadataFileManager qualityReportMetadataFileManager;
    private IdPathManager idPathManager;

    public QualityReportFileInfoManagerImpl(QualityReportMetadataFileManager qualityReportMetadataFileManager, IdPathManager idPathManager) {
        this.qualityReportMetadataFileManager = qualityReportMetadataFileManager;
        this.idPathManager = idPathManager;
    }

    @Override
    public List<QualityReportFileInfo> getQualityReportFiles() throws QualityReportFileInfoManagerException {

        List<QualityReportFileInfo> qualityReportFiles = new ArrayList<>();

        for (QualityReportMetadata qualityReportMetadata : readQualityReportMetadatas()){

            QualityReportFileInfo qualityReportFile = convert(qualityReportMetadata);
            qualityReportFiles.add(qualityReportFile);

        }

        return sort(qualityReportFiles);

    }
    
    private List<QualityReportFileInfo> sort (List<QualityReportFileInfo> qualityReportFileInfos){

        Collections.sort(qualityReportFileInfos, new QualityReportFileInfoComparator());
        Collections.reverse(qualityReportFileInfos);

        return qualityReportFileInfos;

    }

    private List<QualityReportMetadata> readQualityReportMetadatas() throws QualityReportFileInfoManagerException {

        try {
            return qualityReportMetadataFileManager.readAll();
        } catch (de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException e) {
            throw new QualityReportFileInfoManagerException(e);
        }

    }

    private QualityReportFileInfo convert (QualityReportMetadata qualityReportMetadata){

        QualityReportFileInfo qualityReportFile = new QualityReportFileInfo();

        Date creationTimestamp = qualityReportMetadata.getCreationTimestamp();
        String fileId = qualityReportMetadata.getFileId();
        String sqlMappingVersion = qualityReportMetadata.getSqlMappingVersion();

        String excelFilePath = idPathManager.getExcelFilePath(fileId);

        String fileExtension = FilenameUtils.getExtension(excelFilePath);

        String filename = createFilename(creationTimestamp, fileExtension, sqlMappingVersion);
        String version = qualityReportMetadata.getQualityReportVersion();

        qualityReportFile.setTimestamp(creationTimestamp);
        qualityReportFile.setLink(excelFilePath);
        qualityReportFile.setFilename(filename);
        qualityReportFile.setVersion(version);

        return (fileId != null && creationTimestamp != null) ? qualityReportFile : null;

    }

    private String createFilename (Date timestamp, String extension, String sqlMappingVersion){

        String sTimestamp = getTimestampForFilename(timestamp);
        String location = getLocation();
        String basicFilename = getBasicFilename();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(basicFilename);

        stringBuilder = appendElementToFileName(stringBuilder, location);
        stringBuilder = appendElementToFileName(stringBuilder, sTimestamp);
        stringBuilder = appendElementToFileName(stringBuilder, sqlMappingVersion);

        stringBuilder.append('.');
        stringBuilder.append(extension);

        return stringBuilder.toString();

    }

    private StringBuilder appendElementToFileName(StringBuilder fileName, String element){

        if (element != null) {

            fileName.append('-');
            fileName.append(element);

        }

        return fileName;

    }

    private String getBasicFilename(){

        String basicFilename = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_BASIC_FILENAME);
        return (basicFilename != null) ? basicFilename.replaceAll("\\s+","") : "quality-report";

    }

    private String getTimestampForFilename (Date timestamp){

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.ENGLISH);
        return simpleDateFormat.format(timestamp);

    }

    private String getLocation(){

        String location = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LOCATION);

        if (location != null){
            location = location.replaceAll("\\s+","");
        }

        return location;
    }


}
