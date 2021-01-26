package de.samply.share.client.quality.report.chainlinks.statistics.manager;

import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;

public class ChainStatisticsManager {

  private static final int MAX_NUMBER_OF_CALLS_TO_SIGNALIZE_CHANGED_STATUS = 10;

  private ChainStatistics chainStatistics;
  private boolean isStatusChanged = false;
  private int numberOfCallsToSignalizeChangedStatus = 0;

  /**
   * Todo.
   *
   * @return Todo.
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
   * Todo.
   *
   * @param chainStatistics Todo.
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
   * Todo.
   *
   * @return Todo.
   */
  public boolean isStatusChanged() {

    getChainStatistics(); // update status of statistics

    return this.isStatusChanged;

  }

}
