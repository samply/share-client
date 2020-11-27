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

package de.samply.share.client.util.db;

import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation of a Jooq ConnectionProvider
 *
 * Uses the ResourceManager to get and release connections
 */
public class ConnectionProviderImpl implements org.jooq.ConnectionProvider {

    /**
     * Acquire a connection from the connection lifecycle handler.
     * <p>
     * This method is called by jOOQ exactly once per execution lifecycle, i.e.
     * per {@link ExecuteContext}. Implementations may freely chose, whether
     * subsequent calls to this method:
     * <ul>
     * <li>return the same connection instance</li>
     * <li>return the same connection instance for the same thread</li>
     * <li>return the same connection instance for the same transaction (e.g. a
     * <code>javax.transaction.UserTransaction</code>)</li>
     * <li>return a fresh connection instance every time</li>
     * </ul>
     * <p>
     * jOOQ will guarantee that every acquired connection is released through
     * {@link #release(Connection)} exactly once.
     *
     * @return A connection for the current <code>ExecuteContext</code>.
     * @throws DataAccessException If anything went wrong while acquiring a
     *                             connection
     */
    @Override
    public Connection acquire() throws DataAccessException {
        try {
            return ResourceManager.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("Error acquiring connection", e);
        }
    }

    /**
     * Release a connection to the connection lifecycle handler.
     * <p>
     * jOOQ will guarantee that every acquired connection is released exactly
     * once.
     *
     * @param connection A connection that was previously obtained from
     *                   {@link #acquire()}. This is never <code>null</code>.
     * @throws DataAccessException If anything went wrong while releasing a
     *                             connection
     */
    @Override
    public void release(Connection connection) throws DataAccessException {
        try {
            connection.close();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error while closing", e);
        }
    }
}
