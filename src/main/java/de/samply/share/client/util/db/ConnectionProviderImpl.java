package de.samply.share.client.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.jooq.exception.DataAccessException;

/**
 * Implementation of a Jooq ConnectionProvider. Uses the ResourceManager to get and release
 * connections.
 */
public class ConnectionProviderImpl implements org.jooq.ConnectionProvider {

  /**
   * Acquire a connection from the connection lifecycle handler. This method is called by jOOQ
   * exactly once per execution lifecycle, i.e. per {@link org.jooq.ExecuteContext}. Implementations
   * may freely chose, whether subsequent calls to this method:
   * <ul>
   * <li>return the same connection instance</li>
   * <li>return the same connection instance for the same thread</li>
   * <li>return the same connection instance for the same transaction (e.g. a
   * <code>javax.transaction.UserTransaction</code>)</li>
   * <li>return a fresh connection instance every time</li>
   * </ul>
   * jOOQ will guarantee that every acquired connection is released through
   * {@link #release(Connection)} exactly once.
   *
   * @return A connection for the current <code>ExecuteContext</code>.
   * @throws DataAccessException If anything went wrong while acquiring a connection
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
   * Release a connection to the connection lifecycle handler. jOOQ will guarantee that every
   * acquired connection is released exactly once.
   *
   * @param connection A connection that was previously obtained from {@link #acquire()}. This is
   *                   never <code>null</code>.
   * @throws DataAccessException If anything went wrong while releasing a connection
   */
  @Override
  public void release(Connection connection) throws DataAccessException {
    try {
      connection.close();
    } catch (SQLException e) {
      throw new DataAccessException("Error while closing", e);
    }
  }
}
