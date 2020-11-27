package de.samply.share.client.quality.report.chainlinks.connector;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;

public abstract class ChainLinkConnectorFunnel extends ChainLinkConnector {

  public ChainLinkConnectorFunnel(ChainLink nextChainLink) {
    super(nextChainLink);
  }

  protected abstract void keepItemForLater(ChainLinkItem chainLinkItem);

  protected abstract ChainLinkItem getItemForNextChainLink();

  @Override
  public void addItemToNextChainLink(ChainLinkItem chainLinkItem) {
    keepItemForLater(chainLinkItem);
  }

  @Override
  public void setPreviousChainLinkFinalized() {

    ChainLinkItem chainLinkItem = getItemForNextChainLink();
    super.addItemToNextChainLink(chainLinkItem);

    super.setPreviousChainLinkFinalized();

  }


}
