package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.ContactDao;
import de.samply.share.client.model.db.tables.pojos.Contact;
import de.samply.share.client.model.db.tables.records.ContactRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with contact objects.
 */
public class ContactUtil {

  private static final Logger logger = LogManager.getLogger(ContactUtil.class);

  private static final ContactDao contactDao;

  static {
    contactDao = new ContactDao(ResourceManager.getConfiguration());
  }

  private ContactUtil() {
  }

  /**
   * Get one contact.
   *
   * @param id id of the contact
   * @return the contact
   */
  public static Contact fetchContactById(int id) {
    return contactDao.fetchOneById(id);
  }

  /**
   * Insert a new contact into the database.
   *
   * @param contact the new contact to insert
   * @return the assigned database id of the newly inserted contact
   */
  public static int insertContact(Contact contact) {
    DSLContext dslContext = ResourceManager.getDslContext();
    ContactRecord contactRecord = dslContext.newRecord(Tables.CONTACT, contact);
    contactRecord.store();
    contactRecord.refresh();
    return contactRecord.getId();
  }

  /**
   * Insert a new contact into the database.
   *
   * @param contact the new contact to insert
   * @return the assigned database id of the newly inserted contact
   */
  public static int insertContact(de.samply.share.model.common.Contact contact) {
    DSLContext dslContext = ResourceManager.getDslContext();
    ContactRecord contactRecord = dslContext.newRecord(Tables.CONTACT);
    contactRecord.setEmail(contact.getEmail());
    contactRecord.setFirstName(contact.getFirstname());
    contactRecord.setLastName(contact.getLastname());
    contactRecord.setOrganizationName(contact.getOrganization());
    contactRecord.setTitle(contact.getTitle());
    contactRecord.setPhone(contact.getPhone());
    contactRecord.store();
    contactRecord.refresh();
    return contactRecord.getId();
  }
}
