/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.util.connector;

import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.QueryResultStatistic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface for a connector to local data management systems
 *
 * @param <T> QueryResult type
 * @param <U> Patient type
 */
public interface LdmConnector<T, U> {

    String BIRTHDAY_URN = "urn:dktk:dataelement:26:4";
    MdrIdDatatype BIRTHDAY_MDR_ID = new MdrIdDatatype(BIRTHDAY_URN);
    String TEMPDIR = "javax.servlet.context.tempdir";
    String XML_SUFFIX = ".xml";

    /**
     * Posts a query to local datamanagement and returns the location of the result.
     *
     * @param query                       the query
     * @param removeKeysFromView          A list of keys to be removed from the query (and viewfields)
     * @param completeMdsViewFields       if true, add all entries from mds-b and mds-k to viewfields
     * @param statisticsOnly              if true, set a parameter to only request a count of the results, not the whole result lists
     * @param includeAdditionalViewfields if true, check if there are additional viewfields to set in the database. For uploads to central
     *                                    mds database, this should be false
     * @return the location of the result
     * @throws LDMConnectorException
     */
    String postQuery(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException;

    /**
     * Posts an xml view to local datamanagement and returns the location of the result.
     *
     * @param view           the view
     * @param statisticsOnly if true, set a parameter to only request a count of the results, not the whole result lists
     * @return the location of the result
     * @throws LDMConnectorException
     */
    String postViewString(String view, boolean statisticsOnly) throws LDMConnectorException;

    /**
     * Posts an xml criteria snippet to local datamanagement and returns the location of the result.
     *
     * @param criteria                    the criteria
     * @param completeMdsViewFields       if true, add all entries from mds-b and mds-k to viewfields
     * @param statisticsOnly              if true, set a parameter to only request a count of the results, not the whole result lists
     * @param includeAdditionalViewfields if true, check if there are additional viewfields to set in the database. For uploads to central
     *                                    mds database, this should be false
     * @return the location of the result
     * @throws LDMConnectorException
     */
    String postCriteriaString(String criteria, boolean completeMdsViewFields, boolean statisticsOnly, boolean includeAdditionalViewfields) throws LDMConnectorException;

    /**
     * Gets the query result from a given query location.
     *
     * @param location the location
     * @return the results
     * @throws LDMConnectorException
     */
    T getResults(String location) throws LDMConnectorException;

    T getResultsFromPage(String location, int page) throws LDMConnectorException;

    /**
     * Checks if the query is present in the given location.
     *
     * @param location the location where the query should be available
     * @return true, if is query present
     * @throws LDMConnectorException
     */
    boolean isQueryPresent(String location) throws LDMConnectorException;

    /**
     * Gets the stats for a query on the given location.
     *
     * @param location the location
     * @return the stats
     * @throws LDMConnectorException
     */
    Object getStatsOrError(String location) throws LDMConnectorException;

    QueryResultStatistic getQueryResultStatistic(String location) throws LDMConnectorException;

    /**
     * Gets the result count for a query on a given location.
     *
     * @param location the location
     * @return the result count
     * @throws LDMConnectorException
     */
    Integer getResultCount(String location) throws LDMConnectorException;

    /**
     * Gets the page count for a query on a given location.
     *
     * @param location the location
     * @return the result count
     * @throws LDMConnectorException
     */
    Integer getPageCount(String location) throws LDMConnectorException;

    /**
     * Check if the first result page is available
     *
     * @return true if the result is available or if the stats are available and there are 0 results
     * @throws LDMConnectorException
     */
    boolean isFirstResultPageAvailable(String location) throws LDMConnectorException;

    /**
     * Check if the last page of the result is already written.
     *
     * @return true if the result is available or if the stats are available and there are 0 results
     * @throws LDMConnectorException
     */
    boolean isResultDone(String location, QueryResultStatistic qrs) throws LDMConnectorException;

    /**
     * Write a page of transformed patients to disk (used for dryrun)
     *
     * @param queryResult
     * @param index       the number of the page in the result
     * @throws IOException
     */
    void writeQueryResultPageToDisk(T queryResult, int index) throws IOException;

    /**
     * Get the name and version number of the local datamanagement
     *
     * @return local datamanagement name and version number, separated by a forward slash
     */
    String getUserAgentInfo() throws LDMConnectorException;

    /**
     * Check if the local datamanagement is reachable
     *
     * @return CheckResult with outcome and messages
     */
    CheckResult checkConnection();

    /**
     * Get the amount of patients in centraxx
     *
     * @param dktkFlagged when true, only count those with dktk consent. when false, count ALL (not just those without consent)
     * @return the amount of patients in centraxx
     */
    int getPatientCount(boolean dktkFlagged) throws LDMConnectorException, InterruptedException;

    /**
     * Execute a reference query and return amount of patients and execution time
     *
     * @param referenceQuery the query to execute
     * @return amount of patients and execution time
     */
    ReferenceQueryCheckResult getReferenceQueryCheckResult(Query referenceQuery) throws LDMConnectorException;

    default boolean isLdmCentraxx() {
        return false;
    }

    default boolean isLdmSamplystoreBiobank() {
        return true;
    }

    default String getMappingVersion() {
        return "undefined";
    }

    default String getMappingDate() {
        return "undefined";
    }

    default List<CxxMappingElement> getMapping() {
        return new ArrayList<>();
    }

}
