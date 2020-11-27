package de.samply.share.client.quality.report.chainlinks.timer;

public abstract class ChainLinkTimerImpl implements ChainLinkTimer {

  private long maxTimeToWaitInMillis;

  protected abstract long getTimeToSleepInMillis(int attempt);

  @Override
  public void mySleep(int attempt) {

    try {
      Thread.sleep(getTimeToSleepInMillis(attempt));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  @Override
  public synchronized void myWait() {

    try {
      wait(maxTimeToWaitInMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
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
