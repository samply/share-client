package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;

public interface ChainLinkStatisticsConsumer {


  long getRemainingNanoTime();

  String getMessage();

  boolean isFinalized();

  boolean isProcessingElements();

  int getNumberOfItems();

}
