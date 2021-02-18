package de.samply.share.client.util.db;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

/**
 * Take care of database migration. Uses flyway to upgrade/migrate database schemas.
 */
public class Migration {

  private Migration() {
  }

  public static void doUpgrade() throws FlywayException {
    upgradeSamply(ResourceManager.getDataSource());
  }

  /**
   * Upgrade the samply schema.
   *
   * @param dataSource which datasource to use
   */
  private static void upgradeSamply(DataSource dataSource) throws FlywayException {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.setLocations("db/migration_generated/");
    flyway.setSchemas("samply");
    flyway.setIgnoreMissingMigrations(true);
    flyway.migrate();
  }
}
