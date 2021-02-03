package de.samply.share.client.util.db;

import de.samply.share.client.model.db.Tables;
import de.samply.share.client.model.db.enums.InquiryCriteriaStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.daos.InquiryCriteriaDao;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.model.db.tables.records.InquiryCriteriaRecord;
import java.util.List;
import java.util.Optional;
import org.apache.commons.codec.binary.StringUtils;
import org.jooq.DSLContext;

/**
 * Helper Class for CRUD operations with inquiry criteria objects.
 */
public class InquiryCriteriaUtil {

  private static final InquiryCriteriaDao inquiryCriteriaDao;

  static {
    inquiryCriteriaDao = new InquiryCriteriaDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private InquiryCriteriaUtil() {
  }
  
  /**
   * Gets inquiry criteria dao.
   *
   * @return the inquiry criteria dao
   */
  public static InquiryCriteriaDao getInquiryCriteriaDao() {
    return inquiryCriteriaDao;
  }
  
  /**
   * Fetch by id inquiry criteria.
   *
   * @param id the id
   * @return the inquiry criteria
   */
  public static InquiryCriteria fetchById(int id) {
    return inquiryCriteriaDao.fetchOneById(id);
  }
  
  /**
   * Fetch inquiry details by id inquiry criteria.
   *
   * @param id the id
   * @return the inquiry criteria
   */
  // TODO(CQL): Rename to fetchFirstInquiryCriteriaByDetailsId(int id)
  public static InquiryCriteria fetchInquiryDetailsById(int id) {
    return inquiryCriteriaDao.fetchOneById(id);
  }
  
  /**
   * Update inquiry criteria.
   *
   * @param inquiryCriteria the inquiry criteria
   */
  public static void updateInquiryCriteria(InquiryCriteria inquiryCriteria) {
    inquiryCriteriaDao.update(inquiryCriteria);
  }
  
  /**
   * Update inquiry criteria.
   *
   * @param inquiryCriteriaList the inquiry criteria list
   */
  public static void updateInquiryCriteria(List<InquiryCriteria> inquiryCriteriaList) {
    inquiryCriteriaDao.update(inquiryCriteriaList);
  }
  
  /**
   * Gets inquiry criteria for inquiry details.
   *
   * @param inquiryDetails the inquiry details
   * @return the inquiry criteria for inquiry details
   */
  public static List<InquiryCriteria> getInquiryCriteriaForInquiryDetails(
      InquiryDetails inquiryDetails) {
    return inquiryCriteriaDao.fetchByDetailsId(inquiryDetails.getId());
  }
  
  /**
   * Gets inquiry criteria by status.
   *
   * @param status the status
   * @return the inquiry criteria by status
   */
  public static List<InquiryCriteria> getInquiryCriteriaByStatus(InquiryCriteriaStatusType status) {
    return inquiryCriteriaDao.fetchByStatus(status);
  }
  
  /**
   * Insert the InquiryCriteria into the database.
   *
   * @param inquiryCriteria the InquiryCriteria
   * @return id of the record
   */
  public static int insertInquiryCriteria(InquiryCriteria inquiryCriteria) {
    DSLContext dslContext = ResourceManager.getDslContext();
    InquiryCriteriaRecord inquiryCriteriaRecord = dslContext
        .newRecord(Tables.INQUIRY_CRITERIA, inquiryCriteria);
    inquiryCriteriaRecord.store();
    inquiryCriteriaRecord.refresh();

    return inquiryCriteriaRecord.getId();
  }
  
  
  /**
   * Todo.
   *
   * @param inquiryDetails Todo.
   * @param languageType   Todo.
   * @return Todo. first criteria original
   */
  public static InquiryCriteria getFirstCriteriaOriginal(InquiryDetails inquiryDetails,
      QueryLanguageType languageType) {
    List<InquiryCriteria> inquiryCriteriaList = getInquiryCriteriaForInquiryDetails(inquiryDetails);
    Optional<InquiryCriteria> inquiryCriteria =
        inquiryCriteriaList.stream()
            .filter(inquiryCriteriaTemp -> inquiryCriteriaTemp.getQueryLanguage() == languageType)
            .findFirst();

    return inquiryCriteria.orElse(null);
  }
  
  /**
   * Todo.
   *
   * @param inquiryDetails Todo.
   * @param languageType   Todo.
   * @param entityType     the entity type
   * @return Todo. first criteria original
   */
  public static InquiryCriteria getFirstCriteriaOriginal(InquiryDetails inquiryDetails,
      QueryLanguageType languageType, String entityType) {
    List<InquiryCriteria> inquiryCriteriaList = getInquiryCriteriaForInquiryDetails(inquiryDetails);
    Optional<InquiryCriteria> inquiryCriteria =
        inquiryCriteriaList.stream()
            .filter(inquiryCriteriaTemp -> inquiryCriteriaTemp.getQueryLanguage() == languageType)
            .filter(inquiryCriteriaTemp -> StringUtils
                .equals(inquiryCriteriaTemp.getEntityType(), entityType))
            .findFirst();

    return inquiryCriteria.orElse(null);
  }

}
