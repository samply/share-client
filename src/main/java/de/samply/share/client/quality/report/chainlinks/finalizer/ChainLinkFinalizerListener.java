package de.samply.share.client.quality.report.chainlinks.finalizer;

public interface ChainLinkFinalizerListener {

  public void notifyIsFinished();

  public boolean isTimeoutReached();

}
