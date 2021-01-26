package de.samply.share.client.quality.report.chain.factory;

import de.dth.mdr.validator.MdrConnection;
import de.dth.mdr.validator.MdrValidator;
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
import de.samply.share.client.quality.report.centraxx.CentraxxMapperImplV2;
import de.samply.share.client.quality.report.chain.Chain;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactory;
import de.samply.share.client.quality.report.chainlinks.statistics.factory.ChainLinkStatisticsFactoryImpl;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManager;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory;
import de.samply.share.client.quality.report.chainlinks.timer.factory.ChainLinkTimerFactory002;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverterImpl;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager002;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern002;
import de.samply.share.client.quality.report.file.id.path.IdPathManager002;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManagerImpl;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager002;
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
import de.samply.web.mdrfaces.MdrContext;
import java.util.concurrent.ExecutionException;

public class QualityReportChainFactory002 extends QualityReportChainFactory {

  private final ViewFromToSchedulerFactory viewFromToSchedulerFactory =
      new ViewFromToSchedulerFactory();
  private final CentraxxMapper centraXxMapper;
  private final IdPathManager002 idPathManager;

  public QualityReportChainFactory002(IdPathManager002 idPathManager,
      ChainFinalizer chainFinalizer) throws ChainFactoryException {
    this(idPathManager, chainFinalizer, null);
  }

  /**
   * Todo.
   *
   * @param idPathManager                Todo.
   * @param chainFinalizer               Todo.
   * @param localDataManagementRequester Todo.
   * @throws ChainFactoryException Todo.
   */
  public QualityReportChainFactory002(IdPathManager002 idPathManager,
      ChainFinalizer chainFinalizer, LocalDataManagementRequester localDataManagementRequester)
      throws ChainFactoryException {

    this.idPathManager = idPathManager;

    MdrIgnoredElements ignoredElements = getIgnoredDataelements();
    MdrMappedElements mdrMappedElements = createMdrMappedElements();
    localDataManagementRequester =
        (localDataManagementRequester == null) ? createLocalDataManagementRequester()
            : localDataManagementRequester;

    ViewFromToScheduler viewFromToScheduler = viewFromToSchedulerFactory
        .createViewFromToScheduler();
    ViewsCreator viewsCreator = new FromToViewsCreator(viewFromToScheduler);
    ((FromToViewsCreator) viewsCreator).setIgnoredElements(ignoredElements);
    ((FromToViewsCreator) viewsCreator).setMdrMappedElements(mdrMappedElements);

    this.centraXxMapper = createCentraXxMapper(mdrMappedElements);

    setIgnoredElements(ignoredElements);
    setMdrMappedElements(mdrMappedElements);
    setMaxAttempts();
    ModelReader modelReader = new QualityReportModelReaderImpl();
    setModelReader(modelReader);
    MdrConnectionFactory mdrConnectionFactory = createMdrConnectionFactory();
    MdrValidator dthValidator = createMdrValidator(mdrConnectionFactory);
    QueryValidator queryValidator = new QueryValidator(ApplicationBean.getMdrClient());
    long maxTimeToWaitInMillis = getMaxTimeToWaitInMillis();
    QualityResultsValidator qualityResultsValidator = new QualityResultsValidator(dthValidator,
        queryValidator);
    setQualityResultsValidator(qualityResultsValidator);
    QualityResultsAnalyzer qualityResultsAnalyzer = new QualityResultsAnalyzer();
    setQualityResultsAnalyzer(qualityResultsAnalyzer);
    ChainLinkTimerFactory chainLinkTimerFactory = new ChainLinkTimerFactory002(
        maxTimeToWaitInMillis);
    setChainLinkTimerFactory(chainLinkTimerFactory);
    setLocalDataManagementRequester(localDataManagementRequester);
    QualityResultCsvLineManager002 qualityResultCsvLineManager =
        new QualityResultCsvLineManager002();
    CsvQualityReportFileManager csvQualityReportFileManager = new CsvQualityReportFileManager<>(
        qualityResultCsvLineManager, idPathManager);
    setCsvQualityReportFileManager(csvQualityReportFileManager);
    setViewsCreator(viewsCreator);
    MetadataTxtColumnManager002 metadataTxtColumnManager = new MetadataTxtColumnManager002();
    QualityReportMetadataFileManager qualityReportMetadataFileManager =
        new QualityReportMetadataFileManagerImpl<>(metadataTxtColumnManager, idPathManager);
    setQualityReportMetadataFileManager(qualityReportMetadataFileManager);
    ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager =
        createChainLinkStaticStatisticsFileManager();
    ChainLinkStatisticsFactory chainLinkStatisticsFactory = new ChainLinkStatisticsFactoryImpl(
        chainLinkStaticStatisticsFileManager);
    setChainLinkStatisticsFactory(chainLinkStatisticsFactory);
    setChainFinalizer(chainFinalizer);


  }

  private LocalDataManagementRequester createLocalDataManagementRequester() {
    return new LocalDataManagementRequesterImpl();
  }

  private MdrMappedElements createMdrMappedElements() {

    LdmConnector ldmConnector = ApplicationBean.getLdmConnector();
    return new MdrMappedElements(ldmConnector);

  }

  private void updateIgnoredElements() {
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
    DktkIdMdrIdConverter dktkIdManager = new DktkIdMdrIdConverterImpl(mdrClient);
    setExcelQualityReportFileManager(model, mdrClient, centraXxMapper, dktkIdManager);

    return super.create(fileId, model);
  }

  private void setExcelQualityReportFileManager(Model model, MdrClient mdrClient,
      CentraxxMapper centraXxMapper, DktkIdMdrIdConverter dktkIdManager) {

    ExcelPattern excelPattern = new ExcelPattern002(model, mdrClient, centraXxMapper,
        dktkIdManager, mdrMappedElements);
    ExcelQualityReportFileManager excelQualityReportFileManager = new ExcelQualityReportFileManager(
        excelPattern, idPathManager);
    setExcelQualityReportFileManager(excelQualityReportFileManager);

  }

  private CentraxxMapper createCentraXxMapper(MdrMappedElements mdrMappedElements)
      throws ChainFactoryException {
    try {
      return new CentraxxMapperImplV2(mdrMappedElements);
    } catch (CentraxxMapperException e) {
      throw new ChainFactoryException(e);
    }
  }

  private long getMaxTimeToWaitInMillis() throws ChainFactoryException {

    String maxTimeToWaitInMillisS = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MAX_TIME_TO_WAIT_IN_MILLIS);
    return convertToLong(maxTimeToWaitInMillisS);

  }

  private long convertToLong(String number) throws ChainFactoryException {
    try {
      return Long.parseLong(number);
    } catch (Exception e) {
      throw new ChainFactoryException(e);
    }
  }

  private void setMaxAttempts() throws ChainFactoryException {

    String maxAttemptsS = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MAX_ATTEMPTS);
    int maxAttempts = convertToInteger(maxAttemptsS);
    setMaxAttempts(maxAttempts);

  }

  private Integer convertToInteger(String number) throws ChainFactoryException {

    try {
      return Integer.valueOf(number);
    } catch (Exception e) {
      throw new ChainFactoryException(e);
    }

  }

  private MdrIgnoredElements getIgnoredDataelements() {

    String[] properties = PropertyUtils
        .getListOfProperties(EnumConfiguration.QUALITY_REPORT_IGNORED_DATAELEMENTS);

    MdrIgnoredElements ignoredElements = new MdrIgnoredElements();

    for (String dataElementId : properties) {

      if (dataElementId.length() > 0) {

        MdrIdDatatype mdrId = new MdrIdDatatype(dataElementId);
        ignoredElements.add(mdrId);

      }
    }

    return ignoredElements;

  }


  private MdrClient getMdrClient() {
    return MdrContext.getMdrContext().getMdrClient();
  }


  private MdrConnectionFactory createMdrConnectionFactory() {

    return new MdrConnectionFactory();

  }

  private MdrValidator createMdrValidator(MdrConnectionFactory mdrConnectionFactory)
      throws ChainFactoryException {

    try {

      MdrConnection mdrConnection = mdrConnectionFactory.getMdrConnection();
      return new MdrValidator(mdrConnection, getMdrSourceGroups());

    } catch (MdrConnectionException | MdrInvalidResponseException | MdrException
        | ExecutionException e) {
      throw new ChainFactoryException(e);
    }

  }

  private String[] getMdrSourceGroups() {
    return PropertyUtils.getListOfProperties(EnumConfiguration.MDR_SOURCE_GROUPS);
  }

}
