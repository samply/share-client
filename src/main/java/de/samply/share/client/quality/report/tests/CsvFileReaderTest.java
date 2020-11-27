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

import de.samply.common.http.HttpConnector;
import de.samply.common.mdrclient.MdrClient;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerImpl_Test1;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager_002;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.file.id.path.IdPathManager_002;
import de.samply.share.client.quality.report.file.id.path.IdPathManager_Test1;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.reader.ModelReader;
import de.samply.share.client.quality.report.model.reader.ModelReaderException;
import de.samply.share.client.quality.report.model.reader.QualityReportModelReaderImpl;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidDateFilter;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidIntegerFilter;
import de.samply.share.client.util.db.ConfigurationUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/patient-data-f100")
public class CsvFileReaderTest {

    private final static String FILE_ID_SUFFIX = "_2";

    private ModelSearcher modelSearcher;

    private QualityReportFileManager qualityFileManager1;
    private QualityReportFileManager qualityFileManager2;

    {
        Model model = getModel();

        qualityFileManager1 = createQualityFileManager(new QualityResultCsvLineManager_002(), new IdPathManager_002());
        qualityFileManager2 = createQualityFileManager(new QualityResultCsvLineManagerImpl_Test1(model, getMdrClient()), new IdPathManager_Test1());

        modelSearcher = new ModelSearcher(model);

    }

    private Model getModel(){
        try {

            ModelReader modelReader = new QualityReportModelReaderImpl();
            return modelReader.getModel();

        } catch (ModelReaderException e) {
            e.printStackTrace();
            return null;
        }
    }

    private MdrClient getMdrClient(){

        try {

            String mdrUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL);
            HttpConnector httpConnector = ApplicationBean.getHttpConnector();

            return new MdrClient(mdrUrl, httpConnector.getClient(httpConnector.getHttpClient(mdrUrl)));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @GET
    public String myTest(@QueryParam("fileId") String fileId) throws QualityReportFileManagerException {

        String newFileId = getNewFileId(fileId);

        QualityResults qualityResults = qualityFileManager1.readFile(fileId);

        qualityResults = new QualityResultsValidDateFilter(qualityResults, modelSearcher);
        qualityResults = new QualityResultsValidIntegerFilter(qualityResults, modelSearcher);

        qualityFileManager2.writeFile(qualityResults, newFileId);

        return newFileId;
    }

    private String getNewFileId (String fileId){
        return fileId + FILE_ID_SUFFIX;
    }

    private QualityReportFileManager createQualityFileManager(QualityResultCsvLineManager qualityResultsCsvLineManager, IdPathManagerImpl idPathManager) {

        return new CsvQualityReportFileManager(qualityResultsCsvLineManager, idPathManager);

    }



}
