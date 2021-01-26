package de.samply.share.client.quality.report.chain.finalizer;

import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;
import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizerImpl;
import de.samply.share.client.quality.report.timeout.TimeoutJob;

public class ChainFinalizerImpl implements ChainFinalizer {

  private final ChainLinkFinalizer chainLinkFinalizer = new ChainLinkFinalizerImpl();

  @Override
  public void finalizeChain() {
    chainLinkFinalizer.finalizeAll();
  }

  @Override
  public ChainLinkFinalizer getChainLinkFinalizer() {
    return chainLinkFinalizer;
  }

  @Override
  public void addTimeout(long timeout) {
    TimeoutJob timeoutJob = new TimeoutJob(getChainLinkFinalizer(), timeout);
  }

  @Override
  public boolean isTimeoutReached() {
    return chainLinkFinalizer.isTimeoutReachedInAnyChainLinkFinalizerListener();
  }

}
