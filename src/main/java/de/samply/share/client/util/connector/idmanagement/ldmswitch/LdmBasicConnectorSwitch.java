package de.samply.share.client.util.connector.idmanagement.ldmswitch;

import de.samply.share.client.util.connector.LdmBasicConnector;
import de.samply.share.client.util.connector.LdmPostQueryParameterView;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.connector.idmanagement.query.LdmId;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverter;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverterException;
import de.samply.share.client.util.connector.idmanagement.results.LdmResultBuilder;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Query;
import java.util.HashMap;
import java.util.Map;

public class LdmBasicConnectorSwitch implements
    LdmBasicConnector<Query, LdmPostQueryParameterView, QueryResult> {


  private final LdmQueryConverter ldmQueryConverter;
  private final LdmQueryLocationMapper ldmQueryLocationMapper;
  private final Map<LdmId, LdmBasicConnector> ldmIdLdmBasicConnectorMap = new HashMap<>();
  private final LdmResultBuilder ldmResultBuilder;

  /**
   * Todo David.
   * @param ldmConnectorSwitchParameters Todo David.
   */
  public LdmBasicConnectorSwitch(LdmConnectorSwitchParameters ldmConnectorSwitchParameters) {

    this.ldmQueryConverter = ldmConnectorSwitchParameters.getLdmQueryConverter();
    this.ldmQueryLocationMapper = ldmConnectorSwitchParameters.getLdmQueryLocationMapper();
    this.ldmResultBuilder = ldmConnectorSwitchParameters.getLdmResultBuilder();

  }

  @Override
  public String postQuery(Query query, LdmPostQueryParameterView parameter)
      throws LdmConnectorException {

    LdmQueryLocations ldmQueryLocations = new LdmQueryLocations();

    for (LdmId ldmId : ldmIdLdmBasicConnectorMap.keySet()) {

      String ldmQueryLocation = postQuery(ldmId, query, parameter);
      if (ldmQueryLocation != null) {
        ldmQueryLocations.addLdmQueryLocation(ldmId, ldmQueryLocation);
      }

    }
    return ldmQueryLocationMapper.generateGlobalQueryLocation(ldmQueryLocations);
  }

  private String postQuery(LdmId ldmId, Query query, LdmPostQueryParameterView parameter)
      throws LdmConnectorException {
    String ldmQueryLocation = null;
    LdmBasicConnector ldmBasicConnector = ldmIdLdmBasicConnectorMap.get(ldmId);
    if (ldmBasicConnector != null) {
      Query ldmQuery = convertQuery(ldmId, query);
      if (ldmQuery != null) {
        ldmQueryLocation = ldmBasicConnector.postQuery(ldmQuery, parameter);
      }
    }
    return ldmQueryLocation;
  }

  @Override
  public QueryResult getResults(String location) throws LdmConnectorException {
    return ldmResultBuilder.getResults(location);
  }

  @Override
  public QueryResult getResultsFromPage(String location, int page) throws LdmConnectorException {
    return ldmResultBuilder.getResultsFromPage(location, page);
  }

  private Query convertQuery(LdmId ldmId, Query query) throws LdmConnectorException {

    try {

      return ldmQueryConverter.convertQuery(ldmId, query);

    } catch (LdmQueryConverterException e) {
      throw new LdmConnectorException(e);
    }

  }

  /**
   * Todo David.
   * @param ldmId Todo David
   * @param ldmBasicConnector Todo David
   */
  public void addLdmBasicConnector(LdmId ldmId, LdmBasicConnector ldmBasicConnector) {
    ldmIdLdmBasicConnectorMap.put(ldmId, ldmBasicConnector);
  }

}
