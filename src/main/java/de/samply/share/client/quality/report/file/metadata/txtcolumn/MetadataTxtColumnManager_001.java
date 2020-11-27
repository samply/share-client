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

import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;

import java.util.Date;

public class MetadataTxtColumnManager_001 implements MetadataTxtColumnManager {


    @Override
    public String createColumn(QualityReportMetadata qualityReportMetadata) {

        MetadataTxtColumn metadataTxtColumn = new MetadataTxtColumn();

        Date creationTimestamp = qualityReportMetadata.getCreationTimestamp();
        String fileId = qualityReportMetadata.getFileId();
        String sqlMappingVersion = qualityReportMetadata.getSqlMappingVersion();

        metadataTxtColumn.setTimestamp(creationTimestamp);
        metadataTxtColumn.setFileId(fileId);
        metadataTxtColumn.setSqlMappingVersion(sqlMappingVersion);

        return metadataTxtColumn.createColumn();

    }

    @Override
    public QualityReportMetadata parseValuesOfColumn(String column) {

        MetadataTxtColumn metadataTxtColumn = new MetadataTxtColumn();
        metadataTxtColumn.parseValuesOfColumn(column);
        Date timestamp = metadataTxtColumn.getTimestamp();
        String fileId = metadataTxtColumn.getFileId();
        String sqlMappingVersion = metadataTxtColumn.getSqlMappingVersion();

        QualityReportMetadata qualityReportMetadata = new QualityReportMetadata();
        qualityReportMetadata.setCreationTimestamp(timestamp);
        qualityReportMetadata.setFileId(fileId);
        qualityReportMetadata.setSqlMappingVersion(sqlMappingVersion);

        return qualityReportMetadata;

    }

}
