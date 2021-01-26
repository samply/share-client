package de.samply.share.client.util.connector;

import de.samply.share.client.util.connector.exception.LdmConnectorException;

/**
 * An interface for a connector to local data management systems.
 *
 * @param <QueryT>          QueryResult type
 * @param <PostParameterT> postQuery parameter type
 * @param <T_RESULT>         Query type
 */
public interface LdmBasicConnector<QueryT, PostParameterT extends AbstractLdmPostQueryParameter,
    T_RESULT> {

  /**
   * Posts a query to local datamanagement and returns the location of the result.
   *
   * @param query     the query
   * @param parameter combines parameters for posting a query
   * @return the location of the result
   * @throws LdmConnectorException LdmConnectorException
   */
  String postQuery(QueryT query, PostParameterT parameter) throws LdmConnectorException;

  /**
   * Gets the query result from a given query location.
   *
   * @param location the location
   * @return the results
   * @throws LdmConnectorException LdmConnectorException
   */
  T_RESULT getResults(String location) throws LdmConnectorException;

  T_RESULT getResultsFromPage(String location, int page) throws LdmConnectorException;

}
