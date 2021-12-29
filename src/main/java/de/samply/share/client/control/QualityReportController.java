package de.samply.share.client.control;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.chain.Chain;
import de.samply.share.client.quality.report.chain.factory.ChainFactory;
import de.samply.share.client.quality.report.chain.factory.ChainFactoryException;
import de.samply.share.client.quality.report.chain.factory.QualityReportChainFactory002;
import de.samply.share.client.quality.report.chain.finalizer.ChainFinalizer;
import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.manager.ChainStatisticsManager;
import de.samply.share.client.quality.report.faces.QualityReportFileInfo;
import de.samply.share.client.quality.report.faces.QualityReportFileInfoManager;
import de.samply.share.client.quality.report.faces.QualityReportFileInfoManagerException;
import de.samply.share.client.quality.report.faces.QualityReportFileInfoManagerImpl;
import de.samply.share.client.quality.report.file.id.generator.QualityFileIdGenerator;
import de.samply.share.client.quality.report.file.id.generator.QualityFileIdGeneratorImpl;
import de.samply.share.client.quality.report.file.id.path.IdPathManager002;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManagerImpl;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager002;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The type Quality report controller.
 */
@ManagedBean(name = "qualityReportController")
@SessionScoped
public class QualityReportController implements Serializable {


  private static final Logger logger = LoggerFactory.getLogger(QualityReportController.class);
  private final IdPathManager002 idPathManager = new IdPathManager002();
  private final QualityFileIdGenerator qualityFileIdGenerator = new QualityFileIdGeneratorImpl();
  private ChainFactory qualityReportChainFactory;
  private final QualityReportFileInfoManager qualityReportFileInfoManager;
  private final ChainStatisticsManager chainStatisticsManager = ApplicationBean
      .getChainStatisticsManager();
  private final ChainFinalizer chainFinalizer = ApplicationBean.getChainFinalizer();
  private boolean isLoading = true;
  
  
  /**
   * Controller used in frontend in order to generate quality reports.
   */
  public QualityReportController() {

    MetadataTxtColumnManager002 metadataTxtColumnManager = new MetadataTxtColumnManager002();
    QualityReportMetadataFileManager qualityReportMetadataFileManager =
        new QualityReportMetadataFileManagerImpl<>(metadataTxtColumnManager, idPathManager);
    qualityReportFileInfoManager = new QualityReportFileInfoManagerImpl(
        qualityReportMetadataFileManager, idPathManager);
    isLoading = false;

  }


  private ChainFactory createQualityReportChainFactory(IdPathManager002 idPathManager,
      ChainFinalizer chainFinalizer) throws QualityReportControllerException {

    try {
      return new QualityReportChainFactory002(idPathManager, chainFinalizer);
    } catch (ChainFactoryException e) {
      throw new QualityReportControllerException(e);
    }

  }
  
  /**
   * Triggers the generation of a quality report.
   *
   * @throws QualityReportControllerException the quality report controller exception
   */
  public void generate() throws QualityReportControllerException {

    isLoading = true;

    String fileId = qualityFileIdGenerator.generateFileId();
    Chain chain = createQualityReportChain(fileId);
    chainStatisticsManager.setChainStatistics(chain.getChainStatistics());

    chain.run();
    addTimeout();

    isLoading = false;

  }

  private void addTimeout() {

    Long timeout = getTimoutInMilliseconds();
    if (timeout != null) {
      chainFinalizer.addTimeout(timeout);
    }

  }

  private Long getTimoutInMilliseconds() {

    String timeoutInMinutesS = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_TIMEOUT_IN_MINUTES);
    Long timeoutInMinutes = Utils.getAsLong(timeoutInMinutesS);
    return (timeoutInMinutes != null) ? timeoutInMinutes * 60L * 1000L : null;

  }
  
  /**
   * Is task running boolean.
   *
   * @return the boolean
   */
  public boolean isTaskRunning() {
    return !isLoading && chainStatisticsManager.getChainStatistics() != null;
  }
  
  /**
   * Is loading boolean.
   *
   * @return the boolean
   */
  public boolean isLoading() {
    return isLoading;
  }
  
  /**
   * Is status changed boolean.
   *
   * @return the boolean
   */
  public boolean isStatusChanged() {
    return chainStatisticsManager.isStatusChanged();
  }

  private Chain createQualityReportChain(String fileId) throws QualityReportControllerException {

    try {

      return getQualityReportChainFactory().create(fileId);

    } catch (ChainFactoryException e) {
      throw new QualityReportControllerException(e);
    }

  }

  private ChainFactory getQualityReportChainFactory() throws QualityReportControllerException {

    if (qualityReportChainFactory == null) {
      qualityReportChainFactory = createQualityReportChainFactory(idPathManager, chainFinalizer);
    }

    return qualityReportChainFactory;
  }
  
  /**
   * Gets file info related to a quality report (filename, timestamp, version,...).
   * This information will be shown in frontend.
   *
   * @return the quality report file infos
   */
  public List<QualityReportFileInfo> getQualityReportFileInfos() {

    try {

      return qualityReportFileInfoManager.getQualityReportFiles();

    } catch (QualityReportFileInfoManagerException e) {
      logger.error(e.getMessage(),e);
      return null;
    }

  }
  
  /**
   * Downloads a quality report via browser.
   *
   * @param filePath the file path
   * @param filename the filename
   */
  public void download(String filePath, String filename) {

    try {

      Faces.sendFile(new FileInputStream(filePath), filename, true);

    } catch (IOException e) {
      logger.error(e.getMessage(),e);
    }
  }
  
  /**
   * Gets chain statistics.
   *
   * @return the chain statistics
   */
  public ChainStatistics getChainStatistics() {
    return chainStatisticsManager.getChainStatistics();
  }
  
  /**
   * Finalize chain.
   */
  public void finalizeChain() {
    chainFinalizer.finalizeChain();
  }
  
  /**
   * Gets language.
   *
   * @return the language
   */
  public String getLanguage() {
    return ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LANGUAGE_CODE);
  }
  
  /**
   * Is timeout reached boolean.
   *
   * @return the boolean
   */
  public boolean isTimeoutReached() {
    return chainFinalizer.isTimeoutReached();
  }


}
