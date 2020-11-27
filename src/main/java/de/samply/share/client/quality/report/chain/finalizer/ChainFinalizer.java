package de.samply.share.client.quality.report.chain.finalizer;

import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;

public interface ChainFinalizer {

  public void finalizeChain();

  public ChainLinkFinalizer getChainLinkFinalizer();

  public void addTimeout(long timeout);

  public boolean isTimeoutReached();

}
