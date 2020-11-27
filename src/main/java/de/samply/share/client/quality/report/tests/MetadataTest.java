package de.samply.share.client.quality.report.tests;/*
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


import de.samply.share.client.quality.report.file.id.path.IdPathManager_002;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManagerImpl;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager_002;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;

@Path("/metadata-test")
public class MetadataTest {

    @GET
    public String myTest(@QueryParam("fileId") String fileId) throws QualityReportFileManagerException {

        MetadataTxtColumnManager_002 metadataTxtColumnManager = new MetadataTxtColumnManager_002();
        IdPathManager_002 idPathManager = new IdPathManager_002();
        QualityReportMetadataFileManager qualityReportMetadataFileManager = new QualityReportMetadataFileManagerImpl<>(metadataTxtColumnManager, idPathManager);

        QualityReportMetadata qualityReportMetadata = new QualityReportMetadata();
        qualityReportMetadata.setCreationTimestamp(new Date());
        qualityReportMetadata.setFileId(fileId);


        qualityReportMetadataFileManager.write(qualityReportMetadata, fileId);

        QualityReportMetadata qualityReportMetadata1 = qualityReportMetadataFileManager.read(fileId);
        List<QualityReportMetadata> qualityReportMetadatas = qualityReportMetadataFileManager.readAll();

        return "Metadata Test!";

    }

}
