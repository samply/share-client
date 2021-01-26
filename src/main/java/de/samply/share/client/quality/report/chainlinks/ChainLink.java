package de.samply.share.client.quality.report.chainlinks;

import de.samply.share.client.quality.report.chainlinks.connector.ChainLinkConnector;
import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticsException;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticsProducer;
import de.samply.share.client.quality.report.chainlinks.timer.ChainLinkTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class ChainLink<I extends ChainLinkItem> extends Thread {

  protected static final Logger logger = LogManager.getLogger(ChainLink.class);
  protected ConcurrentLinkedDeque<ChainLinkItem> deque = new ConcurrentLinkedDeque<>();
  private ChainLinkFinalizer chainLinkFinalizer;
  private ChainLinkStatisticsProducer chainLinkStatisticsProducer;
  private ChainLinkConnector chainLinkConnector;
  private int maxAttempts;
  private ChainLinkTimer chainLinkTimer;
  private boolean isFinalized = false;
  private boolean isPreviousChainLinkFinalized = false;
  private final List<ChainLinkError> errors = new ArrayList<>();

  protected abstract String getChainLinkId();

  protected abstract I process(I item) throws ChainLinkException;


  @Override
  public void run() {

    logStartChainLink();

    while (!isFinalized && !isPreviousChainLinkFinalizedAndIsInputProcessed()) {

      processItem();

    }

    finalizeChainLink();

  }

  private void logStartChainLink() {
    logger.info(getChainLinkId() + " started");
  }

  private void logEndChainLink() {
    logger.info(getChainLinkId() + " ended");
  }

  private void processItem() {

    ChainLinkItem item = getItem();

    if (item != null) {

      item = processItem(item);
      ChainLinkError error = item.getChainLinkError();

      if (error != null) {
        addError(error);
      }

    } else {
      chainLinkTimer.myWait();
    }
  }

  private ChainLinkItem processItem(ChainLinkItem item) {

    startStatistics();

    item = processItemAndAddElapsedTime(item);

    item.incrementAttempt();

    if (item.isToBeRepeated() && !isMaxAttempt(item)) {

      item.resetConfigurationValues();

      if (item.isAlreadyUsed()) {
        addItemFirst(item);
      } else {
        addItem(item);
      }

      item = sleepAndAddElapsedTime(item);

    } else if (!item.isToBeRepeated()) {

      item.setAlreadyUsed();

      if (item.isToBeForwarded()) {

        addItemToNextChainLink(item);

        if (item.isToBeReused()) {

          item.resetConfigurationValues();
          item.resetAttempt();
          addItemFirst(item);

        }

      }

    }

    addStatistics(item);

    return item;

  }

  private void logErrors() {

    if (errors.size() > 0) {

      logger.error("Errors in current ChainLink: \n");

      for (ChainLinkError error : errors) {

        logger.error("Class: " + error.getChainLinkItem().getClass());
        logger.error("MdrKey: " + error.getError().getMdrKey().toString());
        logger.error("Error Code: " + error.getError().getErrorCode());
        logger.error("Error Description: " + error.getError().getDescription());
        logger.error("Error Extension:" + error.getError().getExtension());
        logger.error("Attempt:" + error.getChainLinkItem().getAttempt());
      }

    }
  }

  private boolean isPreviousChainLinkFinalizedAndIsInputProcessed() {
    return isPreviousChainLinkFinalized && deque.size() == 0;
  }

  protected void logStartProcess(ChainLinkItem item) {
    logger
        .info(getChainLinkId() + ": Start chain link process (attempt:" + item.getAttempt() + ")");
  }

  protected void logEndProcess(ChainLinkItem item) {
    logger.info(getChainLinkId() + ": End chain link process");
  }


  private void startStatistics() {

    if (chainLinkStatisticsProducer != null) {
      chainLinkStatisticsProducer.setFirstElementBeingProcessed();
    }

  }

  private void addStatistics(ChainLinkItem chainLinkItem) {

    if (chainLinkStatisticsProducer != null) {

      chainLinkStatisticsProducer
          .addTimeProProcess(chainLinkItem.getElapsedNanoTime(), chainLinkItem.isToBeRepeated);

      int numberOfItemsToBeProcessed = getNumberOfItemsToBeProcessed();
      chainLinkStatisticsProducer.setNumberOfElementsToBeProcessed(numberOfItemsToBeProcessed);

    }

  }

  protected int getNumberOfItemsToBeProcessed() {
    return deque.size();
  }


  private void finalizeStatistics() {

    try {

      finalizeStatisticsWithoutExceptionManagement();

    } catch (ChainLinkStatisticsException chainLinkStatisticsExeption) {
      logger.error(chainLinkStatisticsExeption);
    }

  }

  private void finalizeStatisticsWithoutExceptionManagement() throws ChainLinkStatisticsException {

    if (chainLinkStatisticsProducer != null) {
      chainLinkStatisticsProducer.finalizeProducer();
    }

  }


  private ChainLinkItem processItemAndAddElapsedTime(ChainLinkItem chainLinkItem) {
    logStartProcess(chainLinkItem);

    chainLinkItem = processAndManageExceptions(chainLinkItem);

    logEndProcess(chainLinkItem);
    long endTime = System.nanoTime();

    long startTime = System.nanoTime();
    chainLinkItem.addElapsedNanoTime(endTime - startTime);

    return chainLinkItem;

  }

  private ChainLinkItem processAndManageExceptions(ChainLinkItem chainLinkItem) {

    try {

      return process((I) chainLinkItem);

    } catch (ChainLinkException e) {

      logger.error(e);
      chainLinkItem.setToBeRepeated();

      return chainLinkItem;
    }

  }

  private ChainLinkItem sleepAndAddElapsedTime(ChainLinkItem chainLinkItem) {

    long startTime = System.nanoTime();
    chainLinkTimer.mySleep(chainLinkItem.getAttempt());
    long endTime = System.nanoTime();

    chainLinkItem.addElapsedNanoTime(endTime - startTime);

    return chainLinkItem;

  }

  private void addItemToNextChainLink(ChainLinkItem chainLinkItem) {

    if (chainLinkConnector != null) {
      chainLinkConnector.addItemToNextChainLink(chainLinkItem);
    }

  }

  private boolean isMaxAttempt(ChainLinkItem chainLinkItem) {
    return (chainLinkItem.getAttempt() > maxAttempts);
  }

  /**
   * Todo.
   *
   * @param chainLinkItem Todo.
   */
  public void addItem(ChainLinkItem chainLinkItem) {

    deque.addLast(chainLinkItem);
    chainLinkTimer.myNotify();

    if (!this.isAlive()) {
      this.start();
    }

  }

  public void addItemFirst(ChainLinkItem chainLinkItem) {
    deque.addFirst(chainLinkItem);
  }

  private ChainLinkItem getItem() {
    return deque.pollFirst();
  }

  /**
   * Todo.
   */
  public synchronized void finalizeChainLink() {

    if (!isFinalized) {

      isFinalized = true;

      if (chainLinkConnector != null) {
        chainLinkConnector.setPreviousChainLinkFinalized();
      }

      if (chainLinkFinalizer != null) {
        chainLinkFinalizer.setChainLinkAsFinalized(this);
      }

      finalizeStatistics();
      chainLinkTimer.myNotify();

      logErrors();
      logEndChainLink();


    }

  }

  public synchronized List<ChainLinkError> getErrors() {
    return errors;
  }

  private synchronized void addError(ChainLinkError error) {
    errors.add(error);
  }

  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public void setChainLinkTimer(ChainLinkTimer chainLinkTimer) {
    this.chainLinkTimer = chainLinkTimer;
  }

  public void setChainLinkConnector(ChainLinkConnector chainLinkConnector) {
    this.chainLinkConnector = chainLinkConnector;
  }

  public void setPreviousChainLinkFinalized() {
    isPreviousChainLinkFinalized = true;
    chainLinkTimer.myNotify();
  }

  public void setChainLinkStatisticsProducer(
      ChainLinkStatisticsProducer chainLinkStatisticsProducer) {
    this.chainLinkStatisticsProducer = chainLinkStatisticsProducer;
  }

  /**
   * Todo.
   *
   * @param chainLinkFinalizer Todo.
   */
  public void setChainLinkFinalizer(ChainLinkFinalizer chainLinkFinalizer) {

    this.chainLinkFinalizer = chainLinkFinalizer;
    if (chainLinkFinalizer != null) {
      chainLinkFinalizer.addChainLink(this);
    }

  }

}
