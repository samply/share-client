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
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.daos.UploadDao;
import de.samply.share.client.model.db.tables.pojos.Upload;
import de.samply.share.client.model.db.tables.records.UploadRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Helper Class for CRUD operations with upload objects
 */
public class UploadUtil {
    
    private static final Logger logger = LogManager.getLogger(UploadUtil.class);

    private static UploadDao uploadDao;

    static {
        uploadDao = new UploadDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private UploadUtil() {

    }

    /**
     * Get the upload DAO
     *
     * @return the upload DAO
     */
    public static UploadDao getUploadDao() {
        return uploadDao;
    }

    /**
     * Insert a new upload into the database
     *
     * @param upload the new upload to insert
     * @return the assigned database id of the newly inserted upload
     */
    public static int insertUpload(Upload upload) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        UploadRecord uploadRecord = dslContext.newRecord(Tables.UPLOAD, upload);
        uploadRecord.store();
        uploadRecord.refresh();
        return uploadRecord.getId();
    }

    /**
     * Update an upload in the database
     *
     * @param upload the upload to update
     */
    public static void updateUpload(Upload upload) {
        uploadDao.update(upload);
    }

    /**
     * Delete an upload from the database
     *
     * @param upload the upload to delete
     */
    public static void deleteUpload(Upload upload) {
        uploadDao.delete(upload);
    }

    /**
     * Get one upload
     *
     * @param id id of the upload
     * @return the upload
     */
    public static Upload fetchUploadById(int id) {
        return uploadDao.fetchOneById(id);
    }

    /**
     * Get a list of all uploads
     *
     * @return list of all uploads
     */
    public static List<Upload> fetchUploads() {
        return uploadDao.findAll();
    }

    /**
     * Set the status of an upload
     *
     * @param uploadId id of the upload
     * @param status the new status to set for the upload
     */
    public static void setUploadStatusById(int uploadId, UploadStatusType status) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        dslContext.update(Tables.UPLOAD)
                .set(Tables.UPLOAD.STATUS, status)
                .where(Tables.UPLOAD.ID.equal(uploadId))
                .execute();
    }

    /**
     * Get the currently active upload if any is present
     *
     * @return the running upload or null if none is running at the moment
     */
    public static Upload getActiveUpload() {
        DSLContext dslContext = ResourceManager.getDSLContext();
        return dslContext.select()
                .from(Tables.UPLOAD)
                .where(Tables.UPLOAD.STATUS.in(
                        UploadStatusType.US_NEW,
                        UploadStatusType.US_QUERY_POSTED,
                        UploadStatusType.US_QUERY_READY,
                        UploadStatusType.US_UPLOADING)).orderBy(de.samply.share.client.model.db.tables.Upload.UPLOAD.TRIGGERED_BY.desc()).limit(1)
                .fetchOneInto(Upload.class);
    }
}
