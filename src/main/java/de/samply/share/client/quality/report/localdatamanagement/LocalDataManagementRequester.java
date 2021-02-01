package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.View;

public interface LocalDataManagementRequester {

  LocalDataManagementResponse<String> postViewAndGetLocationUrlStatisticsOnly(View view)
      throws LocalDataManagementRequesterException;

  LocalDataManagementResponse<String> postViewAndGetLocationUrl(View view)
      throws LocalDataManagementRequesterException;

  LocalDataManagementResponse<QueryResultStatistic> getQueryResultStatistic(
      String locationUrl) throws LocalDataManagementRequesterException;

  LocalDataManagementResponse<QueryResult> getQueryResult(String locationUrl, int page)
      throws LocalDataManagementRequesterException;

  LocalDataManagementResponse<String> getSqlMappingVersion()
      throws LocalDataManagementRequesterException;


}
