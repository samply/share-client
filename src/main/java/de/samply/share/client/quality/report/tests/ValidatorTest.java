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

import de.dth.mdr.validator.MDRValidator;
import de.dth.mdr.validator.MdrConnection;
import de.dth.mdr.validator.exception.MdrException;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.centraxx.CentraXxMapperException;
import de.samply.share.client.quality.report.chain.factory.ChainFactoryException;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager_002;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryException;
import de.samply.share.client.quality.report.file.id.path.IdPathManager_002;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.model.mdr.MdrConnectionFactory;
import de.samply.share.client.quality.report.model.mdr.MdrConnectionFactoryException;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidatorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.QueryValidator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.concurrent.ExecutionException;

@Path("/validator-test")
public class ValidatorTest {

    private QualityReportFileManager qualityFileManager;


    private QualityResultsValidator qualityResultsValidator;


    public ValidatorTest() throws CentraXxMapperException, MdrConnectionFactoryException, ChainFactoryException {

        IdPathManager_002 idPathManager = new IdPathManager_002();
        qualityFileManager = new CsvQualityReportFileManager(new QualityResultCsvLineManager_002(), idPathManager);

        MdrConnectionFactory mdrConnectionFactory = new MdrConnectionFactory();
        MDRValidator dthValidator = createDTHValidator(mdrConnectionFactory);
        QueryValidator queryValidator = new QueryValidator(ApplicationBean.getMdrClient());
        qualityResultsValidator = new QualityResultsValidator(dthValidator, queryValidator);

    }


    @GET
    public String myTest(@QueryParam("fileId") String fileId) throws QualityReportFileManagerException, ExcelWorkbookFactoryException, QualityResultsValidatorException {


        QualityResults qualityResults = qualityFileManager.readFile(fileId);
        qualityResults = qualityResultsValidator.validate(qualityResults);


        return fileId;

    }

    private MDRValidator createDTHValidator(MdrConnectionFactory mdrConnectionFactory) throws ChainFactoryException {

        try {

            MdrConnection mdrConnection = mdrConnectionFactory.getMdrConnection();
            return new MDRValidator(mdrConnection, ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_GRP_MDSB), ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_GRP_MDSK));

        } catch (MdrConnectionException | MdrConnectionFactoryException | MdrInvalidResponseException | MdrException | ExecutionException e) {
            throw new ChainFactoryException(e);
        }

    }


}
