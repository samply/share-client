package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;

public interface ChainLinkStatisticsProducer {

  void addTimeProProcess(Long timeProProcess, Boolean isToBeRepeated);

  void setNumberOfElementsToBeProcessed(Integer numberOfElementsToBeProcessed);

  void finalizeProducer() throws ChainLinkStatisticsException;

  void setFirstElementBeingProcessed();

}
