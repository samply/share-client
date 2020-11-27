package de.samply.share.client.quality.report.chainlinks.instances.view;

import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterException;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;

public class PostViewOnlyStatisticsChainLink<I extends ChainLinkItem & ViewContext> extends
    PostViewChainLink<I> {


  public PostViewOnlyStatisticsChainLink(
      LocalDataManagementRequester localDataManagementRequester) {
    super(localDataManagementRequester);
  }

  @Override
  protected LocalDataManagementResponse getLocalDataManagementResponse(I chainLinkItem)
      throws ChainLinkException {

    try {
      return localDataManagementRequester
          .postViewAndGetLocationUrlStatisticsOnly(chainLinkItem.getView());
    } catch (LocalDataManagementRequesterException e) {
      throw new ChainLinkException(e);
    }

  }

}
