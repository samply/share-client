package de.samply.share.client.quality.report.file.manager;/*
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

import de.samply.share.client.quality.report.file.id.path.IdPathManager;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QualityReportMetadataFileManagerImpl<I extends MetadataTxtColumnManager> implements QualityReportMetadataFileManager {

    private IdPathManager idPathManager;
    private I metadataTxtColumnManager;

    public QualityReportMetadataFileManagerImpl(I metadataTxtColumnManager,IdPathManagerImpl<?,?,I> idPathManager) {

        this.idPathManager = idPathManager;
        this.metadataTxtColumnManager = metadataTxtColumnManager;

    }

    @Override
    public void write(QualityReportMetadata qualityReportMetadata, String fileId) throws QualityReportFileManagerException {

        String metadataFilePath = idPathManager.getMetadataFilePath(fileId);
        qualityReportMetadata.setQualityReportVersion(idPathManager.getCurrentQualityReportVersion());

        writeQualityReportMetadata(qualityReportMetadata, metadataFilePath);

    }

    private void writeQualityReportMetadata (QualityReportMetadata qualityReportMetadata, String filePath) throws QualityReportFileManagerException {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))){

            write(qualityReportMetadata, bufferedWriter);

        } catch (IOException e) {
            throw new QualityReportFileManagerException(e);
        }
    }

    private void write (QualityReportMetadata qualityReportMetadata, BufferedWriter bufferedWriter) throws IOException {

        String column = metadataTxtColumnManager.createColumn(qualityReportMetadata);
        bufferedWriter.write(column);
        bufferedWriter.flush();

    }

    @Override
    public QualityReportMetadata read(String fileId) throws QualityReportFileManagerException {

        String filePath = idPathManager.getMetadataFilePath(fileId);
        return readQualityReportMetadata(filePath);

    }

    @Override
    public List<QualityReportMetadata> readAll() throws QualityReportFileManagerException {

        List<QualityReportMetadata> qualityReportMetadatas = new ArrayList<>();

        for (String metadataFilePath : getAllMetadataFilePaths()){

            QualityReportMetadata qualityReportMetadata = readQualityReportMetadata(metadataFilePath);
            qualityReportMetadatas.add(qualityReportMetadata);

        }


        return qualityReportMetadatas;
    }

    private List<String> getAllMetadataFilePaths() throws QualityReportFileManagerException {

        try{

            return idPathManager.getAllMetadataFilePaths();

        }catch (Exception e){
            throw new QualityReportFileManagerException(e);
        }

    }

    private QualityReportMetadata readQualityReportMetadata (String filePath) throws QualityReportFileManagerException {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))){

            return read(bufferedReader);

        } catch (IOException e) {
            throw new QualityReportFileManagerException(e);
        }

    }

    private QualityReportMetadata read (BufferedReader bufferedReader) throws IOException {

        String column = IOUtils.toString (bufferedReader);
        return metadataTxtColumnManager.parseValuesOfColumn(column);

    }




}
