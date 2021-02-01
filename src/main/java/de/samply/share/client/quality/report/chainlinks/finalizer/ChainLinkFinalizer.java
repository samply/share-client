package de.samply.share.client.quality.report.chainlinks.finalizer;

import de.samply.share.client.quality.report.chainlinks.ChainLink;

public interface ChainLinkFinalizer {

  void addChainLink(ChainLink chainLink);

  void setChainLinkAsFinalized(ChainLink chainLink);

  void finalizeAll();

  void addChainLinkFinalizerListener(ChainLinkFinalizerListener chainLinkFinalizerListener);

  boolean isTimeoutReachedInAnyChainLinkFinalizerListener();

  void setAtLeastOneTimeoutReached();

}
