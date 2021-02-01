package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import java.util.ArrayList;
import java.util.List;

public class CentraXXundIdManagementResultBuilder implements LdmResultBuilder {


  private final LdmConnectorCentraxx ldmConnectorCentraxx;
  private final IdManagementResultGetter idManagementResultGetter;


  public CentraXXundIdManagementResultBuilder(LdmConnectorCentraxx ldmConnectorCentraxx,
      IdManagementResultGetter idManagementResultGetter) {
    this.ldmConnectorCentraxx = ldmConnectorCentraxx;
    this.idManagementResultGetter = idManagementResultGetter;
  }


  @Override
  public QueryResult getResults(String location) throws LdmConnectorException {
    return buildResults(() -> ldmConnectorCentraxx.getResults(location));
  }

  @Override
  public QueryResult getResultsFromPage(String location, int page) throws LdmConnectorException {
    return buildResults(() -> ldmConnectorCentraxx.getResultsFromPage(location, page));
  }

  private QueryResult buildResults(QueryResultSupplier queryResultSupplier)
      throws LdmConnectorException {

    QueryResult queryResult = queryResultSupplier.get();
    return addQueryResultFromIdManagement(queryResult);

  }

  private QueryResult addQueryResultFromIdManagement(QueryResult queryResult)
      throws LdmConnectorException {
    try {
      return addQueryResultFromIdManagement_WithoutManagementException(queryResult);
    } catch (QueryResultsBuilderException | IdManagementResultGetterException e) {
      throw new LdmConnectorException(e);
    }
  }

  private QueryResult addQueryResultFromIdManagement_WithoutManagementException(
      QueryResult queryResult)
      throws QueryResultsBuilderException, IdManagementResultGetterException {

    List<String> patientIds = getPatientIds(queryResult);
    QueryResult idManagementQueryResult = idManagementResultGetter.getResult(patientIds);

    QueryResultsBuilder queryResultsBuilder = new QueryResultsBuilder();
    queryResultsBuilder.addQueryResult(queryResult);
    queryResultsBuilder.addQueryResult(idManagementQueryResult);

    return queryResultsBuilder.getNextQueryResult();

  }

  private List<String> getPatientIds(QueryResult queryResult) {

    List<String> patientIds = new ArrayList<>();

    for (Patient patient : queryResult.getPatient()) {
      patientIds.add(patient.getId());
    }

    return patientIds;

  }

  private interface QueryResultSupplier {

    QueryResult get() throws LdmConnectorException;
  }

}
