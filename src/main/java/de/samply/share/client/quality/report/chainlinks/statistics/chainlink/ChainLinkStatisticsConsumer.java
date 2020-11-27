package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;

public interface ChainLinkStatisticsConsumer {


  public long getRemainingNanoTime();

  public String getMessage();

  public boolean isFinalized();

  public boolean isProcessingElements();

  public int getNumberOfItems();

}
