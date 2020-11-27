package de.samply.share.client.quality.report.chain;/*
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
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.chain.factory.QualityReportChainException;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.connector.ChainLinkConnector;
import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;
import de.samply.share.client.quality.report.chainlinks.instances.allelements.NotFoundDataElements_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.configuration.ConfigurationStarter_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.configuration.ConfigurationTerminator_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.file.MetadataQualityReportFileWriter_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.file.QualityReportFileWriter_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.result.GetResults_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.statistic.GetStats_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.validator.GetResults_To_Validator_Connector;
import de.samply.share.client.quality.report.chainlinks.instances.validator.Validator_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.view.CreateViews_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.view.CreateViews_To_PostView_Connector;
import de.samply.share.client.quality.report.chainlinks.instances.view.PostViewOnlyStatistics_ChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.view.PostView_ChainLink;
import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatisticsImpl;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactory;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactoryException;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.operations.QualityResultsAnalyzer;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.views.ViewsCreator;
import de.samply.share.client.util.db.ConfigurationUtil;


public class QualityReportChain implements Chain {


    private int maxAttempts;
    private ChainLinkTimerFactory chainLinkTimerFactory;
    private ChainLinkStatisticsFactory chainLinkStatisticsFactory;

    private ChainLink firstChainLink;
    private String fileId;
    private ChainStatistics chainStatistics = new ChainStatisticsImpl();
    private ChainLinkFinalizer chainLinkFinalizer;



    public QualityReportChain(ChainParameters chainParameters) throws QualityReportChainException {

        setParametersToInitializeChainlink(chainParameters);

        // get resources
        Model model = chainParameters.getModel();
        ModelSearcher modelSearcher = new ModelSearcher(model);
        LocalDataManagementRequester localDataManagementRequester = chainParameters.getLocalDataManagementRequester();
        CsvQualityReportFileManager csvQualityReportFileManager = chainParameters.getCsvQualityReportFileManager();
        ExcelQualityReportFileManager excelQualityReportFileManager = chainParameters.getExcelQualityReportFileManager();
        ViewsCreator viewsCreator = chainParameters.getViewsCreator();
        QualityResultsValidator qualityResultsValidator = chainParameters.getQualityResultsValidator();
        QualityResultsAnalyzer qualityResultsAnalyzer = chainParameters.getQualityResultsAnalyzer();
        MdrIgnoredElements ignoredElements = chainParameters.getIgnoredElements();
        QualityReportMetadataFileManager qualityReportMetadataFileManager = chainParameters.getQualityReportMetadataFileManager();
        chainLinkStatisticsFactory = chainParameters.getChainLinkStatisticsFactory();
        ChainFinalizer chainFinalizer = chainParameters.getChainFinalizer();
        if (chainFinalizer != null){
            chainLinkFinalizer = chainFinalizer.getChainLinkFinalizer();
        }


        // Create chain links
        ConfigurationStarter_ChainLink configurationStarter_chainLink = new ConfigurationStarter_ChainLink();
        //IgnoredElements_ChainLink ignoredElements_chainLink = new IgnoredElements_ChainLink(ignoredElements, modelSearcher);
        NotFoundDataElements_ChainLink notFoundDataElements_ChainLink = new NotFoundDataElements_ChainLink(model);
        CreateViews_ChainLink createViews_chainLink = new CreateViews_ChainLink(model, viewsCreator);
        PostViewOnlyStatistics_ChainLink postViewOnlyStatistics_chainLink = new PostViewOnlyStatistics_ChainLink(localDataManagementRequester);
        GetStats_ChainLink getStats_chainLink = new GetStats_ChainLink(localDataManagementRequester);
        PostView_ChainLink postView_chainLink = new PostView_ChainLink(localDataManagementRequester);
        GetResults_ChainLink getResults_chainLink = new GetResults_ChainLink(localDataManagementRequester);
        Validator_ChainLink validator_chainLink = new Validator_ChainLink(qualityResultsValidator);
        MetadataQualityReportFileWriter_ChainLink metadataQualityReportFileWriter_chainLink = new MetadataQualityReportFileWriter_ChainLink<>(qualityReportMetadataFileManager, localDataManagementRequester);
        QualityReportFileWriter_ChainLink csvWriter_chainLink = new QualityReportFileWriter_ChainLink(csvQualityReportFileManager);
        QualityReportFileWriter_ChainLink excelWriter_chainLink = new QualityReportFileWriter_ChainLink(excelQualityReportFileManager);
        ConfigurationTerminator_ChainLink configurationTerminator_chainLink = new ConfigurationTerminator_ChainLink();

        // set first chain link
        firstChainLink = configurationStarter_chainLink;

        // connect chain links
        //configurationStarter_chainLink.setChainLinkConnector(new ChainLinkConnector(ignoredElements_chainLink));
        //ignoredElements_chainLink.setChainLinkConnector(new ChainLinkConnector(createViews_chainLink));
        configurationStarter_chainLink.setChainLinkConnector(new ChainLinkConnector(createViews_chainLink));

        boolean isOnlyStatisticsAvailable = isOnlyStatisticsAvailable();
        if (isOnlyStatisticsAvailable){

            createViews_chainLink.setChainLinkConnector(new CreateViews_To_PostView_Connector<ChainLinkContext>(postViewOnlyStatistics_chainLink));
            postViewOnlyStatistics_chainLink.setChainLinkConnector(new ChainLinkConnector(getStats_chainLink));
            getStats_chainLink.setChainLinkConnector(new ChainLinkConnector(postView_chainLink));
            postView_chainLink.setChainLinkConnector(new ChainLinkConnector(getResults_chainLink));

        } else {

            createViews_chainLink.setChainLinkConnector(new CreateViews_To_PostView_Connector<ChainLinkContext>(postView_chainLink));
            postView_chainLink.setChainLinkConnector(new ChainLinkConnector(getStats_chainLink));
            getStats_chainLink.setChainLinkConnector(new ChainLinkConnector(getResults_chainLink));

        }


        getResults_chainLink.setChainLinkConnector(new GetResults_To_Validator_Connector<ChainLinkContext>(validator_chainLink, qualityResultsAnalyzer));
        validator_chainLink.setChainLinkConnector(new ChainLinkConnector(notFoundDataElements_ChainLink));
        notFoundDataElements_ChainLink.setChainLinkConnector(new ChainLinkConnector(csvWriter_chainLink));

        csvWriter_chainLink.setChainLinkConnector(new ChainLinkConnector(excelWriter_chainLink));
        excelWriter_chainLink.setChainLinkConnector(new ChainLinkConnector(metadataQualityReportFileWriter_chainLink));
        metadataQualityReportFileWriter_chainLink.setChainLinkConnector(new ChainLinkConnector(configurationTerminator_chainLink));

        // initialize chain links
        initialize(configurationStarter_chainLink, ChainLinkStatisticKey.CONFIGURATION_STARTER);
        //initialize(ignoredElements_chainLink, ChainLinkStatisticKey.IGNORED_ELEMENTS_SETTER);
        initialize(notFoundDataElements_ChainLink, ChainLinkStatisticKey.NOT_FOUND_DATA_ELEMENTS_SETTER);
        initialize(createViews_chainLink, ChainLinkStatisticKey.VIEWS_CREATOR);
        if (isOnlyStatisticsAvailable) {
            initialize(postViewOnlyStatistics_chainLink, ChainLinkStatisticKey.VIEWS_ONLY_STATISTICS_SENDER);
        }
        initialize(getStats_chainLink, ChainLinkStatisticKey.LOCAL_DATA_MANAGEMENT_STATISTICS_REQUESTER);
        initialize(postView_chainLink, ChainLinkStatisticKey.VIEWS_SENDER);
        initialize(getResults_chainLink, ChainLinkStatisticKey.LOCAL_DATA_MANAGEMENT_RESULTS_REQUESTER);
        initialize(validator_chainLink, ChainLinkStatisticKey.VALIDATOR);
        initialize(csvWriter_chainLink, ChainLinkStatisticKey.QUALITY_REPORT_CSV_WRITER);
        initialize(excelWriter_chainLink, ChainLinkStatisticKey.QUALITY_REPORT_EXCEL_WRITER);
        initialize(metadataQualityReportFileWriter_chainLink, ChainLinkStatisticKey.QUALITY_REPORT_METADATA_WRITER);
        initialize(configurationTerminator_chainLink, ChainLinkStatisticKey.CONFIGURATION_TERMINATOR);


    }

    private void setParametersToInitializeChainlink (ChainParameters chainParameters){

        maxAttempts = chainParameters.getMaxAttempts();
        chainLinkTimerFactory = chainParameters.getChainLinkTimerFactory();
        fileId = chainParameters.getFileId();

    }

    private void initialize (ChainLink chainLink, ChainLinkStatisticKey chainLinkStatisticKey) throws QualityReportChainException {

        chainLink.setMaxAttempts(maxAttempts);
        chainLink.setChainLinkTimer(chainLinkTimerFactory.createChainLinkTimer());
        chainLink.setChainLinkFinalizer(chainLinkFinalizer);
        initializeStatistics(chainLink, chainLinkStatisticKey);

    }

    private void initializeStatistics (ChainLink chainLink, ChainLinkStatisticKey chainLinkStatisticKey) throws QualityReportChainException {

        ChainLinkStatistics chainLinkStatistics = createChainLinkStatistics(chainLinkStatisticKey);

        chainLink.setChainLinkStatisticsProducer(chainLinkStatistics);
        chainStatistics.addChainLinkStatisticsConsumer(chainLinkStatistics);

    }

    private ChainLinkStatistics createChainLinkStatistics (ChainLinkStatisticKey chainLinkStatisticKey) throws QualityReportChainException {

        try {
            return chainLinkStatisticsFactory.createChainLinkStatistics(chainLinkStatisticKey);
        } catch (ChainLinkStatisticsFactoryException e) {
            throw new QualityReportChainException(e);
        }

    }


    @Override
    public void run() {

        ChainLinkContext chainLinkContext = createChainLinkContext();
        firstChainLink.addItem(chainLinkContext);
        firstChainLink.setPreviousChainLinkFinalized();

    }

    private ChainLinkContext createChainLinkContext (){

        ChainLinkContext chainLinkContext = new ChainLinkContext();
        chainLinkContext.setFileId(fileId);

        return chainLinkContext;

    }

    private boolean isOnlyStatisticsAvailable(){

        String availability = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_ONLY_STATISTICS_AVAILABLE);
        Boolean isAvailable = (availability == null) ? true : convertToBoolean(availability);

        return (isAvailable == null) ? true : isAvailable;

    }

    private Boolean convertToBoolean (String booleanValue){

        try{
            return Boolean.valueOf(booleanValue);
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public void finalizeChain(){
        firstChainLink.finalizeChainLink();
    }

    @Override
    public ChainStatistics getChainStatistics() {
        return chainStatistics;
    }

}
