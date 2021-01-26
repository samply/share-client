package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;

import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManager;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManagerException;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsParameters;

public class ChainLinkStatistics implements ChainLinkStatisticsConsumer,
    ChainLinkStatisticsProducer {

  private final ChainLinkStatisticKey chainLinkStatisticKey;
  private final ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager;

  private long averageNanoTimeOfProcess = 18L * 1000000000L;
  private long totalProcessedNanoTime;
  private int numberOfProcessedItems;
  private int numberOfItemsToBeProcessed = 1;
  private boolean isFinalized = false;
  private boolean isFirstElementBeingProcessed = false;


  /**
   * Todo.
   *
   * @param chainLinkStatisticKey                Todo.
   * @param chainLinkStaticStatisticsFileManager Todo.
   */
  public ChainLinkStatistics(ChainLinkStatisticKey chainLinkStatisticKey,
      ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager) {

    this.chainLinkStatisticKey = chainLinkStatisticKey;
    this.chainLinkStaticStatisticsFileManager = chainLinkStaticStatisticsFileManager;

  }


  @Override
  public void addTimeProProcess(Long timeProProcess, Boolean isToBeRepeated) {

    totalProcessedNanoTime += timeProProcess;
    if (!isToBeRepeated) {
      numberOfProcessedItems++;
    }

  }

  @Override
  public void setNumberOfElementsToBeProcessed(Integer numberOfElementsToBeProcessed) {

    if (numberOfElementsToBeProcessed != null) {
      this.numberOfItemsToBeProcessed = numberOfElementsToBeProcessed;
    }

  }

  @Override
  public void finalizeProducer() throws ChainLinkStatisticsException {

    isFinalized = true;
    updateChainLinkStaticStatisticsFileManager();

  }

  public boolean isProcessingElements() {
    return isFirstElementBeingProcessed && numberOfItemsToBeProcessed > 0;
  }

  @Override
  public int getNumberOfItems() {
    return numberOfItemsToBeProcessed + numberOfProcessedItems;
  }

  private void updateChainLinkStaticStatisticsFileManager() throws ChainLinkStatisticsException {

    try {

      updateChainLinkStaticStatisticsFileManagerWithoutExceptionManagement();

    } catch (ChainLinkStaticStatisticsFileManagerException e) {
      throw new ChainLinkStatisticsException(e);
    }
  }

  private void updateChainLinkStaticStatisticsFileManagerWithoutExceptionManagement()
      throws ChainLinkStaticStatisticsFileManagerException {

    ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters =
        new ChainLinkStaticStatisticsParameters();

    chainLinkStaticStatisticsParameters.setAverageNanoTime(getAverageNanoTimeOfProcess());
    chainLinkStaticStatisticsParameters.setAverageNumberOfItems(numberOfProcessedItems);

    chainLinkStaticStatisticsFileManager
        .update(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);

  }


  @Override
  public long getRemainingNanoTime() {

    long averageNanoTimeOfProcess = getAverageNanoTimeOfProcess();

    return (!isFinalized && numberOfItemsToBeProcessed == 0) ? averageNanoTimeOfProcess
        : numberOfItemsToBeProcessed * averageNanoTimeOfProcess;

  }

  @Override
  public String getMessage() {
    return chainLinkStatisticKey.getMessage();
  }

  private long getAverageNanoTimeOfProcess() {

    return (numberOfProcessedItems == 0) ? averageNanoTimeOfProcess
        : totalProcessedNanoTime / numberOfProcessedItems;

  }

  /**
   * Todo.
   *
   * @param averageNanoTimeOfProcess Todo.
   */
  public void setAverageNanoTimeOfProcess(Long averageNanoTimeOfProcess) {

    if (averageNanoTimeOfProcess != null) {
      this.averageNanoTimeOfProcess = averageNanoTimeOfProcess;
    }

  }

  @Override
  public boolean isFinalized() {
    return isFinalized;
  }

  public void setFirstElementBeingProcessed() {
    isFirstElementBeingProcessed = true;
  }

}
