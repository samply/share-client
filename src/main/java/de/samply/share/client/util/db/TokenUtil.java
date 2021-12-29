package de.samply.share.client.util.db;

import de.samply.share.client.model.db.tables.daos.TokenDao;
import de.samply.share.client.model.db.tables.pojos.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper Class for CRUD operations with token objects.
 */
public class TokenUtil {

  private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

  private static final TokenDao tokenDao;

  static {
    tokenDao = new TokenDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private TokenUtil() {
  }

  /**
   * Get the token DAO.
   *
   * @return the token DAO
   */
  public static TokenDao getTokenDao() {
    return tokenDao;
  }

  /**
   * Get one token.
   *
   * @param id id of the token
   * @return the token
   */
  public static Token fetchTokenById(int id) {
    return tokenDao.fetchOneById(id);
  }
}
