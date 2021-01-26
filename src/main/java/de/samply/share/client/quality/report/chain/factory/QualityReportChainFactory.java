package de.samply.share.client.quality.report.chain.factory;

import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.chain.Chain;
import de.samply.share.client.quality.report.chain.ChainParameters;
import de.samply.share.client.quality.report.chain.QualityReportChain;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
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

public class QualityReportChainFactory implements ChainFactory {

  protected MdrIgnoredElements ignoredElements;
  protected MdrMappedElements mdrMappedElements;
  private ChainLinkTimerFactory chainLinkTimerFactory;
  private LocalDataManagementRequester localDataManagementRequester;
  private CsvQualityReportFileManager csvQualityReportFileManager;
  private ExcelQualityReportFileManager excelQualityReportFileManager;
  private QualityReportMetadataFileManager qualityReportMetadataFileManager;
  private QualityResultsAnalyzer qualityResultsAnalyzer;
  private QualityResultsValidator qualityResultsValidator;
  private ViewsCreator viewsCreator;
  private ChainLinkStatisticsFactory chainLinkStatisticsFactory;
  private ChainFinalizer chainFinalizer;


  private ModelReader modelReader;
  private int maxAttempts;


  @Override
  public Chain create(String fileId) throws ChainFactoryException {

    Model model = getModel();
    return create(fileId, model);

  }

  protected Chain create(String fileId, Model model) throws ChainFactoryException {

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
    chainParameters.setMdrMappedElements(mdrMappedElements);

    return create(chainParameters);

  }

  private Chain create(ChainParameters chainParameters) throws ChainFactoryException {

    try {
      return new QualityReportChain(chainParameters);
    } catch (QualityReportChainException e) {
      throw new ChainFactoryException(e);
    }

  }

  /**
   * Todo.
   *
   * @return Todo.
   * @throws ChainFactoryException Todo.
   */
  public Model getModel() throws ChainFactoryException {
    try {
      return modelReader.getModel();
    } catch (ModelReaderException e) {
      throw new ChainFactoryException(e);
    }
  }


  public void setChainLinkTimerFactory(ChainLinkTimerFactory chainLinkTimerFactory) {
    this.chainLinkTimerFactory = chainLinkTimerFactory;
  }

  public void setLocalDataManagementRequester(
      LocalDataManagementRequester localDataManagementRequester) {
    this.localDataManagementRequester = localDataManagementRequester;
  }

  public void setCsvQualityReportFileManager(
      CsvQualityReportFileManager csvQualityReportFileManager) {
    this.csvQualityReportFileManager = csvQualityReportFileManager;
  }

  public void setExcelQualityReportFileManager(
      ExcelQualityReportFileManager excelQualityReportFileManager) {
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

  public void setQualityReportMetadataFileManager(
      QualityReportMetadataFileManager qualityReportMetadataFileManager) {
    this.qualityReportMetadataFileManager = qualityReportMetadataFileManager;
  }

  public void setChainLinkStatisticsFactory(ChainLinkStatisticsFactory chainLinkStatisticsFactory) {
    this.chainLinkStatisticsFactory = chainLinkStatisticsFactory;
  }

  public void setChainFinalizer(ChainFinalizer chainFinalizer) {
    this.chainFinalizer = chainFinalizer;
  }

  public void setMdrMappedElements(MdrMappedElements mdrMappedElements) {
    this.mdrMappedElements = mdrMappedElements;
  }

}
