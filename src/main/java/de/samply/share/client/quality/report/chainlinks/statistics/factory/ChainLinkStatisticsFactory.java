package de.samply.share.client.quality.report.chainlinks.statistics.factory;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatistics;

public interface ChainLinkStatisticsFactory {

  ChainLinkStatistics createChainLinkStatistics(ChainLinkStatisticKey chainLinkStatisticKey)
      throws ChainLinkStatisticsFactoryException;

}
