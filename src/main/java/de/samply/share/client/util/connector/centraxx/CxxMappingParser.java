package de.samply.share.client.util.connector.centraxx;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CxxMappingParser {

  private static final Logger logger = LogManager.getLogger(CxxMappingParser.class);

  /**
   * Analyzes mapping between mdr data elements and centraxx data elements returned
   * by the REST-API of CentraXX for the connector.
   *
   * @param httpEntity http entity with the mapping returned by Centraxx.
   * @return List of centraxx mapping elements.
   */
  public List<CxxMappingElement> parse(String httpEntity) {

    try {
      return parseWithoutExceptionManagement(httpEntity);
    } catch (Exception e) {
      logger.info(e);
      return new ArrayList<>();
    }
  }

  private List<CxxMappingElement> parse(JsonArray jsonArray) {

    List<CxxMappingElement> cxxMappingElementList = new ArrayList<>();

    if (jsonArray != null) {

      PercentageLogger percentageLogger = new PercentageLogger(logger, jsonArray.size(),
          "analyzing mdr mapping...");
      for (JsonElement jsonElement : jsonArray) {

        CxxMappingElement cxxMappingElement = getCxxMappingElement(jsonElement);
        if (cxxMappingElement != null) {
          logger.debug(cxxMappingElement.getMdrId());
          cxxMappingElementList.add(cxxMappingElement);
        }

        percentageLogger.incrementCounter();

      }

    }

    return cxxMappingElementList;

  }

  private List<CxxMappingElement> parseWithoutExceptionManagement(String httpEntity) {

    JsonParser jsonParser = new JsonParser();
    JsonArray jsonArray = jsonParser.parse(httpEntity).getAsJsonArray();

    return parse(jsonArray);

  }


  private CxxMappingElement getCxxMappingElement(JsonElement jsonElement) {

    JsonObject jsonObject = jsonElement.getAsJsonObject();
    CxxMappingElement cxxMappingElement = new CxxMappingElement();

    String cxxName = getStringOfJsonObject(jsonObject, CxxConstants.CXX_NAME);
    String teilerBaseViewColumn = getStringOfJsonObject(jsonObject,
        CxxConstants.TEILER_BASE_VIEW_COLUMN);
    MdrIdDatatype mdrId = getMdrId(jsonObject);

    cxxMappingElement.setMdrName(cxxName);
    cxxMappingElement.setTeilerBaseViewColumn(teilerBaseViewColumn);
    cxxMappingElement.setMdrId(mdrId);

    cxxMappingElement = addMdrRepresentations(cxxMappingElement, jsonObject);

    return cxxMappingElement;

  }


  private CxxMappingElement addMdrRepresentations(CxxMappingElement cxxMappingElement,
      JsonObject jsonObject) {

    try {

      return addMdrRepresentations_WithoutManagementException(cxxMappingElement, jsonObject);

    } catch (Exception e) {

      logger.debug(e);
      return cxxMappingElement;

    }

  }

  private CxxMappingElement addMdrRepresentations_WithoutManagementException(
      CxxMappingElement cxxMappingElement, JsonObject jsonObject) {

    JsonElement jsonElementMdrRepresentations = jsonObject.get(CxxConstants.MDR_REPRESENTATIONS);

    if (jsonElementMdrRepresentations != null) {

      JsonArray jsonArrayMdrRepresentations = jsonElementMdrRepresentations.getAsJsonArray();

      if (jsonArrayMdrRepresentations != null) {

        for (JsonElement jsonElementMdrRepresentation : jsonArrayMdrRepresentations) {

          JsonObject jsonObjectMdrRepresentation = jsonElementMdrRepresentation.getAsJsonObject();
          String mdrValue = getStringOfJsonObject(jsonObjectMdrRepresentation,
              CxxConstants.MDR_PERMITTED_VALUE);

          JsonElement jsonElementCxxRepresentations = jsonObjectMdrRepresentation
              .get(CxxConstants.CXX_REPRESENTATIONS);

          cxxMappingElement = addJsonElementCxxRepresentations(cxxMappingElement, mdrValue,
              jsonElementCxxRepresentations);

        }
      }
    }

    return cxxMappingElement;
  }


  private CxxMappingElement addJsonElementCxxRepresentations(CxxMappingElement cxxMappingElement,
      String mdrValue, JsonElement jsonElementCxxRepresentations) {

    try {

      return addJsonElementCxxRepresentations_WithoutManagementException(cxxMappingElement,
          mdrValue, jsonElementCxxRepresentations);

    } catch (Exception e) {

      logger.debug(e);
      return cxxMappingElement;

    }

  }

  private CxxMappingElement addJsonElementCxxRepresentations_WithoutManagementException(
      CxxMappingElement cxxMappingElement, String mdrValue,
      JsonElement jsonElementCxxRepresentations) {

    boolean hasCxxValues = false;

    if (jsonElementCxxRepresentations != null) {

      JsonArray jsonArrayCxxRepresentations = jsonElementCxxRepresentations.getAsJsonArray();

      if (jsonArrayCxxRepresentations != null) {

        for (JsonElement jsonElementCxxRepresentation : jsonArrayCxxRepresentations) {

          JsonObject jsonObjectCxxRepresentation = jsonElementCxxRepresentation.getAsJsonObject();
          String cxxValue = getStringOfJsonObject(jsonObjectCxxRepresentation,
              CxxConstants.CXX_VALUE_NAME);

          cxxMappingElement.addValue(mdrValue, cxxValue);
          hasCxxValues = true;

        }

      }

    }

    if (!hasCxxValues) {
      logger.debug(mdrValue + " without CentraXX value");
    }

    return cxxMappingElement;

  }


  private MdrIdDatatype getMdrId(JsonObject jsonObject) {

    String urnNamespace = getStringOfJsonObject(jsonObject, CxxConstants.URN_NAMESPACE);
    String urnElementId = getStringOfJsonObject(jsonObject, CxxConstants.URN_ELEMENT_ID);
    String urnRevision = getStringOfJsonObject(jsonObject, CxxConstants.URN_REVISION);

    MdrIdDatatype mdrId = null;

    if (urnNamespace != null && urnElementId != null) {

      mdrId = new MdrIdDatatype(urnNamespace + ":" + urnElementId);

      if (urnRevision != null) {
        mdrId.setVersion(urnRevision);
      }

    }

    return mdrId;

  }

  private String getStringOfJsonObject(JsonObject jsonObject, String jsonObjectName) {

    JsonElement jsonElement = jsonObject.get(jsonObjectName);
    return (jsonElement != null) ? jsonElement.getAsString() : null;

  }

}
