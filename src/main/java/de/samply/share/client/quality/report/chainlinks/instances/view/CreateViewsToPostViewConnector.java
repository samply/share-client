package de.samply.share.client.quality.report.chainlinks.instances.view;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.connector.ChainLinkConnector;
import de.samply.share.model.common.View;


public class CreateViewsToPostViewConnector<I extends ChainLinkItem & ViewsContext & ViewContext>
    extends ChainLinkConnector {

  public CreateViewsToPostViewConnector(ChainLink nextChainLink) {
    super(nextChainLink);
  }

  @Override
  public void addItemToNextChainLink(ChainLinkItem chainLinkItem) {

    postViews((I) chainLinkItem);

  }

  private void postViews(I item) {

    for (View view : item.getViews()) {

      item.setView(view);
      super.addItemToNextChainLink(item);

    }

  }


}
