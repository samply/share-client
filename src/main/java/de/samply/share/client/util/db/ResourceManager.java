package de.samply.share.client.util.db;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Offers objects to handle database connections
 */
public class ResourceManager implements Serializable {

    @Resource(name = "jdbc/postgres")
    private static final DataSource dataSource;

    private static final String JAVA_COMP_ENV = "java:comp/env";

    private static final String JDBC_POSTGRES = "jdbc/postgres";

    static {
        try {
            Context initContext = new InitialContext();
            Context context = (Context) initContext.lookup(JAVA_COMP_ENV);
            dataSource = (DataSource) context.lookup(JDBC_POSTGRES);
        } catch (NamingException ex) {
            throw new ExceptionInInitializerError("dataSource not initialized");
        }
    }

    /**
     * Gets the Domain Specific Language (DSL) context.
     *
     * @param connection the database connection to be used to create the DSL context
     * @return the DSL context
     */
    static synchronized DSLContext getDSLContext() {
        Configuration configuration = new DefaultConfiguration().set(new ConnectionProviderImpl()).set(SQLDialect.POSTGRES);
        return DSL.using(configuration);
    }

    /**
     * Gets a jooq configuration with custom connection provider.
     *
     * @return the configuration
     * @throws SQLException if {@link this.getConnection()} fails
     */
    public static synchronized Configuration getConfiguration() {
        return new DefaultConfiguration().set(new ConnectionProviderImpl()).set(SQLDialect.POSTGRES);
    }

    /**
     * Gets the database connection.
     *
     * @return the connection
     * @throws SQLException if the connection to the database fails (due to wrong url or
     *                      credentials)
     */
    static synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Close a database connection and ignore SQLException if it occurs.
     *
     * @param connection The connection to close.
     */
    public static void close(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
