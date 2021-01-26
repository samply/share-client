package de.samply.share.client.quality.report.chainlinks.connector;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;

public class ChainLinkConnector {

  protected ChainLink nextChainLink;


  public ChainLinkConnector(ChainLink nextChainLink) {
    this.nextChainLink = nextChainLink;
  }


  /**
   * Todo.
   *
   * @param chainLinkItem Todo.
   */
  public void addItemToNextChainLink(ChainLinkItem chainLinkItem) {
    if (chainLinkItem != null) {
      ChainLinkItem chainLinkItem2 = chainLinkItem.clone();

      if (nextChainLink.getState() != Thread.State.TERMINATED) {
        nextChainLink.addItem(chainLinkItem2);
      }
    }

  }

  public void finalizeNextChainLink() {
    nextChainLink.finalizeChainLink();
  }

  public void setPreviousChainLinkFinalized() {
    nextChainLink.setPreviousChainLinkFinalized();
  }

}
