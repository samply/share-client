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
import de.samply.share.client.model.db.tables.daos.DocumentDao;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.Document;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.model.db.tables.records.DocumentRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Helper Class for CRUD operations with document objects
 */
public class DocumentUtil {

    private static final Logger logger = LogManager.getLogger(DocumentUtil.class);

    private static DocumentDao documentDao;

    static {
        documentDao = new DocumentDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private DocumentUtil() {
    }

    /**
     * Get the document DAO
     *
     * @return the document DAO
     */
    public static DocumentDao getDocumentDao() {
        return documentDao;
    }

    /**
     * Get one document
     *
     * @param id id of the document
     * @return the document
     */
    public static Document fetchDocumentById(int id) {
        return documentDao.fetchOneById(id);
    }

    /**
     * Update a document in the database
     *
     * @param document the document to update
     */
    public static void updateDocument(Document document) {
        documentDao.update(document);
    }

    /**
     * Update a list of documents in the database
     *
     * @param documentList the list of documents to update
     */
    public static void updateDocuments(List<Document> documentList) {
        documentDao.update(documentList);
    }

    /**
     * Get all documents for one inquiry
     *
     * @param inquiry the inquiry for which the documents are wanted
     * @return the list of documents associated with the given inquiry
     */
    public static List<Document> getDocumentsForInquiry(Inquiry inquiry) {
        return getDocumentsForInquiry(inquiry.getId());
    }

    /**
     * Get all documents for one inquiry by its id
     *
     * @param inquiryId the id of the inquiry for which the documents are wanted
     * @return the list of documents associated with the given inquiry
     */
    public static List<Document> getDocumentsForInquiry(int inquiryId) {
        return documentDao.fetchByInquiryId(inquiryId);
    }

    /**
     * Get all documents for one user
     *
     * @param user the user whose documents are wanted
     * @return the list of documents from the user
     */
    public static List<Document> getDocumentsForUser(User user) {
        return getDocumentsForUser(user.getId());
    }

    /**
     * Get all documents for one user by his/her id
     *
     * @param userId the id of the user whose documents are wanted
     * @return the list of documents from the user
     */
    public static List<Document> getDocumentsForUser(int userId) {
        return documentDao.fetchByUserId(userId);
    }

    /**
     * Insert a new document into the database
     *
     * @param document the new document to insert
     * @return the assigned database id of the newly inserted document
     */
    public static int insertDocument(Document document) {
        DSLContext dslContext = ResourceManager.getDSLContext();
        DocumentRecord documentRecord = dslContext.newRecord(Tables.DOCUMENT, document);
        documentRecord.store();
        documentRecord.refresh();
        return documentRecord.getId();
    }

    /**
     * Read a document into a bytestream
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
            logger.error("IO Exception while trying to read a document" ,e);
            return null;
        }
    }

    /**
     * Delete a document from the database
     *
     * @param documentId the id of the document to delete
     */
    public static void deleteDocument(int documentId) {
        documentDao.deleteById(documentId);
    }

}
