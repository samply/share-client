package de.samply.share.client.quality.report.chain.finalizer;

import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;

public interface ChainFinalizer {

  void finalizeChain();

  ChainLinkFinalizer getChainLinkFinalizer();

  void addTimeout(long timeout);

  boolean isTimeoutReached();

}
