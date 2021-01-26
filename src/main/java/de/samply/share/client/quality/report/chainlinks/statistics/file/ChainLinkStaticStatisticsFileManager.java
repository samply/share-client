package de.samply.share.client.quality.report.chainlinks.statistics.file;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import de.samply.share.client.quality.report.file.manager.anonym.AnonymTxtColumnFileManager;
import de.samply.share.client.quality.report.file.manager.anonym.AnonymTxtColumnFileManagerException;
import de.samply.share.client.quality.report.file.manager.anonym.AnonymTxtColumnFileManagerImpl;
import de.samply.share.client.quality.report.file.txtcolumn.AnonymTxtColumn;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.io.File;


public class ChainLinkStaticStatisticsFileManager {


  private final AnonymTxtColumnFileManager anonymTxtColumnFileManager;

  /**
   * Todo.
   */
  public ChainLinkStaticStatisticsFileManager() {

    String filePath = getFilePath();
    anonymTxtColumnFileManager = new AnonymTxtColumnFileManagerImpl(filePath);

  }


  /**
   * Todo.
   *
   * @param chainLinkStatisticTxtColumn Todo.
   * @throws ChainLinkStaticStatisticsFileManagerException Todo.
   */
  public synchronized void write(ChainLinkStaticStatistics chainLinkStatisticTxtColumn)
      throws ChainLinkStaticStatisticsFileManagerException {

    try {

      AnonymTxtColumn anonymTxtColumn = convert(chainLinkStatisticTxtColumn);
      anonymTxtColumnFileManager.write(anonymTxtColumn);

    } catch (AnonymTxtColumnFileManagerException e) {
      throw new ChainLinkStaticStatisticsFileManagerException(e);
    }

  }

  private AnonymTxtColumn convert(ChainLinkStaticStatistics chainLinkStaticStatistics) {

    AnonymTxtColumn anonymTxtColumn = new AnonymTxtColumn();

    for (ChainLinkStatisticKey chainLinkStatisticKey : ChainLinkStatisticKey.values()) {

      ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters =
          chainLinkStaticStatistics.get(chainLinkStatisticKey);
      if (chainLinkStaticStatisticsParameters != null) {
        anonymTxtColumn.addElement(chainLinkStatisticKey.getFileKey(),
            chainLinkStaticStatisticsParameters.toString());
      }

    }

    return anonymTxtColumn;

  }

  private ChainLinkStaticStatistics convert(AnonymTxtColumn anonymTxtColumn) {

    ChainLinkStaticStatistics chainLinkStaticStatistics = new ChainLinkStaticStatistics();

    if (anonymTxtColumn != null) {

      for (ChainLinkStatisticKey chainLinkStatisticKey : ChainLinkStatisticKey.values()) {
        String element = anonymTxtColumn.getElement(chainLinkStatisticKey.getFileKey());
        if (element != null) {

          ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters =
              new ChainLinkStaticStatisticsParameters(element);
          chainLinkStaticStatistics.put(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);

        }
      }

    }

    return chainLinkStaticStatistics;
  }


  /**
   * Todo.
   *
   * @param chainLinkStatisticKey               Todo.
   * @param chainLinkStaticStatisticsParameters Todo.
   * @throws ChainLinkStaticStatisticsFileManagerException Todo.
   */
  public synchronized void update(ChainLinkStatisticKey chainLinkStatisticKey,
      ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters)
      throws ChainLinkStaticStatisticsFileManagerException {

    if (chainLinkStaticStatisticsParameters != null) {

      ChainLinkStaticStatistics chainLinkStaticStatistics = read();
      chainLinkStaticStatistics.put(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);
      write(chainLinkStaticStatistics);

    }

  }

  /**
   * Todo.
   *
   * @return Todo.
   * @throws ChainLinkStaticStatisticsFileManagerException Todo.
   */
  public synchronized ChainLinkStaticStatistics read()
      throws ChainLinkStaticStatisticsFileManagerException {

    try {

      AnonymTxtColumn anonymTxtColumn = anonymTxtColumnFileManager.read();
      return convert(anonymTxtColumn);


    } catch (AnonymTxtColumnFileManagerException e) {
      throw new ChainLinkStaticStatisticsFileManagerException(e);
    }

  }


  private String getFilePath() {

    String filePath = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
    String filename = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_STATISTICS_FILENAME);

    return (filePath != null && filename != null) ? filePath + File.separator + filename : null;

  }


}
