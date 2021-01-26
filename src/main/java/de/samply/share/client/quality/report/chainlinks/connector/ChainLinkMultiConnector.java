package de.samply.share.client.quality.report.chainlinks.connector;

import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import java.util.ArrayList;
import java.util.List;

public class ChainLinkMultiConnector extends ChainLinkConnector {

  private final List<ChainLinkConnector> chainLinkConnectors = new ArrayList<>();


  public ChainLinkMultiConnector() {
    super(null);
  }

  public void addChainLinkConnector(ChainLinkConnector chainLinkConnector) {
    chainLinkConnectors.add(chainLinkConnector);
  }

  /**
   * Todo.
   *
   * @param chainLinkItem Todo.
   */
  public void addItemToNextChainLink(ChainLinkItem chainLinkItem) {
    for (ChainLinkConnector chainLinkConnector : chainLinkConnectors) {
      chainLinkConnector.addItemToNextChainLink(chainLinkItem);
    }

  }

  /**
   * Todo.
   */
  public void finalizeNextChainLink() {
    for (ChainLinkConnector chainLinkConnector : chainLinkConnectors) {
      chainLinkConnector.finalizeNextChainLink();
    }

  }

  /**
   * Todo.
   */
  public void setPreviousChainLinkFinalized() {
    for (ChainLinkConnector chainLinkConnector : chainLinkConnectors) {
      chainLinkConnector.setPreviousChainLinkFinalized();
    }

  }


}
