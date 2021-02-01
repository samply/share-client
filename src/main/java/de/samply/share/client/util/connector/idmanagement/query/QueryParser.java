package de.samply.share.client.util.connector.idmanagement.query;

import de.samply.share.model.common.ConditionType;
import de.samply.share.model.common.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class QueryParser {

  protected abstract Serializable parseTerm(Serializable term) throws QueryParserException;

  private void parseTerm(Serializable term, List<Serializable> termParent)
      throws QueryParserException {
    Serializable term2 = parseTerm(term);
    if (term != term2) {
      termParent.remove(term);
      if (term2 != null) {
        termParent.add(term2);
      }
    }
  }


  /**
   * Todo David.
   * @param query Todo David
   * @throws QueryParserException QueryParserException
   */
  public void parse(Query query) throws QueryParserException {
    if (query != null) {
      parse(query.getWhere(), null);
    }
  }

  protected void parse(Serializable serializable, Serializable serializableParent)
      throws QueryParserException {

    if (serializable instanceof ConditionType) {

      ConditionType conditionType = (ConditionType) serializable;
      List<Serializable> serializableChilds = new ArrayList<>(conditionType.getAndOrEqOrLike());

      for (Serializable serializableChild : serializableChilds) {

        if (serializableChild instanceof ConditionType) {
          parse(serializableChild, serializable);
        } else {
          parseTerm(serializableChild, conditionType.getAndOrEqOrLike());
        }

      }

    }
  }
}
