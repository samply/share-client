package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.View;

public interface LocalDataManagementRequester {

  public LocalDataManagementResponse<String> postViewAndGetLocationUrlStatisticsOnly(View view)
      throws LocalDataManagementRequesterException;

  public LocalDataManagementResponse<String> postViewAndGetLocationUrl(View view)
      throws LocalDataManagementRequesterException;

  public LocalDataManagementResponse<QueryResultStatistic> getQueryResultStatistic(
      String locationUrl) throws LocalDataManagementRequesterException;

  public LocalDataManagementResponse<QueryResult> getQueryResult(String locationUrl, int page)
      throws LocalDataManagementRequesterException;

  public LocalDataManagementResponse<String> getSqlMappingVersion()
      throws LocalDataManagementRequesterException;


}
