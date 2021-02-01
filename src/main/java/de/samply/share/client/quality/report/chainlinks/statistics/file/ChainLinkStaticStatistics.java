package de.samply.share.client.quality.report.chainlinks.statistics.file;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import java.util.HashMap;
import java.util.Map;

public class ChainLinkStaticStatistics {


  private final Map<ChainLinkStatisticKey, ChainLinkStaticStatisticsParameters>
      chainLinkStaticStatisticsParametersMap = new HashMap<>();


  /**
   * Put chain link statistic identifier.
   *
   * @param chainLinkStatisticKey               Chain Link identifier.
   * @param chainLinkStaticStatisticsParameters Encapsulates paramters oc chain link statistic.
   */
  public void put(ChainLinkStatisticKey chainLinkStatisticKey,
      ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters) {

    chainLinkStaticStatisticsParametersMap
        .put(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);

  }

  /**
   * get chain link statistics parameters of the statistics of a  chain link.
   *
   * @param chainLinkStatisticKey Identifier of the chain link statistics.
   * @return Paramters of chain link statistics.
   */
  public ChainLinkStaticStatisticsParameters get(ChainLinkStatisticKey chainLinkStatisticKey) {

    return chainLinkStaticStatisticsParametersMap.get(chainLinkStatisticKey);

  }

}
