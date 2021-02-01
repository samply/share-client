package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.BrokerDao;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.records.BrokerRecord;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with broker objects.
 */
public class BrokerUtil {

  private static final Logger logger = LogManager.getLogger(BrokerUtil.class);

  private static final BrokerDao brokerDao;

  static {
    brokerDao = new BrokerDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private BrokerUtil() {
  }

  /**
   * Get the broker DAO.
   *
   * @return the broker DAO
   */
  public static BrokerDao getBrokerDao() {
    return brokerDao;
  }

  /**
   * Get a list of all brokers.
   *
   * @return list of all brokers
   */
  public static List<Broker> fetchBrokers() {
    return brokerDao.findAll();
  }

  /**
   * Get one broker.
   *
   * @param id id of the broker
   * @return the broker
   */
  public static Broker fetchBrokerById(int id) {
    return brokerDao.fetchOneById(id);
  }

  /**
   * Insert a new broker into the database.
   *
   * @param broker the new broker to insert
   * @return the assigned database id of the newly inserted broker
   */
  public static int insertBroker(Broker broker) {
    DSLContext dslContext = ResourceManager.getDslContext();
    BrokerRecord brokerRecord = dslContext.newRecord(Tables.BROKER, broker);
    brokerRecord.store();
    brokerRecord.refresh();
    return brokerRecord.getId();
  }

  /**
   * Update a broker in the database.
   *
   * @param broker the broker to update
   */
  public static void updateBroker(Broker broker) {
    brokerDao.update(broker);
  }

  /**
   * Delete a broker from the database.
   *
   * @param broker the broker to delete
   */
  public static void deleteBroker(Broker broker) {
    brokerDao.delete(broker);
  }

  /**
   * Get the number of brokers in the database.
   *
   * @return the number of brokers
   */
  public static long getCount() {
    return brokerDao.count();
  }
}
