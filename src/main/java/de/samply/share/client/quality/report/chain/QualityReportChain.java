package de.samply.share.client.quality.report.chain;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.chain.factory.QualityReportChainException;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.connector.ChainLinkConnector;
import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;
import de.samply.share.client.quality.report.chainlinks.instances.allelements.NotFoundDataElementsChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.configuration.ConfigurationStarterChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.configuration.ConfigurationTerminatorChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.file.MetadataQualityReportFileWriterChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.file.QualityReportFileWriterChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.result.GetResultsChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.statistic.GetStatsChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.validator.GetResultsToValidatorConnector;
import de.samply.share.client.quality.report.chainlinks.instances.validator.ValidatorChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.view.CreateViewsChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.view.CreateViewsToPostViewConnector;
import de.samply.share.client.quality.report.chainlinks.instances.view.PostViewChainLink;
import de.samply.share.client.quality.report.chainlinks.instances.view.PostViewOnlyStatisticsChainLink;
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
  private final ChainLinkStatisticsFactory chainLinkStatisticsFactory;

  private final ChainLink firstChainLink;
  private String fileId;
  private final ChainStatistics chainStatistics = new ChainStatisticsImpl();
  private ChainLinkFinalizer chainLinkFinalizer;


  /**
   * Todo.
   *
   * @param chainParameters Todo.
   * @throws QualityReportChainException Todo.
   */
  public QualityReportChain(ChainParameters chainParameters) throws QualityReportChainException {

    setParametersToInitializeChainlink(chainParameters);

    // get resources
    Model model = chainParameters.getModel();
    ModelSearcher modelSearcher = new ModelSearcher(model);
    LocalDataManagementRequester localDataManagementRequester = chainParameters
        .getLocalDataManagementRequester();
    ViewsCreator viewsCreator = chainParameters.getViewsCreator();
    MdrIgnoredElements ignoredElements = chainParameters.getIgnoredElements();
    MdrMappedElements mdrMappedElements = chainParameters.getMdrMappedElements();
    chainLinkStatisticsFactory = chainParameters.getChainLinkStatisticsFactory();
    ChainFinalizer chainFinalizer = chainParameters.getChainFinalizer();
    if (chainFinalizer != null) {
      chainLinkFinalizer = chainFinalizer.getChainLinkFinalizer();
    }

    // Create chain links
    ConfigurationStarterChainLink configurationStarterChainLink =
        new ConfigurationStarterChainLink();
    //IgnoredElementsChainLink ignoredElementsChainLink =
    // new IgnoredElementsChainLink(ignoredElements, modelSearcher);
    CreateViewsChainLink createViewsChainLink = new CreateViewsChainLink(model, viewsCreator);
    PostViewOnlyStatisticsChainLink postViewOnlyStatisticsChainLink =
        new PostViewOnlyStatisticsChainLink(localDataManagementRequester);
    GetStatsChainLink getStatsChainLink = new GetStatsChainLink(localDataManagementRequester);
    PostViewChainLink postViewChainLink = new PostViewChainLink(localDataManagementRequester);
    GetResultsChainLink getResultsChainLink = new GetResultsChainLink(
        localDataManagementRequester);
    // set first chain link
    firstChainLink = configurationStarterChainLink;

    // connect chain links
    //configurationStarterChainLink
    // .setChainLinkConnector(new ChainLinkConnector(ignoredElementsChainLink));
    //ignoredElementsChainLink.setChainLinkConnector(new ChainLinkConnector(createViewsChainLink));
    configurationStarterChainLink
        .setChainLinkConnector(new ChainLinkConnector(createViewsChainLink));

    boolean isOnlyStatisticsAvailable = isOnlyStatisticsAvailable();
    if (isOnlyStatisticsAvailable) {

      createViewsChainLink.setChainLinkConnector(
          new CreateViewsToPostViewConnector<ChainLinkContext>(
              postViewOnlyStatisticsChainLink));
      postViewOnlyStatisticsChainLink
          .setChainLinkConnector(new ChainLinkConnector(getStatsChainLink));
      getStatsChainLink.setChainLinkConnector(new ChainLinkConnector(postViewChainLink));
      postViewChainLink.setChainLinkConnector(new ChainLinkConnector(getResultsChainLink));

    } else {

      createViewsChainLink.setChainLinkConnector(
          new CreateViewsToPostViewConnector<ChainLinkContext>(postViewChainLink));
      postViewChainLink.setChainLinkConnector(new ChainLinkConnector(getStatsChainLink));
      getStatsChainLink.setChainLinkConnector(new ChainLinkConnector(getResultsChainLink));

    }
    QualityResultsValidator qualityResultsValidator = chainParameters.getQualityResultsValidator();
    QualityResultsAnalyzer qualityResultsAnalyzer = chainParameters.getQualityResultsAnalyzer();
    ValidatorChainLink validatorChainLink = new ValidatorChainLink(qualityResultsValidator);
    getResultsChainLink.setChainLinkConnector(
        new GetResultsToValidatorConnector<ChainLinkContext>(validatorChainLink,
            qualityResultsAnalyzer));
    NotFoundDataElementsChainLink notFoundDataElementsChainLink =
        new NotFoundDataElementsChainLink(model);
    CsvQualityReportFileManager csvQualityReportFileManager = chainParameters
        .getCsvQualityReportFileManager();
    ExcelQualityReportFileManager excelQualityReportFileManager = chainParameters
        .getExcelQualityReportFileManager();
    QualityReportFileWriterChainLink csvWriterChainLink = new QualityReportFileWriterChainLink(
        csvQualityReportFileManager);
    QualityReportFileWriterChainLink excelWriterChainLink = new QualityReportFileWriterChainLink(
        excelQualityReportFileManager);
    validatorChainLink
        .setChainLinkConnector(new ChainLinkConnector(notFoundDataElementsChainLink));
    notFoundDataElementsChainLink
        .setChainLinkConnector(new ChainLinkConnector(csvWriterChainLink));

    csvWriterChainLink.setChainLinkConnector(new ChainLinkConnector(excelWriterChainLink));
    QualityReportMetadataFileManager qualityReportMetadataFileManager = chainParameters
        .getQualityReportMetadataFileManager();
    MetadataQualityReportFileWriterChainLink metadataQualityReportFileWriterChainLink =
        new MetadataQualityReportFileWriterChainLink<>(qualityReportMetadataFileManager,
            localDataManagementRequester);
    excelWriterChainLink
        .setChainLinkConnector(new ChainLinkConnector(metadataQualityReportFileWriterChainLink));
    ConfigurationTerminatorChainLink configurationTerminatorChainLink =
        new ConfigurationTerminatorChainLink();
    metadataQualityReportFileWriterChainLink
        .setChainLinkConnector(new ChainLinkConnector(configurationTerminatorChainLink));

    // initialize chain links
    initialize(configurationStarterChainLink, ChainLinkStatisticKey.CONFIGURATION_STARTER);
    //initialize(ignoredElementsChainLink, ChainLinkStatisticKey.IGNORED_ELEMENTS_SETTER);
    initialize(notFoundDataElementsChainLink,
        ChainLinkStatisticKey.NOT_FOUND_DATA_ELEMENTS_SETTER);
    initialize(createViewsChainLink, ChainLinkStatisticKey.VIEWS_CREATOR);
    if (isOnlyStatisticsAvailable) {
      initialize(postViewOnlyStatisticsChainLink,
          ChainLinkStatisticKey.VIEWS_ONLY_STATISTICS_SENDER);
    }
    initialize(getStatsChainLink,
        ChainLinkStatisticKey.LOCAL_DATA_MANAGEMENT_STATISTICS_REQUESTER);
    initialize(postViewChainLink, ChainLinkStatisticKey.VIEWS_SENDER);
    initialize(getResultsChainLink, ChainLinkStatisticKey.LOCAL_DATA_MANAGEMENT_RESULTS_REQUESTER);
    initialize(validatorChainLink, ChainLinkStatisticKey.VALIDATOR);
    initialize(csvWriterChainLink, ChainLinkStatisticKey.QUALITY_REPORT_CSV_WRITER);
    initialize(excelWriterChainLink, ChainLinkStatisticKey.QUALITY_REPORT_EXCEL_WRITER);
    initialize(metadataQualityReportFileWriterChainLink,
        ChainLinkStatisticKey.QUALITY_REPORT_METADATA_WRITER);
    initialize(configurationTerminatorChainLink, ChainLinkStatisticKey.CONFIGURATION_TERMINATOR);


  }

  private void setParametersToInitializeChainlink(ChainParameters chainParameters) {

    maxAttempts = chainParameters.getMaxAttempts();
    chainLinkTimerFactory = chainParameters.getChainLinkTimerFactory();
    fileId = chainParameters.getFileId();

  }

  private void initialize(ChainLink chainLink, ChainLinkStatisticKey chainLinkStatisticKey)
      throws QualityReportChainException {

    chainLink.setMaxAttempts(maxAttempts);
    chainLink.setChainLinkTimer(chainLinkTimerFactory.createChainLinkTimer());
    chainLink.setChainLinkFinalizer(chainLinkFinalizer);
    initializeStatistics(chainLink, chainLinkStatisticKey);

  }

  private void initializeStatistics(ChainLink chainLink,
      ChainLinkStatisticKey chainLinkStatisticKey) throws QualityReportChainException {

    ChainLinkStatistics chainLinkStatistics = createChainLinkStatistics(chainLinkStatisticKey);

    chainLink.setChainLinkStatisticsProducer(chainLinkStatistics);
    chainStatistics.addChainLinkStatisticsConsumer(chainLinkStatistics);

  }

  private ChainLinkStatistics createChainLinkStatistics(ChainLinkStatisticKey chainLinkStatisticKey)
      throws QualityReportChainException {

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

  private ChainLinkContext createChainLinkContext() {

    ChainLinkContext chainLinkContext = new ChainLinkContext();
    chainLinkContext.setFileId(fileId);

    return chainLinkContext;

  }

  private boolean isOnlyStatisticsAvailable() {

    String availability = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_ONLY_STATISTICS_AVAILABLE);
    Boolean isAvailable = availability == null || convertToBoolean(availability);

    return isAvailable == null || isAvailable;

  }

  private Boolean convertToBoolean(String booleanValue) {

    try {
      return Boolean.valueOf(booleanValue);
    } catch (Exception e) {
      return null;
    }

  }

  @Override
  public ChainStatistics getChainStatistics() {
    return chainStatistics;
  }

}
