package de.samply.share.client.quality.report.chainlinks.instances.result;

import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.LdmChainLink;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterException;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;
import de.samply.share.model.ccp.QueryResult;


public class GetResultsChainLink<I extends ChainLinkItem & ResultContext> extends LdmChainLink<I> {

  /**
   * Todo.
   *
   * @param localDataManagementRequester Todo.
   */
  public GetResultsChainLink(LocalDataManagementRequester localDataManagementRequester) {

    super(localDataManagementRequester);

  }

  @Override
  protected LocalDataManagementResponse getLocalDataManagementResponse(I chainLinkItem)
      throws ChainLinkException {

    try {
      return (chainLinkItem.getMaxPages() > 0) ? localDataManagementRequester
          .getQueryResult(chainLinkItem.getLocationUrl(), chainLinkItem.getPage()) : null;
    } catch (LocalDataManagementRequesterException e) {
      throw new ChainLinkException(e);
    }

  }

  @Override
  protected I process(I chainLinkItem, LocalDataManagementResponse localDataManagementResponse) {

    if (localDataManagementResponse != null) {

      chainLinkItem.setQueryResult((QueryResult) localDataManagementResponse.getResponse());

      if (localDataManagementResponse.isSuccessful()) {

        chainLinkItem.incrPage();
        if (!chainLinkItem.areResultsCompleted()) {
          chainLinkItem.setToBeReused();
        }

      }

    }

    return chainLinkItem;

  }

  @Override
  protected String getChainLinkId() {
    return "Local Data Management Results Getter";
  }

  @Override
  protected int getNumberOfItemsToBeProcessed() {

    int numberOfItemsToBeProcessed = 0;

    for (ChainLinkItem item : deque) {

      I getResultsItem = (I) item;
      int remainingPages = getResultsItem.getMaxPages() - getResultsItem.getPage();

      numberOfItemsToBeProcessed += remainingPages;
    }

    return numberOfItemsToBeProcessed;

  }


}
