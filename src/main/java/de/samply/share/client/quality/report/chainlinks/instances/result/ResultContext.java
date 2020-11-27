package de.samply.share.client.quality.report.chainlinks.instances.result;

import de.samply.share.model.ccp.QueryResult;


public interface ResultContext {

  public String getLocationUrl();

  public int getMaxPages();

  public int getPage();

  public void incrPage();

  public QueryResult getQueryResult();

  public void setQueryResult(QueryResult queryResult);

  public boolean areResultsCompleted();


}
