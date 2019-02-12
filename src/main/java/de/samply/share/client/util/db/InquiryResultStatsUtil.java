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
import de.samply.share.client.model.db.tables.daos.InquiryResultStatsDao;
import de.samply.share.client.model.db.tables.pojos.InquiryResultStats;
import de.samply.share.client.model.db.tables.records.InquiryResultStatsRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Helper Class for CRUD operations with inquiry result stats objects
 */
public class InquiryResultStatsUtil {

    private static final Logger logger = LogManager.getLogger(InquiryResultStatsUtil.class);

    private static InquiryResultStatsDao inquiryResultStatsDao;

    static {
        inquiryResultStatsDao = new InquiryResultStatsDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private InquiryResultStatsUtil() {
    }

    /**
     * Get the inquiry result stats DAO
     *
     * @return the inquiry result stats DAO
     */
    public static InquiryResultStatsDao getInquiryResultStatsDao() {
        return inquiryResultStatsDao;
    }

    /**
     * Get a list of all inquiry result stats
     *
     * @return list of all inquiry result stats
     */
    public static List<InquiryResultStats> fetchInquiryResultStats() {
        return inquiryResultStatsDao.findAll();
    }

    /**
     * Get a list of all inquiry result stats for certain inquiry result
     *
     * @param inquiryResultId the id of the inquiry result
     * @return the list of inquiry result stats
     */
    public static InquiryResultStats getInquiryResultStatsForInquiryResultById(int inquiryResultId) {
        return inquiryResultStatsDao.fetchOneByInquiryResultId(inquiryResultId);
    }

    /**
     * Get one inquiry result stats object
     *
     * @param inquiryResultStatsId id of the inquiry result stats
     * @return
     */
    public static InquiryResultStats fetchInquiryResultStatsById(int inquiryResultStatsId) {
        return inquiryResultStatsDao.fetchOneById(inquiryResultStatsId);
    }

    /**
     * Insert new inquiry result stats into the database
     *
     * @param inquiryResultStats the new inquiry result stats to insert
     * @return the assigned database id of the newly inserted inquiry result stats
     */
    public static int insertInquiryResultStats(InquiryResultStats inquiryResultStats) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        InquiryResultStatsRecord inquiryResultStatsRecord = dslContext.newRecord(Tables.INQUIRY_RESULT_STATS, inquiryResultStats);
        inquiryResultStatsRecord.store();
        inquiryResultStatsRecord.refresh();
        return inquiryResultStatsRecord.getId();
    }

    /**
     * Update inquiry result stats in the database
     *
     * @param inquiryResultStats the inquiry result stats to update
     */
    public static void updateInquiryResultStats(InquiryResultStats inquiryResultStats) {
        inquiryResultStatsDao.update(inquiryResultStats);
    }
}
