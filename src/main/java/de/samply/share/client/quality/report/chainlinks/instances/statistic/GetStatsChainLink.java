package de.samply.share.client.quality.report.chainlinks.instances.statistic;

import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.LdmChainLink;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterException;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;
import de.samply.share.model.common.QueryResultStatistic;

public class GetStatsChainLink<I extends ChainLinkItem & StatisticContext> extends
    LdmChainLink<I> {


  public GetStatsChainLink(LocalDataManagementRequester localDataManagementRequester) {
    super(localDataManagementRequester);
  }

  @Override
  protected LocalDataManagementResponse getLocalDataManagementResponse(I chainLinkItem)
      throws ChainLinkException {

    try {
      return localDataManagementRequester.getQueryResultStatistic(chainLinkItem.getLocationUrl());
    } catch (LocalDataManagementRequesterException e) {
      throw new ChainLinkException(e);
    }

  }

  @Override
  protected I process(I chainLinkItem, LocalDataManagementResponse localDataManagementResponse) {

    QueryResultStatistic queryResultStatistic = getQueryResultStatistic(
        localDataManagementResponse);

    logNumberOfPages(queryResultStatistic);

    if (queryResultStatistic != null) {
      chainLinkItem.setMaxPages(queryResultStatistic.getNumberOfPages());
    } else {
      chainLinkItem.setNotToBeForwarded();
    }
    //chainLinkItem.setMaxPages(0);
    return chainLinkItem;

  }

  private void logNumberOfPages(QueryResultStatistic queryResultStatistic) {

    if (queryResultStatistic != null) {
      logger.info("Number of elements to be requested: " + queryResultStatistic.getNumberOfPages());
    }

  }

  private QueryResultStatistic getQueryResultStatistic(
      LocalDataManagementResponse<QueryResultStatistic> localDataManagementResponse) {
    return localDataManagementResponse.getResponse();
  }

  @Override
  protected String getChainLinkId() {
    return "Local Data Management Statistics Getter";
  }
}
