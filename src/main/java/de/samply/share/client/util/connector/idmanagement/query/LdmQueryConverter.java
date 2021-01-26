package de.samply.share.client.util.connector.idmanagement.query;

import de.samply.share.client.util.connector.idmanagement.converter.LdmSerializableConverters;
import de.samply.share.client.util.connector.idmanagement.converter.SerializableConverter;
import de.samply.share.client.util.connector.idmanagement.converter.SerializableConverterException;
import de.samply.share.client.util.connector.idmanagement.utils.TermFactory;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.common.ConditionType;
import de.samply.share.model.common.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LdmQueryConverter {

  private final Map<LdmId, LdmSerializableConverters> ldmIdLdmSerializableConverters =
      new HashMap<>();

  /**
   * Todo David.
   * @param ldmId Todo David
   * @param ldmSerializableConverters Todo David
   */
  public void addLdmSerializableConverters(LdmId ldmId,
      LdmSerializableConverters ldmSerializableConverters) {
    ldmIdLdmSerializableConverters.put(ldmId, ldmSerializableConverters);
  }

  /**
   * Todo David.
   * @param ldmId Todo David
   * @param query Todo David
   * @return Todo David
   * @throws LdmQueryConverterException LdmQueryConverterException
   */
  public Query convertQuery(LdmId ldmId, Query query) throws LdmQueryConverterException {

    for (LdmId tempLdmId : ldmIdLdmSerializableConverters.keySet()) {

      if (!ldmId.equals(tempLdmId)) {
        parse(tempLdmId, query);

      }

    }

    return query;

  }

  private void parse(LdmId ldmId, Query query) throws LdmQueryConverterException {
    try {
      parse_WithoutManagementException(ldmId, query);
    } catch (QueryParserException e) {
      throw new LdmQueryConverterException(e);
    }
  }

  private void parse_WithoutManagementException(LdmId ldmId, Query query)
      throws QueryParserException {

    QueryConverter queryConverter = new QueryConverter(ldmId);
    queryConverter.parse(query);

  }

  private class QueryConverter extends QueryParser {

    private final LdmId ldmId;

    public QueryConverter(LdmId ldmId) {
      this.ldmId = ldmId;
    }

    @Override
    protected Serializable parseTerm(Serializable serializable) throws QueryParserException {

      String mdrKey = TermFactory.getMdrKey(serializable);

      if (mdrKey != null) {

        MdrIdDatatype mdrId = new MdrIdDatatype(mdrKey);

        SerializableConverter serializableConverter = getSerializableConverter(ldmId, mdrId);

        if (serializableConverter != null) {
          serializable = convert(serializableConverter, serializable);
        }

      }

      return serializable;

    }

    private Serializable convert(SerializableConverter serializableConverter,
        Serializable serializable) throws QueryParserException {
      try {
        return serializableConverter.convert(serializable);
      } catch (SerializableConverterException e) {
        throw new QueryParserException(e);
      }
    }

    private SerializableConverter getSerializableConverter(LdmId ldmId, MdrIdDatatype mdrId) {

      SerializableConverter serializableConverter = null;

      LdmSerializableConverters ldmSerializableConverters = ldmIdLdmSerializableConverters
          .get(ldmId);
      if (ldmSerializableConverters != null) {
        serializableConverter = ldmSerializableConverters.getSerializableConverter(mdrId);
      }

      return serializableConverter;

    }

    @Override
    protected void parse(Serializable serializable, Serializable serializableParent)
        throws QueryParserException {
      super.parse(serializable, serializableParent);

      if (serializable instanceof ConditionType) {

        ConditionType conditionType = (ConditionType) serializable;

        if (conditionType.getAndOrEqOrLike().size() == 0 && serializableParent != null
            && serializableParent instanceof ConditionType) {

          ConditionType conditionTypeParent = (ConditionType) serializableParent;
          List<Serializable> andOrEqOrLike = conditionTypeParent.getAndOrEqOrLike();
          andOrEqOrLike.remove(conditionType);

        }

      }
    }
  }


}
