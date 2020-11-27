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

import de.samply.share.client.model.db.tables.daos.TokenDao;
import de.samply.share.client.model.db.tables.pojos.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * Helper Class for CRUD operations with token objects
 */
public class TokenUtil {

    private static final Logger logger = LogManager.getLogger(TokenUtil.class);

    private static TokenDao tokenDao;

    static {
        tokenDao = new TokenDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private TokenUtil() {
    }

    /**
     * Get the token DAO
     *
     * @return the token DAO
     */
    public static TokenDao getTokenDao() {
        return tokenDao;
    }

    /**
     * Get one token
     *
     * @param id id of the token
     * @return the token
     */
    public static Token fetchTokenById(int id) {
        return tokenDao.fetchOneById(id);
    }
}
