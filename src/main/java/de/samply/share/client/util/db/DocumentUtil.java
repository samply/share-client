package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.tables.daos.DocumentDao;
import de.samply.share.client.model.db.tables.pojos.Document;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.model.db.tables.records.DocumentRecord;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with document objects.
 */
public class DocumentUtil {

  private static final Logger logger = LogManager.getLogger(DocumentUtil.class);

  private static final DocumentDao documentDao;

  static {
    documentDao = new DocumentDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private DocumentUtil() {
  }

  /**
   * Get the document DAO.
   *
   * @return the document DAO
   */
  public static DocumentDao getDocumentDao() {
    return documentDao;
  }

  /**
   * Get one document.
   *
   * @param id id of the document
   * @return the document
   */
  public static Document fetchDocumentById(int id) {
    return documentDao.fetchOneById(id);
  }

  /**
   * Update a document in the database.
   *
   * @param document the document to update
   */
  public static void updateDocument(Document document) {
    documentDao.update(document);
  }

  /**
   * Update a list of documents in the database.
   *
   * @param documentList the list of documents to update
   */
  public static void updateDocuments(List<Document> documentList) {
    documentDao.update(documentList);
  }

  /**
   * Get all documents for one inquiry.
   *
   * @param inquiry the inquiry for which the documents are wanted
   * @return the list of documents associated with the given inquiry
   */
  public static List<Document> getDocumentsForInquiry(Inquiry inquiry) {
    return getDocumentsForInquiry(inquiry.getId());
  }

  /**
   * Get all documents for one inquiry by its id.
   *
   * @param inquiryId the id of the inquiry for which the documents are wanted
   * @return the list of documents associated with the given inquiry
   */
  public static List<Document> getDocumentsForInquiry(int inquiryId) {
    return documentDao.fetchByInquiryId(inquiryId);
  }

  /**
   * Get all documents for one user.
   *
   * @param user the user whose documents are wanted
   * @return the list of documents from the user
   */
  public static List<Document> getDocumentsForUser(User user) {
    return getDocumentsForUser(user.getId());
  }

  /**
   * Get all documents for one user by his/her id.
   *
   * @param userId the id of the user whose documents are wanted
   * @return the list of documents from the user
   */
  public static List<Document> getDocumentsForUser(int userId) {
    return documentDao.fetchByUserId(userId);
  }

  /**
   * Insert a new document into the database.
   *
   * @param document the new document to insert
   * @return the assigned database id of the newly inserted document
   */
  public static int insertDocument(Document document) {
    DSLContext dslContext = ResourceManager.getDslContext();
    DocumentRecord documentRecord = dslContext.newRecord(Tables.DOCUMENT, document);
    documentRecord.store();
    documentRecord.refresh();
    return documentRecord.getId();
  }

  /**
   * Read a document into a bytestream.
   *
   * @param documentId the id of the document to read
   * @return the byte array stream of the document
   */
  public static ByteArrayOutputStream getDocumentOutputStreamById(int documentId) {
    Document document = documentDao.fetchOneById(documentId);

    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(document.getData());
      bos.close();
      return bos;
    } catch (IOException e) {
      logger.error("IO Exception while trying to read a document", e);
      return null;
    }
  }

  /**
   * Delete a document from the database.
   *
   * @param documentId the id of the document to delete
   */
  public static void deleteDocument(int documentId) {
    documentDao.deleteById(documentId);
  }

}
