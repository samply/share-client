package de.samply.share.client.quality.report.chainlinks.statistics.chain;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticsConsumer;
import java.util.List;

public interface ChainStatistics {

  public void addChainLinkStatisticsConsumer(
      ChainLinkStatisticsConsumer chainLinkStatisticsConsumer);

  public int getPercentage();

  public String getEstimatedTimeToBeCompleted();

  public List<String> getMessages();

  public boolean isAccurate();

  public String getTimeConsumed();

  public String getStartTime();

  public boolean isFinalized();

}
