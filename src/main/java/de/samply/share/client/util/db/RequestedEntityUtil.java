package de.samply.share.client.util.db;

import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.tables.daos.InquiryRequestedEntityDao;
import de.samply.share.client.model.db.tables.daos.RequestedEntityDao;
import de.samply.share.client.model.db.tables.pojos.InquiryRequestedEntity;
import de.samply.share.client.model.db.tables.pojos.RequestedEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper Class for CRUD operations with requested entity objects.
 */
public class RequestedEntityUtil {

  private static final Logger logger = LogManager.getLogger(BrokerUtil.class);

  private static final RequestedEntityDao requestedEntityDao;
  private static final InquiryRequestedEntityDao inquiryRequestedEntityDao;

  static {
    requestedEntityDao = new RequestedEntityDao(ResourceManager.getConfiguration());
    inquiryRequestedEntityDao = new InquiryRequestedEntityDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private RequestedEntityUtil() {
  }

  /**
   * Get the requested entity DAO.
   *
   * @return the requested entity DAO
   */
  public static RequestedEntityDao getRequestedEntityDao() {
    return requestedEntityDao;
  }

  /**
   * Get the id of an entity type.
   *
   * @param value the enum value of the entity type
   * @return the id of the entity type
   */
  public static int getIdForValue(EntityType value) {
    return requestedEntityDao.fetchOneByName(value).getId();
  }

  /**
   * Get the entity for the entity value.
   *
   * @param value the value of the wanted entity
   * @return the entity
   */
  public static RequestedEntity getRequestedEntityForValue(EntityType value) {
    return requestedEntityDao.fetchOneByName(value);
  }

  /**
   * Insert a new relation between inquiry and requested entity into the database.
   *
   * @param inquiryId       the id of the inquiry for which the entity is requested
   * @param requestedEntity the requested entity
   */
  public static void insertInquiryIdRequestedEntity(int inquiryId,
      RequestedEntity requestedEntity) {
    InquiryRequestedEntity ire = new InquiryRequestedEntity();
    ire.setInquiryId(inquiryId);
    ire.setRequestedEntityId(requestedEntity.getId());
    inquiryRequestedEntityDao.insert(ire);
  }
}
