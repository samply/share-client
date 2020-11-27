package de.samply.share.client.quality.report.chainlinks.timer;

public class ChainLinkTimer002 extends ChainLinkTimerImpl {

  @Override
  protected long getTimeToSleepInMillis(int attempt) {

    int exponent = attempt / 4;

    Double pow = Math.pow(2, exponent);

    return pow.longValue() * 60 * 1000;

  }

}
