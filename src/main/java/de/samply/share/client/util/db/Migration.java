package de.samply.share.client.util.db;

import de.samply.share.common.utils.ProjectInfo;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import javax.sql.DataSource;

/**
 * Take care of database migration
 *
 * Uses flyway to upgrade/migrate database schemas
 */
public class Migration {
    private Migration() {
    }

    public static void doUpgrade() throws FlywayException {
        upgradeSamply(ResourceManager.getDataSource());
    }

    /**
     * Upgrade the samply schema
     *
     * @param dataSource which datasource to use
     */
    private static void upgradeSamply(DataSource dataSource) throws FlywayException {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        if(ProjectInfo.INSTANCE.getProjectName().equals("dktk")) {
            flyway.setLocations("db/migration/dktk");
        }else if(ProjectInfo.INSTANCE.getProjectName().equals("samply")){
            flyway.setLocations("db/migration/samply");
        }
        flyway.setSchemas("samply");
        flyway.migrate();
    }
}
