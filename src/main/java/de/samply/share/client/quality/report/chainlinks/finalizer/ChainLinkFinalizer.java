package de.samply.share.client.quality.report.chainlinks.finalizer;

import de.samply.share.client.quality.report.chainlinks.ChainLink;

public interface ChainLinkFinalizer {

  public void addChainLink(ChainLink chainLink);

  public void setChainLinkAsFinalized(ChainLink chainLink);

  public void finalizeAll();

  public void addChainLinkFinalizerListener(ChainLinkFinalizerListener chainLinkFinalizerListener);

  public boolean isTimeoutReachedInAnyChainLinkFinalizerListener();

  public void setAtLeastOneTimeoutReached();

}
