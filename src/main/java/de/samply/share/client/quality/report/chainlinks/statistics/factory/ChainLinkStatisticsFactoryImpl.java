package de.samply.share.client.quality.report.chainlinks.statistics.factory;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManager;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManagerException;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChainLinkStatisticsFactoryImpl implements ChainLinkStatisticsFactory {

  protected static final Logger logger = LogManager.getLogger(ChainLinkStatisticsFactoryImpl.class);
  private final ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager;


  public ChainLinkStatisticsFactoryImpl(
      ChainLinkStaticStatisticsFileManager statisticsFileManager) {
    this.chainLinkStaticStatisticsFileManager = statisticsFileManager;
  }


  @Override
  public ChainLinkStatistics createChainLinkStatistics(ChainLinkStatisticKey chainLinkStatisticKey)
      throws ChainLinkStatisticsFactoryException {

    ChainLinkStatistics chainLinkStatistics = new ChainLinkStatistics(chainLinkStatisticKey,
        chainLinkStaticStatisticsFileManager);

    ChainLinkStaticStatistics chainLinkStaticStatistics = readChainLinkStaticStatistics();
    ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters =
        chainLinkStaticStatistics.get(chainLinkStatisticKey);

    if (chainLinkStatistics != null && chainLinkStaticStatisticsParameters != null) {

      Long averageNanoTime = chainLinkStaticStatisticsParameters.getAverageNanoTime();
      Integer averageNumberOfItems = chainLinkStaticStatisticsParameters.getAverageNumberOfItems();

      chainLinkStatistics.setNumberOfElementsToBeProcessed(averageNumberOfItems);
      chainLinkStatistics.setAverageNanoTimeOfProcess(averageNanoTime);

    }

    return chainLinkStatistics;

  }

  private ChainLinkStaticStatistics readChainLinkStaticStatistics()
      throws ChainLinkStatisticsFactoryException {

    try {
      return chainLinkStaticStatisticsFileManager.read();
    } catch (ChainLinkStaticStatisticsFileManagerException e) {

      logger.info("Static statistics file could not be read");
      logger.info("Create new static statistics file");

      return createNewChainLinkStaticStatisticsFile();

      //throw new ChainLinkStatisticsFactoryException(e);

    }

  }

  private ChainLinkStaticStatistics createNewChainLinkStaticStatisticsFile()
      throws ChainLinkStatisticsFactoryException {

    try {
      return createNewChainLinkStaticStatisticsFile_withoutExceptionManagement();
    } catch (ChainLinkStaticStatisticsFileManagerException e) {

      logger.error("Error creating static statistics file");
      throw new ChainLinkStatisticsFactoryException(e);
    }

  }

  private ChainLinkStaticStatistics
          createNewChainLinkStaticStatisticsFile_withoutExceptionManagement() throws
      ChainLinkStaticStatisticsFileManagerException {
    ChainLinkStaticStatistics chainLinkStaticStatistics = new ChainLinkStaticStatistics();
    chainLinkStaticStatisticsFileManager.write(chainLinkStaticStatistics);

    return chainLinkStaticStatistics;

  }

}
