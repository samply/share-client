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


import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.client.quality.report.chain.Chain;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chain.QualityReportChain;
import de.samply.share.client.quality.report.chain.ChainParameters;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactory;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.reader.ModelReader;
import de.samply.share.client.quality.report.model.reader.ModelReaderException;
import de.samply.share.client.quality.report.results.operations.QualityResultsAnalyzer;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.views.ViewsCreator;

import java.util.List;


public class QualityReportChainFactory implements ChainFactory {

    private ChainLinkTimerFactory chainLinkTimerFactory;
    private LocalDataManagementRequester localDataManagementRequester;
    private CsvQualityReportFileManager csvQualityReportFileManager;
    private ExcelQualityReportFileManager excelQualityReportFileManager;
    private QualityReportMetadataFileManager qualityReportMetadataFileManager;
    private QualityResultsAnalyzer qualityResultsAnalyzer;
    private QualityResultsValidator qualityResultsValidator;
    private ViewsCreator viewsCreator;
    protected MdrIgnoredElements ignoredElements;
    private ChainLinkStatisticsFactory chainLinkStatisticsFactory;
    private ChainFinalizer chainFinalizer;


    private ModelReader modelReader;
    private int maxAttempts;



    @Override
    public Chain create (String fileId) throws ChainFactoryException {

        Model model = getModel();
        return create(fileId, model);

    }

    protected Chain create (String fileId, Model model) throws ChainFactoryException {

        ChainParameters chainParameters = new ChainParameters();

        chainParameters.setModel(model);
        chainParameters.setCsvQualityReportFileManager(csvQualityReportFileManager);
        chainParameters.setExcelQualityReportFileManager(excelQualityReportFileManager);
        chainParameters.setChainLinkTimerFactory(chainLinkTimerFactory);
        chainParameters.setFileId(fileId);
        chainParameters.setLocalDataManagementRequester(localDataManagementRequester);
        chainParameters.setMaxAttempts(maxAttempts);
        chainParameters.setQualityResultsAnalyzer(qualityResultsAnalyzer);
        chainParameters.setQualityResultsValidator(qualityResultsValidator);
        chainParameters.setViewsCreator(viewsCreator);
        chainParameters.setIgnoredElements(ignoredElements);
        chainParameters.setQualityReportMetadataFileManager(qualityReportMetadataFileManager);
        chainParameters.setChainLinkStatisticsFactory(chainLinkStatisticsFactory);
        chainParameters.setChainFinalizer(chainFinalizer);


        return create(chainParameters);

    }

    private Chain create (ChainParameters chainParameters) throws ChainFactoryException {


        try {
            return new QualityReportChain(chainParameters);
        } catch (QualityReportChainException e) {
            throw new ChainFactoryException(e);
        }

    }

    public Model getModel () throws ChainFactoryException {

        try {

            return modelReader.getModel();

        } catch (ModelReaderException e) {
            throw new ChainFactoryException(e);
        }

    }


    public void setChainLinkTimerFactory(ChainLinkTimerFactory chainLinkTimerFactory) {
        this.chainLinkTimerFactory = chainLinkTimerFactory;
    }

    public void setLocalDataManagementRequester(LocalDataManagementRequester localDataManagementRequester) {
        this.localDataManagementRequester = localDataManagementRequester;
    }

    public void setCsvQualityReportFileManager(CsvQualityReportFileManager csvQualityReportFileManager) {
        this.csvQualityReportFileManager = csvQualityReportFileManager;
    }

    public void setExcelQualityReportFileManager(ExcelQualityReportFileManager excelQualityReportFileManager) {
        this.excelQualityReportFileManager = excelQualityReportFileManager;
    }

    public void setQualityResultsAnalyzer(QualityResultsAnalyzer qualityResultsAnalyzer) {
        this.qualityResultsAnalyzer = qualityResultsAnalyzer;
    }

    public void setQualityResultsValidator(QualityResultsValidator qualityResultsValidator) {
        this.qualityResultsValidator = qualityResultsValidator;
    }

    public void setViewsCreator(ViewsCreator viewsCreator) {
        this.viewsCreator = viewsCreator;
    }

    public void setModelReader(ModelReader modelReader) {
        this.modelReader = modelReader;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setIgnoredElements(MdrIgnoredElements ignoredElements) {
        this.ignoredElements = ignoredElements;
    }

    public void setQualityReportMetadataFileManager(QualityReportMetadataFileManager qualityReportMetadataFileManager) {
        this.qualityReportMetadataFileManager = qualityReportMetadataFileManager;
    }

    public void setChainLinkStatisticsFactory(ChainLinkStatisticsFactory chainLinkStatisticsFactory) {
        this.chainLinkStatisticsFactory = chainLinkStatisticsFactory;
    }

    public void setChainFinalizer(ChainFinalizer chainFinalizer) {
        this.chainFinalizer = chainFinalizer;
    }

}
