package de.samply.share.client.quality.report.chainlinks.finalizer;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import java.util.ArrayList;
import java.util.List;

public class ChainLinkFinalizerImpl implements ChainLinkFinalizer {

  private final List<ChainLink> chainLinks = new ArrayList<>();
  private final List<ChainLinkFinalizerListener> chainLinkFinalizerListeners = new ArrayList<>();
  private boolean isTimeoutReachedInAnyChainLinkFinalizerListener = false;

  @Override
  public synchronized void addChainLink(ChainLink chainLink) {
    chainLinks.add(chainLink);
  }

  @Override
  public synchronized void finalizeAll() {

    List<ChainLink> chainLinks = new ArrayList<>();
    chainLinks.addAll(this.chainLinks);

    for (ChainLink chainLink : chainLinks) {
      chainLink.finalizeChainLink();
    }

    notifyIsFinalizedToAllListeners();

  }

  @Override
  public synchronized void setChainLinkAsFinalized(ChainLink chainLink) {

    chainLinks.remove(chainLink);
    notifyIsFinalizedToAllListenersIfNoMoreChainLinks();

  }

  @Override
  public void addChainLinkFinalizerListener(ChainLinkFinalizerListener chainLinkFinalizerListener) {
    chainLinkFinalizerListeners.add(chainLinkFinalizerListener);
  }

  @Override
  public boolean isTimeoutReachedInAnyChainLinkFinalizerListener() {

    boolean isTimeOutReached = false;
    for (ChainLinkFinalizerListener chainLinkFinalizerListener : chainLinkFinalizerListeners) {
      if (chainLinkFinalizerListener.isTimeoutReached()) {
        isTimeOutReached = true;
      }
    }

    if (chainLinkFinalizerListeners.size() > 0) {
      isTimeoutReachedInAnyChainLinkFinalizerListener = isTimeOutReached;
    }

    return isTimeoutReachedInAnyChainLinkFinalizerListener;

  }

  @Override
  public void setAtLeastOneTimeoutReached() {
    isTimeoutReachedInAnyChainLinkFinalizerListener = true;
  }

  private void notifyIsFinalizedToAllListenersIfNoMoreChainLinks() {

    if (chainLinks.size() == 0) {
      notifyIsFinalizedToAllListeners();
    }

  }

  private void notifyIsFinalizedToAllListeners() {

    List<ChainLinkFinalizerListener> chainLinkFinalizerListenerList = new ArrayList<>();
    chainLinkFinalizerListenerList.addAll(chainLinkFinalizerListeners);

    for (ChainLinkFinalizerListener chainLinkFinalizerListener : chainLinkFinalizerListenerList) {

      chainLinkFinalizerListener.notifyIsFinished();
      chainLinkFinalizerListeners.remove(chainLinkFinalizerListener);

    }

  }

}
