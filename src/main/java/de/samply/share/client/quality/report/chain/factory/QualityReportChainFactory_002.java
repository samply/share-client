package de.samply.share.client.quality.report.chain.factory;/*
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
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.centraxx.CentraxxMapperException;
import de.samply.share.client.quality.report.centraxx.CentraxxMapperImpl_v2;
import de.samply.share.client.quality.report.chain.Chain;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactory;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactoryImpl;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManager;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory_002;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_Converter;
import de.samply.share.client.quality.report.dktk.DktkId_MdrId_ConverterImpl;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager_002;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern_002;
import de.samply.share.client.quality.report.file.id.path.IdPathManager_002;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManagerImpl;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager_002;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterImpl;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.mdr.MdrConnectionFactory;
import de.samply.share.client.quality.report.model.reader.ModelReader;
import de.samply.share.client.quality.report.model.reader.QualityReportModelReaderImpl;
import de.samply.share.client.quality.report.properties.PropertyUtils;
import de.samply.share.client.quality.report.results.operations.QualityResultsAnalyzer;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.views.ViewsCreator;
import de.samply.share.client.quality.report.views.fromto.FromToViewsCreator;
import de.samply.share.client.quality.report.views.fromto.scheduler.ViewFromToScheduler;
import de.samply.share.client.quality.report.views.fromto.scheduler.ViewFromToSchedulerFactory;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.QueryValidator;
import de.samply.web.mdrFaces.MdrContext;

import java.util.concurrent.ExecutionException;

public class QualityReportChainFactory_002 extends QualityReportChainFactory {

    private ViewFromToSchedulerFactory viewFromToSchedulerFactory = new ViewFromToSchedulerFactory();
    private CentraxxMapper centraXxMapper;
    private IdPathManager_002 idPathManager;

    public QualityReportChainFactory_002(IdPathManager_002 idPathManager, ChainFinalizer chainFinalizer) throws ChainFactoryException {
        this (idPathManager, chainFinalizer, null);
    }

    public QualityReportChainFactory_002(IdPathManager_002 idPathManager, ChainFinalizer chainFinalizer, LocalDataManagementRequester localDataManagementRequester) throws ChainFactoryException {

        this.idPathManager = idPathManager;


        MdrIgnoredElements ignoredElements = getIgnoredDataelements();
        MdrMappedElements mdrMappedElements = createMdrMappedElements();
        ModelReader modelReader = new QualityReportModelReaderImpl();
        MdrConnectionFactory mdrConnectionFactory = createMdrConnectionFactory();
        MDRValidator dthValidator = createMDRValidator(mdrConnectionFactory);
        QueryValidator queryValidator = new QueryValidator(ApplicationBean.getMdrClient());
        QualityResultsValidator qualityResultsValidator = new QualityResultsValidator(dthValidator, queryValidator);
        QualityResultsAnalyzer qualityResultsAnalyzer = new QualityResultsAnalyzer();
        long maxTimeToWaitInMillis = getMaxTimeToWaitInMillis();
        ChainLinkTimerFactory chainLinkTimerFactory = new ChainLinkTimerFactory_002(maxTimeToWaitInMillis);
        ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager = createChainLinkStaticStatisticsFileManager();
        ChainLinkStatisticsFactory chainLinkStatisticsFactory = new ChainLinkStatisticsFactoryImpl(chainLinkStaticStatisticsFileManager);


        localDataManagementRequester = (localDataManagementRequester == null) ? createLocalDataManagementRequester() : localDataManagementRequester;
        QualityResultCsvLineManager_002 qualityResultCsvLineManager = new QualityResultCsvLineManager_002();
        CsvQualityReportFileManager csvQualityReportFileManager = new CsvQualityReportFileManager<>(qualityResultCsvLineManager, idPathManager);

        MetadataTxtColumnManager_002 metadataTxtColumnManager = new MetadataTxtColumnManager_002();
        QualityReportMetadataFileManager qualityReportMetadataFileManager = new QualityReportMetadataFileManagerImpl<>(metadataTxtColumnManager, idPathManager);

        ViewFromToScheduler viewFromToScheduler = viewFromToSchedulerFactory.createViewFromToScheduler();
        ViewsCreator viewsCreator = new FromToViewsCreator(viewFromToScheduler);
        ((FromToViewsCreator)viewsCreator).setIgnoredElements(ignoredElements);
        ((FromToViewsCreator)viewsCreator).setMdrMappedElements(mdrMappedElements);

        this.centraXxMapper = createCentraXxMapper(mdrMappedElements);


        setIgnoredElements(ignoredElements);
        setMdrMappedElements(mdrMappedElements);
        setMaxAttempts();
        setModelReader(modelReader);
        setQualityResultsValidator(qualityResultsValidator);
        setQualityResultsAnalyzer(qualityResultsAnalyzer);
        setChainLinkTimerFactory(chainLinkTimerFactory);

        setLocalDataManagementRequester(localDataManagementRequester);
        setCsvQualityReportFileManager(csvQualityReportFileManager);
        setViewsCreator(viewsCreator);
        setQualityReportMetadataFileManager(qualityReportMetadataFileManager);
        setChainLinkStatisticsFactory(chainLinkStatisticsFactory);
        setChainFinalizer(chainFinalizer);


    }

    private LocalDataManagementRequester createLocalDataManagementRequester(){
        return new LocalDataManagementRequesterImpl();
    }

    private MdrMappedElements createMdrMappedElements (){

        LdmConnector ldmConnector = ApplicationBean.getLdmConnector();
        return new MdrMappedElements(ldmConnector);

    }

    private void updateIgnoredElements(){
        MdrIgnoredElements ignoredElements = getIgnoredDataelements();
        setIgnoredElements(ignoredElements);
    }

    private ChainLinkStaticStatisticsFileManager createChainLinkStaticStatisticsFileManager() {

        return new ChainLinkStaticStatisticsFileManager();

    }

    @Override
    protected Chain create(String fileId, Model model) throws ChainFactoryException {

        updateIgnoredElements();

        MdrClient mdrClient = getMdrClient();
        DktkId_MdrId_Converter dktkIdManager = new DktkId_MdrId_ConverterImpl(mdrClient);
        setExcelQualityReportFileManager(model, mdrClient, centraXxMapper, dktkIdManager);

        return super.create(fileId, model);
    }

    private void setExcelQualityReportFileManager(Model model, MdrClient mdrClient, CentraxxMapper centraXxMapper, DktkId_MdrId_Converter dktkIdManager){

        ExcelPattern excelPattern = new ExcelPattern_002(model, mdrClient, centraXxMapper, dktkIdManager, mdrMappedElements);
        ExcelQualityReportFileManager excelQualityReportFileManager = new ExcelQualityReportFileManager(excelPattern, idPathManager);
        setExcelQualityReportFileManager(excelQualityReportFileManager);

    }

    private CentraxxMapper createCentraXxMapper (MdrMappedElements mdrMappedElements) throws ChainFactoryException {
        try {
            return new CentraxxMapperImpl_v2(mdrMappedElements);
        } catch (CentraxxMapperException e) {
            throw new ChainFactoryException(e);
        }
    }

    private long getMaxTimeToWaitInMillis() throws ChainFactoryException {

        String sMaxTimeToWaitInMillis = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MAX_TIME_TO_WAIT_IN_MILLIS);
        return convertToLong(sMaxTimeToWaitInMillis);

    }

    private long convertToLong (String number) throws ChainFactoryException {
        try {
            return Long.parseLong(number);
        }catch (Exception e){
            throw new ChainFactoryException(e);
        }
    }

    private void setMaxAttempts() throws ChainFactoryException {

        String sMaxAttempts = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MAX_ATTEMPTS);
        int maxAttempts = convertToInteger(sMaxAttempts);
        setMaxAttempts(maxAttempts);

    }

    private Integer convertToInteger(String number) throws ChainFactoryException {

        try {
            return Integer.valueOf(number);
        } catch (Exception e){
            throw new ChainFactoryException(e);
        }

    }

    private MdrIgnoredElements getIgnoredDataelements(){

        String[] properties = PropertyUtils.getListOfProperties(EnumConfiguration.QUALITY_REPORT_IGNORED_DATAELEMENTS);

        MdrIgnoredElements ignoredElements = new MdrIgnoredElements();


        for (String dataElementId : properties){

            if (dataElementId.length() > 0) {

                MdrIdDatatype mdrId = new MdrIdDatatype(dataElementId);
                ignoredElements.add(mdrId);

            }
        }


        return ignoredElements;

    }


    private MdrClient getMdrClient(){
        return MdrContext.getMdrContext().getMdrClient();
    }



    private MdrConnectionFactory createMdrConnectionFactory() {

        return new MdrConnectionFactory();

    }

    private MDRValidator createMDRValidator(MdrConnectionFactory mdrConnectionFactory) throws ChainFactoryException {

        try {

            MdrConnection mdrConnection = mdrConnectionFactory.getMdrConnection();
            return new MDRValidator(mdrConnection, getMdrSourceGroups());

        } catch (MdrConnectionException | MdrInvalidResponseException | MdrException | ExecutionException e) {
            throw new ChainFactoryException(e);
        }

    }

    private String [] getMdrSourceGroups(){
        return PropertyUtils.getListOfProperties(EnumConfiguration.MDR_SOURCE_GROUPS);
    }

}
