package de.samply.share.client.quality.report.chainlinks.timer;

public interface ChainLinkTimer {

  public void mySleep(int attempt);

  public void myWait();

  public void myNotify();

}
