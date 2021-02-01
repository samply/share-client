package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.daos.UploadDao;
import de.samply.share.client.model.db.tables.pojos.Upload;
import de.samply.share.client.model.db.tables.records.UploadRecord;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with upload objects.
 */
public class UploadUtil {

  private static final Logger logger = LogManager.getLogger(UploadUtil.class);

  private static final UploadDao uploadDao;

  static {
    uploadDao = new UploadDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private UploadUtil() {

  }

  /**
   * Get the upload DAO.
   *
   * @return the upload DAO
   */
  public static UploadDao getUploadDao() {
    return uploadDao;
  }

  /**
   * Insert a new upload into the database.
   *
   * @param upload the new upload to insert
   * @return the assigned database id of the newly inserted upload
   */
  public static int insertUpload(Upload upload) {
    DSLContext dslContext = ResourceManager.getDslContext();
    UploadRecord uploadRecord = dslContext.newRecord(Tables.UPLOAD, upload);
    uploadRecord.store();
    uploadRecord.refresh();
    return uploadRecord.getId();
  }

  /**
   * Update an upload in the database.
   *
   * @param upload the upload to update
   */
  public static void updateUpload(Upload upload) {
    uploadDao.update(upload);
  }

  /**
   * Delete an upload from the database.
   *
   * @param upload the upload to delete
   */
  public static void deleteUpload(Upload upload) {
    uploadDao.delete(upload);
  }

  /**
   * Get one upload.
   *
   * @param id id of the upload
   * @return the upload
   */
  public static Upload fetchUploadById(int id) {
    return uploadDao.fetchOneById(id);
  }

  /**
   * Get a list of all uploads.
   *
   * @return list of all uploads
   */
  public static List<Upload> fetchUploads() {
    return uploadDao.findAll();
  }

  /**
   * Set the status of an upload.
   *
   * @param uploadId id of the upload
   * @param status   the new status to set for the upload
   */
  public static void setUploadStatusById(int uploadId, UploadStatusType status) {
    DSLContext dslContext = ResourceManager.getDslContext();
    dslContext.update(Tables.UPLOAD)
        .set(Tables.UPLOAD.STATUS, status)
        .where(Tables.UPLOAD.ID.equal(uploadId))
        .execute();
  }

  /**
   * Get the currently active upload if any is present.
   *
   * @return the running upload or null if none is running at the moment
   */
  public static Upload getActiveUpload() {
    DSLContext dslContext = ResourceManager.getDslContext();
    return dslContext.select()
        .from(Tables.UPLOAD)
        .where(Tables.UPLOAD.STATUS.in(
            UploadStatusType.US_NEW,
            UploadStatusType.US_QUERY_POSTED,
            UploadStatusType.US_QUERY_READY,
            UploadStatusType.US_UPLOADING))
        .orderBy(de.samply.share.client.model.db.tables.Upload.UPLOAD.TRIGGERED_BY.desc()).limit(1)
        .fetchOneInto(Upload.class);
  }
}
