package de.samply.share.client.quality.report.chainlinks.statistics.file;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import java.util.HashMap;
import java.util.Map;

public class ChainLinkStaticStatistics {


  private final Map<ChainLinkStatisticKey, ChainLinkStaticStatisticsParameters>
      chainLinkStaticStatisticsParametersMap = new HashMap<>();


  /**
   * Todo.
   *
   * @param chainLinkStatisticKey               Todo.
   * @param chainLinkStaticStatisticsParameters Todo.
   */
  public void put(ChainLinkStatisticKey chainLinkStatisticKey,
      ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters) {

    chainLinkStaticStatisticsParametersMap
        .put(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);

  }

  /**
   * Todo.
   *
   * @param chainLinkStatisticKey Todo.
   * @return Todo.
   */
  public ChainLinkStaticStatisticsParameters get(ChainLinkStatisticKey chainLinkStatisticKey) {

    return chainLinkStaticStatisticsParametersMap.get(chainLinkStatisticKey);

  }

}
