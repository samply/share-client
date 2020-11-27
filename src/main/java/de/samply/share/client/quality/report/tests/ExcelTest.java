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
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.centraxx.CentraXxMapperException;
import de.samply.share.client.quality.report.centraxx.CentraXxMapperImpl;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_ConverterImpl;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager_002;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern_002;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryException;
import de.samply.share.client.quality.report.file.id.path.IdPathManager_002;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.reader.ModelReader;
import de.samply.share.client.quality.report.model.reader.ModelReaderException;
import de.samply.share.client.quality.report.model.reader.QualityReportModelReaderImpl;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.util.db.ConfigurationUtil;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/excel-test")
public class ExcelTest {


    private ModelSearcher modelSearcher;
    private QualityReportFileManager qualityFileManager;
    private ExcelQualityReportFileManager excelQualityFileManager;
    private MdrClient mdrClient;
    private DktkId_MdrId_Converter dktkIdManager;



    public ExcelTest() throws CentraXxMapperException {

        Model model = getModel();

        modelSearcher = new ModelSearcher(model);
        mdrClient = getMdrClient();
        dktkIdManager = new DktkId_MdrId_ConverterImpl(mdrClient);


        IdPathManager_002 idPathManager = new IdPathManager_002();
        qualityFileManager = new CsvQualityReportFileManager(new QualityResultCsvLineManager_002(), idPathManager);

        ExcelPattern excelPattern = new ExcelPattern_002(model, mdrClient, new CentraXxMapperImpl(), dktkIdManager, new MdrIgnoredElements());
        excelQualityFileManager = new ExcelQualityReportFileManager(excelPattern, idPathManager);

    }

    @GET
    public String myTest(@QueryParam("fileId") String fileId) throws QualityReportFileManagerException, ExcelWorkbookFactoryException {



        QualityResults qualityResults = qualityFileManager.readFile(fileId);

        excelQualityFileManager.writeFile(qualityResults, fileId);

        return fileId;

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




}
