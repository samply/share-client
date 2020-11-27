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

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.ContactDao;
import de.samply.share.client.model.db.tables.pojos.Contact;
import de.samply.share.client.model.db.tables.records.ContactRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

import java.sql.SQLException;

/**
 * Helper Class for CRUD operations with contact objects
 */
public class ContactUtil {

    private static final Logger logger = LogManager.getLogger(ContactUtil.class);

    private static ContactDao contactDao;

    static {
        contactDao = new ContactDao(ResourceManager.getConfiguration());
    }

    private ContactUtil() {
    }

    /**
     * Get one contact
     *
     * @param id id of the contact
     * @return the contact
     */
    public static Contact fetchContactById(int id) {
        return contactDao.fetchOneById(id);
    }

    /**
     * Insert a new contact into the database
     *
     * @param contact the new contact to insert
     * @return the assigned database id of the newly inserted contact
     */
    public static int insertContact(Contact contact) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        ContactRecord contactRecord = dslContext.newRecord(Tables.CONTACT, contact);
        contactRecord.store();
        contactRecord.refresh();
        return contactRecord.getId();
    }

    /**
     * Insert a new contact into the database
     *
     * @param contact the new contact to insert
     * @return the assigned database id of the newly inserted contact
     */
    public static int insertContact(de.samply.share.model.common.Contact contact) {
        DSLContext dslContext = ResourceManager.getDSLContext();
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
