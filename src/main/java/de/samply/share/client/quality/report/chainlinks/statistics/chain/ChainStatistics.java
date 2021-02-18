package de.samply.share.client.quality.report.chainlinks.statistics.chain;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticsConsumer;
import java.util.List;

public interface ChainStatistics {

  void addChainLinkStatisticsConsumer(
      ChainLinkStatisticsConsumer chainLinkStatisticsConsumer);

  int getPercentage();

  String getEstimatedTimeToBeCompleted();

  List<String> getMessages();

  boolean isAccurate();

  String getTimeConsumed();

  String getStartTime();

  boolean isFinalized();

}
