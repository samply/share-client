package de.samply.share.client.quality.report.chainlinks;

import de.samply.share.model.common.Error;

public class ChainLinkError {

  private ChainLinkItem chainLinkItem;
  private Error error;

  public ChainLinkItem getChainLinkItem() {
    return chainLinkItem;
  }

  public void setChainLinkItem(ChainLinkItem chainLinkItem) {
    this.chainLinkItem = chainLinkItem;
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

}
