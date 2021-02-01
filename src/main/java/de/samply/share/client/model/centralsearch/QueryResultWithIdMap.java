package de.samply.share.client.model.centralsearch;

import de.samply.share.client.model.IdObject;
import de.samply.share.model.ccp.QueryResult;
import java.util.Map;

/**
 * Holds a query result as well as a map with ids and their export ids.
 */
public class QueryResultWithIdMap {

  private QueryResult queryResult;
  private Map<IdObject, IdObject> idMap;

  public QueryResultWithIdMap(QueryResult queryResult, Map<IdObject, IdObject> idMap) {
    this.queryResult = queryResult;
    this.idMap = idMap;
  }

  public QueryResult getQueryResult() {
    return queryResult;
  }

  public void setQueryResult(QueryResult queryResult) {
    this.queryResult = queryResult;
  }

  public Map<IdObject, IdObject> getIdMap() {
    return idMap;
  }

  public void setIdMap(Map<IdObject, IdObject> idMap) {
    this.idMap = idMap;
  }
}
