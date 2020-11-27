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
import de.samply.share.client.model.db.tables.pojos.UserSeenInquiry;
import de.samply.share.client.model.db.tables.daos.UserSeenInquiryDao;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

import java.sql.SQLException;

/**
 * Helper Class for CRUD operations with user seen inquiry relations
 */
public class UserSeenInquiryUtil {

    private static final Logger logger = LogManager.getLogger(UserSeenInquiryUtil.class);

    private static UserSeenInquiryDao userSeenInquiryDao;

    static {
        userSeenInquiryDao = new UserSeenInquiryDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private UserSeenInquiryUtil() {
    }

    /**
     * Get the user seen inquiry DAO
     *
     * @return the user seen inquiry DAO
     */
    public static UserSeenInquiryDao getUserSeenInquiryDao() {
        return userSeenInquiryDao;
    }

    /**
     * Get a user seen inquiry relation
     *
     * @param userId the id of the user
     * @param inquiryId the id of the inquiry
     * @return an object if the user has seen the inquiry, null otherwise
     */
    public static UserSeenInquiry fetchUserSeenInquiryByUserIdAndInquiryId(int userId, int inquiryId) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        return dslContext
                .selectFrom(Tables.USER_SEEN_INQUIRY)
                .where(Tables.USER_SEEN_INQUIRY.USER_ID.equal(userId))
                .and(Tables.USER_SEEN_INQUIRY.INQUIRY_ID.equal(inquiryId))
                .fetchOneInto(UserSeenInquiry.class);
    }

    /**
     * Get a user seen inquiry relation
     *
     * @param user the user
     * @param inquiry the inquiry
     * @return an object if the user has seen the inquiry, null otherwise
     */
    public static UserSeenInquiry fetchUserSeenInquiryByUserAndInquiry(User user, Inquiry inquiry) {
        return fetchUserSeenInquiryByUserIdAndInquiryId(user.getId(), inquiry.getId());
    }

    /**
     * Check if a user has seen a certain inquiry
     *
     * @param userId the id of the user
     * @param inquiryId the id of the inquiry
     * @return if the user has seen the inquiry
     */
    public static boolean hasUserSeenInquiryByIds(int userId, int inquiryId) {
        return fetchUserSeenInquiryByUserIdAndInquiryId(userId, inquiryId) != null;
    }

    /**
     * Check if a user has seen a certain inquiry
     *
     * @param user the user
     * @param inquiry the inquiry
     * @return if the user has seen the inquiry
     */
    public static boolean hasUserSeenInquiry(User user, Inquiry inquiry) {
        return fetchUserSeenInquiryByUserAndInquiry(user, inquiry) != null;
    }

    /**
     * Mark an inquiry as seen by the user
     *
     * @param user the user
     * @param inquiry the inquiry
     */
    public static void setUserSeenInquiry(User user, Inquiry inquiry) {
        if (!hasUserSeenInquiry(user, inquiry)) {
            insertUserSeenInquiry(new UserSeenInquiry(user.getId(), inquiry.getId()));
        }
    }

    /**
     * Insert a new user has seen inquiry relation to the database
     *
     * @param usi the user has seen inquiry object
     */
    private static void insertUserSeenInquiry(UserSeenInquiry usi) {
        userSeenInquiryDao.insert(usi);
    }

    /**
     * Delete a user has seen inquiry relation from the database
     *
     * @param user the user
     * @param inquiry the inquiry
     */
    private static void removeUserSeenInquiry(User user, Inquiry inquiry) {
        UserSeenInquiry usi = fetchUserSeenInquiryByUserAndInquiry(user, inquiry);
        if (usi != null) {
            userSeenInquiryDao.delete(usi);
        }
    }
}
