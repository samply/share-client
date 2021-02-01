package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.model.ccp.QueryResult;

public interface LdmResultBuilder {

  QueryResult getResults(String location) throws LdmConnectorException;

  QueryResult getResultsFromPage(String location, int page) throws LdmConnectorException;

}
