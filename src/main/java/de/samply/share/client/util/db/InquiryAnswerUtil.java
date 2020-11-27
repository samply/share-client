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

import de.samply.share.client.model.db.tables.daos.InquiryAnswerDao;
import de.samply.share.client.model.db.tables.pojos.InquiryAnswer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * Helper Class for CRUD operations with inquiry answer objects
 */
public class InquiryAnswerUtil {

    private static final Logger logger = LogManager.getLogger(InquiryAnswerUtil.class);

    private static InquiryAnswerDao inquiryAnswerDao;

    static {
        inquiryAnswerDao = new InquiryAnswerDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private InquiryAnswerUtil() {
    }

    /**
     * Get the inquiry answer DAO
     *
     * @return the inquiry answer DAO
     */
    public static InquiryAnswerDao getInquiryAnswerDao() {
        return inquiryAnswerDao;
    }

    /**
     * Get one inquiry answer
     *
     * @param id id of the inquiry answer
     * @return the inquiry answer
     */
    public static InquiryAnswer fetchInquiryAnswerById(int id) {
        return inquiryAnswerDao.fetchOneById(id);
    }

    /**
     * Get the inquiry answer belonging to a certain inquiry details object
     *
     * @param inquiryDetailsId the id of the inquiry details object
     * @return the answer belonging to the inquiry details object
     */
    public static InquiryAnswer fetchInquiryAnswerByInquiryDetailsId(int inquiryDetailsId) {
        return inquiryAnswerDao.fetchOneByInquiryDetailsId(inquiryDetailsId);
    }

    /**
     * Insert a new inquiry answer into the database
     *
     * @param inquiryAnswer the inquiry answer to insert
     */
    public static void insertInquiryAnswer(InquiryAnswer inquiryAnswer) {
        inquiryAnswerDao.insert(inquiryAnswer);
    }
}
