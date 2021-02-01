package de.samply.share.client.quality.report.chainlinks.statistics.manager;

import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;

public class ChainStatisticsManager {

  private static final int MAX_NUMBER_OF_CALLS_TO_SIGNALIZE_CHANGED_STATUS = 10;

  private ChainStatistics chainStatistics;
  private boolean isStatusChanged = false;
  private int numberOfCallsToSignalizeChangedStatus = 0;

  /**
   * Get chain statistics. This is a summary of the chain link statistics.
   *
   * @return Chain statistics.
   */
  public ChainStatistics getChainStatistics() {

    if (chainStatistics != null && chainStatistics.isFinalized()) {

      chainStatistics = null;
      setStatusChanged();

    } else {

      if (numberOfCallsToSignalizeChangedStatus > 0) {
        numberOfCallsToSignalizeChangedStatus--;
      } else {
        isStatusChanged = false;
      }

    }

    return chainStatistics;
  }

  /**
   * Set Chain statistics.
   *
   * @param chainStatistics Chain statistics.
   */
  public void setChainStatistics(ChainStatistics chainStatistics) {

    this.chainStatistics = chainStatistics;
    setStatusChanged();

  }

  private void setStatusChanged() {
    isStatusChanged = true;
    numberOfCallsToSignalizeChangedStatus = MAX_NUMBER_OF_CALLS_TO_SIGNALIZE_CHANGED_STATUS;
  }

  /**
   * Checks if the chain statistics habe changed.
   *
   * @return Check.
   */
  public boolean isStatusChanged() {

    getChainStatistics(); // update status of statistics

    return this.isStatusChanged;

  }

}
