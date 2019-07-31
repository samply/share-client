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
import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.tables.daos.InquiryResultDao;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.records.InquiryResultRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper Class for CRUD operations with inquiry result objects
 */
public class InquiryResultUtil {

    private static final Logger logger = LogManager.getLogger(InquiryResultUtil.class);

    private static InquiryResultDao inquiryResultDao;

    static {
        inquiryResultDao = new InquiryResultDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private InquiryResultUtil() {
    }

    /**
     * Get the inquiry result DAO
     *
     * @return the inquiry result DAO
     */
    public static InquiryResultDao getInquiryResultDao() {
        return inquiryResultDao;
    }

    /**
     * Get a list of all inquiry results
     *
     * @return list of all inquiry results
     */
    public static List<InquiryResult> fetchInquiryResults() {
        return inquiryResultDao.findAll();
    }

    /**
     * Get a list of all inquiry results for certain inquiry details
     *
     * @param inquiryDetailsId the id of the inquiry details
     * @return the list of inquiry results
     */
    public static List<InquiryResult> fetchInquiryResultsForInquiryDetailsById(int inquiryDetailsId) {
        return inquiryResultDao.fetchByInquiryDetailsId(inquiryDetailsId);
    }

    /**
     * Get one inquiry result
     *
     * @param inquiryResultId id of the inquiry result
     * @return
     */
    public static InquiryResult fetchInquiryResultById(int inquiryResultId) {
        return inquiryResultDao.fetchOneById(inquiryResultId);
    }

    public static InquiryResult fetchLatestInquiryResultForInquiryCriteriaById(int inquiryCriteriaId){
        DSLContext dslContext = ResourceManager.getDSLContext();
        return dslContext
                .selectFrom(Tables.INQUIRY_RESULT)
                .where(Tables.INQUIRY_RESULT.INQUIRY_CRITERIA_ID.equal(inquiryCriteriaId))
                .and(Tables.INQUIRY_RESULT.EXECUTED_AT.equal(
                        dslContext
                                .select(DSL.max(Tables.INQUIRY_RESULT.EXECUTED_AT))
                                .from(Tables.INQUIRY_RESULT)
                                .where(Tables.INQUIRY_RESULT.INQUIRY_CRITERIA_ID.equal(inquiryCriteriaId))
                ))
                .fetchOneInto(InquiryResult.class);
    }

    /**
     * Get the latest inquiry result for certain inquiry details
     *
     * @param inquiryDetailsId the id of the inquiry details
     * @return the latest inquiry result belonging to the given inquiry details
     */
    public static InquiryResult fetchLatestInquiryResultForInquiryDetailsById(int inquiryDetailsId) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        return dslContext
                .selectFrom(Tables.INQUIRY_RESULT)
                .where(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(inquiryDetailsId))
                .and(Tables.INQUIRY_RESULT.EXECUTED_AT.equal(
                        dslContext
                                .select(DSL.max(Tables.INQUIRY_RESULT.EXECUTED_AT))
                                .from(Tables.INQUIRY_RESULT)
                                .where(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(inquiryDetailsId))
                ))
                .fetchOneInto(InquiryResult.class);
    }

    /**
     * Insert a new inquiry result into the database
     *
     * @param inquiryResult the new inquiry result to insert
     * @return the assigned database id of the newly inserted inquiry result
     */
    public static int insertInquiryResult(InquiryResult inquiryResult) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        InquiryResultRecord inquiryResultRecord = dslContext.newRecord(Tables.INQUIRY_RESULT, inquiryResult);
        inquiryResultRecord.store();
        inquiryResultRecord.refresh();
        return inquiryResultRecord.getId();
    }

    /**
     * Update an inquiry result in the database
     *
     * @param inquiryResult the inquiry result to update
     */
    public static void updateInquiryResult(InquiryResult inquiryResult) {
        inquiryResultDao.update(inquiryResult);
    }

    /**
     * Get a list of inquiries for a certain entity type, where no notifications have been sent yet
     *
     * @param entityType the requested entity type
     * @param includeEmpty if set to true, results with 0 matching data sets will be included
     * @return the list of inquiry results
     */
    public static List<InquiryResult> getInquiryResultsForNotification(EntityType entityType, boolean includeEmpty) {
        if (includeEmpty) {
            return getInquiryResultsForNotificationIncludeEmpty(entityType);
        } else {
            return getInquiryResultsForNotification(entityType);
        }
    }

    /**
     * Get a list of inquiries for a certain entity type, where no notifications have been sent yet
     *
     * Include empty results
     *
     * @param entityType the requested entity type
     * @return the list of inquiry results
     */
    public static List<InquiryResult> getInquiryResultsForNotificationIncludeEmpty(EntityType entityType) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        return dslContext
                .select(Tables.INQUIRY_RESULT.fields())
                .from(Tables.INQUIRY).join(Tables.INQUIRY_DETAILS).on(Tables.INQUIRY_DETAILS.INQUIRY_ID.equal(Tables.INQUIRY.ID))
                .join(Tables.INQUIRY_RESULT).on(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(Tables.INQUIRY_DETAILS.ID))
                .join(Tables.INQUIRY_REQUESTED_ENTITY).on(Tables.INQUIRY_REQUESTED_ENTITY.INQUIRY_ID.equal(Tables.INQUIRY.ID))
                .join(Tables.REQUESTED_ENTITY).on(Tables.INQUIRY_REQUESTED_ENTITY.REQUESTED_ENTITY_ID.equal(Tables.REQUESTED_ENTITY.ID))
                .where(Tables.INQUIRY_RESULT.SIZE.isNotNull()
                        .and(Tables.INQUIRY_RESULT.IS_ERROR.equal(Boolean.FALSE))
                        .and(Tables.INQUIRY_RESULT.NOTIFICATION_SENT.equal(Boolean.FALSE))
                        .and(Tables.REQUESTED_ENTITY.NAME.equal(entityType)))
                .fetchInto(InquiryResult.class);
    }

    /**
     * Get a list of inquiries for a certain entity type, where no notifications have been sent yet
     *
     * Do not include empty results
     *
     * @param entityType the requested entity type
     * @return the list of inquiry results
     */
    public static List<InquiryResult> getInquiryResultsForNotification(EntityType entityType) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        return dslContext
                .select(Tables.INQUIRY_RESULT.fields())
                .from(Tables.INQUIRY).join(Tables.INQUIRY_DETAILS).on(Tables.INQUIRY_DETAILS.INQUIRY_ID.equal(Tables.INQUIRY.ID))
                    .join(Tables.INQUIRY_RESULT).on(Tables.INQUIRY_RESULT.INQUIRY_DETAILS_ID.equal(Tables.INQUIRY_DETAILS.ID))
                    .join(Tables.INQUIRY_REQUESTED_ENTITY).on(Tables.INQUIRY_REQUESTED_ENTITY.INQUIRY_ID.equal(Tables.INQUIRY.ID))
                    .join(Tables.REQUESTED_ENTITY).on(Tables.INQUIRY_REQUESTED_ENTITY.REQUESTED_ENTITY_ID.equal(Tables.REQUESTED_ENTITY.ID))
                .where(Tables.INQUIRY_RESULT.SIZE.isNotNull()
                        .and(Tables.INQUIRY_RESULT.SIZE.greaterThan(0))
                        .and(Tables.INQUIRY_RESULT.IS_ERROR.equal(Boolean.FALSE))
                        .and(Tables.INQUIRY_RESULT.NOTIFICATION_SENT.equal(Boolean.FALSE))
                        .and(Tables.REQUESTED_ENTITY.NAME.equal(entityType)))
                .fetchInto(InquiryResult.class);
    }

    /**
     * Set the notification sent flag for a list of inquiryResults
     *
     * @param inquiryResults the inquiry results for which to set the flag
     */
    public static void setNotificationSentForInquiryResults(List<InquiryResult> inquiryResults) {
        List<Integer> ids = new ArrayList<>();
        for (InquiryResult inquiryResult : inquiryResults) {
            ids.add(inquiryResult.getId());
        }
        DSLContext dslContext = ResourceManager.getDSLContext();
        dslContext.update(Tables.INQUIRY_RESULT)
                .set(Tables.INQUIRY_RESULT.NOTIFICATION_SENT, true)
                .where(Tables.INQUIRY_RESULT.ID.in(ids))
                .execute();
    }
}
