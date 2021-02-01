package de.samply.share.client.util.connector;

import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.model.common.QueryResultStatistic;
import java.io.IOException;

/**
 * An interface for a connector to local data management systems.
 *
 * @param <QueryT>         QueryResult type
 * @param <PostParameterT> postQuery parameter type
 * @param <T_RESULT>       Query type
 */
public interface LdmConnector<QueryT, PostParameterT extends AbstractLdmPostQueryParameter,
    T_RESULT> extends LdmBasicConnector<QueryT, PostParameterT, T_RESULT> {

  String TEMPDIR = "javax.servlet.context.tempdir";
  String XML_SUFFIX = ".xml";


  /**
   * Gets the stats for a query on the given location.
   *
   * @param location the location
   * @return the stats
   * @throws LdmConnectorException LdmConnectorException
   */
  LdmQueryResult getStatsOrError(String location) throws LdmConnectorException;

  QueryResultStatistic getQueryResultStatistic(String location) throws LdmConnectorException;

  /**
   * Gets the result count for a query on a given location.
   *
   * @param location the location
   * @return the result count
   * @throws LdmConnectorException LdmConnectorException
   */
  Integer getResultCount(String location) throws LdmConnectorException;

  /**
   * Gets the page count for a query on a given location.
   *
   * @param location the location
   * @return the result count
   * @throws LdmConnectorException LdmConnectorException
   */
  Integer getPageCount(String location) throws LdmConnectorException;

  /**
   * Check if the first result page is available.
   *
   * @return true if the result is available or if the stats are available and there are 0 results
   * @throws LdmConnectorException LdmConnectorException
   */
  boolean isFirstResultPageAvailable(String location) throws LdmConnectorException;

  /**
   * Check if the last page of the result is already written.
   *
   * @return true if the result is available or if the stats are available and there are 0 results
   * @throws LdmConnectorException LdmConnectorException
   */
  boolean isResultDone(String location, QueryResultStatistic qrs) throws LdmConnectorException;

  /**
   * Write a page of transformed patients to disk (used for dryrun).
   *
   * @param queryResult the queryResult
   * @param index       the number of the page in the result
   * @throws IOException IOException
   */
  void writeQueryResultPageToDisk(T_RESULT queryResult, int index) throws IOException;

  /**
   * Get the name and version number of the local datamanagement.
   *
   * @return local datamanagement name and version number, separated by a forward slash
   */
  String getUserAgentInfo() throws LdmConnectorException;

  /**
   * Check if the local datamanagement is reachable.
   *
   * @return CheckResult with outcome and messages
   */
  CheckResult checkConnection();

  /**
   * Get the amount of patients in centraxx.
   *
   * @param dktkFlagged when true, only count those with dktk consent. when false, count ALL (not
   *                    just those without consent)
   * @return the amount of patients in centraxx
   */
  int getPatientCount(boolean dktkFlagged) throws LdmConnectorException, InterruptedException;

  /**
   * Execute a reference query and return amount of patients and execution time.
   *
   * @param referenceQuery the query to execute
   * @return amount of patients and execution time
   */
  ReferenceQueryCheckResult getReferenceQueryCheckResult(QueryT referenceQuery)
      throws LdmConnectorException;

  default boolean isLdmCentraxx() {
    return false;
  }

  default boolean isLdmSamplystoreBiobank() {
    return false;
  }

  default boolean isLdmCql() {
    return false;
  }
}
