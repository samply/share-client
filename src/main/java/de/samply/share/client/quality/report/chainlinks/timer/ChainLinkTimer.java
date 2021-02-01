package de.samply.share.client.quality.report.chainlinks.timer;

public interface ChainLinkTimer {

  void mySleep(int attempt);

  void myWait();

  void myNotify();

}
