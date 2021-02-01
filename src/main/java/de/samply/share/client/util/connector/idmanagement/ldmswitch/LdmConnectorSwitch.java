package de.samply.share.client.util.connector.idmanagement.ldmswitch;

import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.LdmConnectorCcp;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.LdmPostQueryParameterView;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.QueryResultStatistic;
import java.io.IOException;
import java.util.List;

public class LdmConnectorSwitch implements LdmConnectorCcp {

  private final LdmConnectorCentraxx ldmConnectorCentraxx;
  private final LdmBasicConnectorSwitch ldmBasicConnectorSwitch;

  /**
   * Todo David.
   * @param ldmConnectorCentraxx Todo David
   * @param ldmBasicConnectorSwitch Todo David
   */
  public LdmConnectorSwitch(LdmConnectorCentraxx ldmConnectorCentraxx,
      LdmBasicConnectorSwitch ldmBasicConnectorSwitch) {

    this.ldmConnectorCentraxx = ldmConnectorCentraxx;
    this.ldmBasicConnectorSwitch = ldmBasicConnectorSwitch;

  }

  @Override
  public LdmQueryResult getStatsOrError(String location) throws LdmConnectorException {
    return ldmConnectorCentraxx.getStatsOrError(location);
  }

  @Override
  public QueryResultStatistic getQueryResultStatistic(String location)
      throws LdmConnectorException {
    return ldmConnectorCentraxx.getQueryResultStatistic(location);
  }

  @Override
  public Integer getResultCount(String location) throws LdmConnectorException {
    return ldmConnectorCentraxx.getResultCount(location);
  }

  @Override
  public Integer getPageCount(String location) throws LdmConnectorException {
    return ldmConnectorCentraxx.getPageCount(location);
  }

  @Override
  public boolean isFirstResultPageAvailable(String location) throws LdmConnectorException {
    return ldmConnectorCentraxx.isFirstResultPageAvailable(location);
  }

  @Override
  public boolean isResultDone(String location, QueryResultStatistic qrs)
      throws LdmConnectorException {
    return ldmConnectorCentraxx.isResultDone(location, qrs);
  }

  @Override
  public void writeQueryResultPageToDisk(QueryResult queryResult, int index) throws IOException {
    ldmConnectorCentraxx.writeQueryResultPageToDisk(queryResult, index);
  }

  @Override
  public String getUserAgentInfo() throws LdmConnectorException {
    return ldmConnectorCentraxx.getUserAgentInfo();
  }

  @Override
  public CheckResult checkConnection() {
    return ldmConnectorCentraxx.checkConnection();
  }

  @Override
  public int getPatientCount(boolean dktkFlagged)
      throws LdmConnectorException, InterruptedException {
    return ldmConnectorCentraxx.getPatientCount(dktkFlagged);
  }

  @Override
  public ReferenceQueryCheckResult getReferenceQueryCheckResult(Query referenceQuery)
      throws LdmConnectorException {
    return ldmConnectorCentraxx.getReferenceQueryCheckResult(referenceQuery);
  }

  @Override
  public String postQuery(Query query, LdmPostQueryParameterView parameter)
      throws LdmConnectorException {
    return ldmBasicConnectorSwitch.postQuery(query, parameter);
  }

  @Override
  public QueryResult getResults(String location) throws LdmConnectorException {
    return ldmBasicConnectorSwitch.getResults(location);
  }

  @Override
  public QueryResult getResultsFromPage(String location, int page) throws LdmConnectorException {
    return ldmBasicConnectorSwitch.getResultsFromPage(location, page);
  }

  @Override
  public String getMappingVersion() {
    return ldmConnectorCentraxx.getMappingVersion();
  }

  @Override
  public String getMappingDate() {
    return ldmConnectorCentraxx.getMappingDate();
  }

  @Override
  public List<CxxMappingElement> getMapping() {
    return ldmConnectorCentraxx.getMapping();
  }

  // TODO: isldmCentraXX - isXXX is a dangerous logic: Please refactor!
  @Override
  public boolean isLdmCentraxx() {
    return true;
  }

}
