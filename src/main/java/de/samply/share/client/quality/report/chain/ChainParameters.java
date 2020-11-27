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

import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactory;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.results.operations.QualityResultsAnalyzer;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.views.ViewsCreator;


public class ChainParameters {

    private String fileId;
    private LocalDataManagementRequester localDataManagementRequester;
    private int maxAttempts;
    private ChainLinkTimerFactory chainLinkTimerFactory;
    private CsvQualityReportFileManager csvQualityReportFileManager;
    private ExcelQualityReportFileManager excelQualityReportFileManager;
    private Model model;
    private ViewsCreator viewsCreator;
    private QualityResultsValidator qualityResultsValidator;
    private QualityResultsAnalyzer qualityResultsAnalyzer;
    private MdrIgnoredElements ignoredElements;
    private QualityReportMetadataFileManager qualityReportMetadataFileManager;
    private ChainLinkStatisticsFactory chainLinkStatisticsFactory;
    private ChainFinalizer chainFinalizer;


    public LocalDataManagementRequester getLocalDataManagementRequester() {
        return localDataManagementRequester;
    }

    public void setLocalDataManagementRequester(LocalDataManagementRequester localDataManagementRequester) {
        this.localDataManagementRequester = localDataManagementRequester;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public ChainLinkTimerFactory getChainLinkTimerFactory() {
        return chainLinkTimerFactory;
    }

    public void setChainLinkTimerFactory(ChainLinkTimerFactory chainLinkTimerFactory) {
        this.chainLinkTimerFactory = chainLinkTimerFactory;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public ViewsCreator getViewsCreator() {
        return viewsCreator;
    }

    public void setViewsCreator(ViewsCreator viewsCreator) {
        this.viewsCreator = viewsCreator;
    }

    public QualityResultsValidator getQualityResultsValidator() {
        return qualityResultsValidator;
    }

    public void setQualityResultsValidator(QualityResultsValidator qualityResultsValidator) {
        this.qualityResultsValidator = qualityResultsValidator;
    }

    public QualityResultsAnalyzer getQualityResultsAnalyzer() {
        return qualityResultsAnalyzer;
    }

    public void setQualityResultsAnalyzer(QualityResultsAnalyzer qualityResultsAnalyzer) {
        this.qualityResultsAnalyzer = qualityResultsAnalyzer;
    }

    public CsvQualityReportFileManager getCsvQualityReportFileManager() {
        return csvQualityReportFileManager;
    }

    public void setCsvQualityReportFileManager(CsvQualityReportFileManager csvQualityReportFileManager) {
        this.csvQualityReportFileManager = csvQualityReportFileManager;
    }

    public ExcelQualityReportFileManager getExcelQualityReportFileManager() {
        return excelQualityReportFileManager;
    }

    public void setExcelQualityReportFileManager(ExcelQualityReportFileManager excelQualityReportFileManager) {
        this.excelQualityReportFileManager = excelQualityReportFileManager;
    }

    public MdrIgnoredElements getIgnoredElements() {
        return ignoredElements;
    }

    public void setIgnoredElements(MdrIgnoredElements ignoredElements) {
        this.ignoredElements = ignoredElements;
    }

    public QualityReportMetadataFileManager getQualityReportMetadataFileManager() {
        return qualityReportMetadataFileManager;
    }

    public void setQualityReportMetadataFileManager(QualityReportMetadataFileManager qualityReportMetadataFileManager) {
        this.qualityReportMetadataFileManager = qualityReportMetadataFileManager;
    }

    public ChainLinkStatisticsFactory getChainLinkStatisticsFactory() {
        return chainLinkStatisticsFactory;
    }

    public void setChainLinkStatisticsFactory(ChainLinkStatisticsFactory chainLinkStatisticsFactory) {
        this.chainLinkStatisticsFactory = chainLinkStatisticsFactory;
    }

    public ChainFinalizer getChainFinalizer() {
        return chainFinalizer;
    }

    public void setChainFinalizer(ChainFinalizer chainFinalizer) {
        this.chainFinalizer = chainFinalizer;
    }

}
