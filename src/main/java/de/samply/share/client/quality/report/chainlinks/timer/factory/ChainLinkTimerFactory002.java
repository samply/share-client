package de.samply.share.client.quality.report.chainlinks.timer.factory;

import de.samply.share.client.quality.report.chainlinks.timer.ChainLinkTimer;
import de.samply.share.client.quality.report.chainlinks.timer.ChainLinkTimer002;

public class ChainLinkTimerFactory002 implements ChainLinkTimerFactory {

  private final long maxTimeToWaitInMillis;

  public ChainLinkTimerFactory002(long maxTimeToWaitInMillis) {
    this.maxTimeToWaitInMillis = maxTimeToWaitInMillis;
  }


  @Override
  public ChainLinkTimer createChainLinkTimer() {

    ChainLinkTimer002 chainLinkTimer = new ChainLinkTimer002();
    chainLinkTimer.setMaxTimeToWaitInMillis(maxTimeToWaitInMillis);

    return chainLinkTimer;

  }

}
