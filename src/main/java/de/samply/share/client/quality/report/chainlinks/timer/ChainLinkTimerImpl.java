package de.samply.share.client.quality.report.chainlinks.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChainLinkTimerImpl implements ChainLinkTimer {

  private static final Logger logger = LoggerFactory.getLogger(ChainLinkTimerImpl.class);

  private long maxTimeToWaitInMillis;

  protected abstract long getTimeToSleepInMillis(int attempt);

  @Override
  public void mySleep(int attempt) {

    try {
      Thread.sleep(getTimeToSleepInMillis(attempt));
    } catch (InterruptedException e) {
      logger.error(e.getMessage(),e);
    }

  }

  @Override
  public synchronized void myWait() {

    try {
      wait(maxTimeToWaitInMillis);
    } catch (InterruptedException e) {
      logger.error(e.getMessage(),e);
    }

  }

  @Override
  public synchronized void myNotify() {
    notifyAll();
  }

  public void setMaxTimeToWaitInMillis(long maxTimeToWaitInMillis) {
    this.maxTimeToWaitInMillis = maxTimeToWaitInMillis;
  }

}
