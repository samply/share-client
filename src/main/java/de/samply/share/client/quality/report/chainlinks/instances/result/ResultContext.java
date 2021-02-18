package de.samply.share.client.quality.report.chainlinks.instances.result;

import de.samply.share.model.ccp.QueryResult;


public interface ResultContext {

  String getLocationUrl();

  int getMaxPages();

  int getPage();

  void incrPage();

  QueryResult getQueryResult();

  void setQueryResult(QueryResult queryResult);

  boolean areResultsCompleted();


}
