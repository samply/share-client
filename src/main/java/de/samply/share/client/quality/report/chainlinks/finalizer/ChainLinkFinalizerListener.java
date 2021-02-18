package de.samply.share.client.quality.report.chainlinks.finalizer;

public interface ChainLinkFinalizerListener {

  void notifyIsFinished();

  boolean isTimeoutReached();

}
