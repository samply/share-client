package de.samply.share.client.util.connector;

import com.google.gson.Gson;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

/**
 * A connector that handles all communication with the ID Manager.
 */
public class IdManagerBasicInfoConnector extends AbstractComponentBasicInfoConnector {

  private static final Logger logger = LogManager.getLogger(IdManagerBasicInfoConnector.class);

  private static final String EXPORT_ID_PATH = "getExportIds";
  
  /**
   * Creates an Id Manager Basic Info Connector.
   */
  public IdManagerBasicInfoConnector() {

    super(EnumConfiguration.ID_MANAGER_URL);
  }
  
  
  /**
   * Get export ids for a collection of id objects.
   *
   * @param idObjectMap a map that holds local ids and id objects
   * @return a map with local ids and their export ids
   * @throws IdManagerConnectorException the id manager connector exception
   */
  public HashMap<String, String> getExportIds(HashMap<String, IdObject> idObjectMap)
      throws IdManagerConnectorException {

    LinkedList<IdObject> idList = new LinkedList<>();

    idList.addAll(idObjectMap.values());

    if (SamplyShareUtils.isNullOrEmpty(idList)) {
      throw new IdManagerConnectorException("id list is empty");
    }
    return getExportIdsWithMaxNumberOfConnectionAttempts(idList, idObjectMap);
  }

  private HashMap<String, String> getExportIds(LinkedList<IdObject> idList,
      HashMap<String, IdObject> idObjectMap, int numberOfConnectionAttempt)
      throws IdManagerConnectorException {
    try {
      return getExportIds(idList, idObjectMap);
    } catch (IdManagerConnectorException e) {
      if (numberOfConnectionAttempt < maxNumberOfConnectionAttempts) {
        e.printStackTrace();
        sleepBetweenConnectionAttempts();
        logger.debug("Getting export ids: Attempt " + numberOfConnectionAttempt);
        return getExportIds(idList, idObjectMap, numberOfConnectionAttempt + 1);
      } else {
        throw e;
      }
    }
  }

  private HashMap<String, String> getExportIds(LinkedList<IdObject> idList,
      HashMap<String, IdObject> idObjectMap) throws IdManagerConnectorException {

    try {
      return getExportIds_WithoutExceptionManagement(idList, idObjectMap);
    } catch (IOException | JSONException e) {
      throw new IdManagerConnectorException(e);
    }

  }

  private HashMap<String, String> getExportIdsWithMaxNumberOfConnectionAttempts(
      LinkedList<IdObject> idList, HashMap<String, IdObject> idObjectMap)
      throws IdManagerConnectorException {
    return getExportIds(idList, idObjectMap, 0);
  }

  private HashMap<String, String> getExportIds_WithoutExceptionManagement(
      LinkedList<IdObject> idList, HashMap<String, IdObject> idObjectMap)
      throws IdManagerConnectorException, IOException, JSONException {

    HashMap<String, String> idMap = new HashMap<>();

    HttpPost httpPost = new HttpPost(
        SamplyShareUtils.addTrailingSlash(url.getPath()) + EXPORT_ID_PATH);
    httpPost.setConfig(requestConfig);
    httpPost.setHeader("Accept", MediaType.APPLICATION_JSON);
    httpPost.setHeader("Content-type", MediaType.APPLICATION_JSON);

    httpPost.setEntity(new StringEntity((new Gson()).toJson(idList), ContentType.APPLICATION_JSON));
    CloseableHttpResponse response = httpClient.execute(httpHost, httpPost);

    int statusCode = response.getStatusLine().getStatusCode();
    HttpEntity entity = response.getEntity();

    if (statusCode >= 400) {
      throw new IdManagerConnectorException(
          "Got an error from ID Management: " + response.getStatusLine());
    }

    String jsonString = EntityUtils.toString(entity, Consts.UTF_8);
    logger.debug(jsonString);
    JSONArray jsonArray = new JSONArray(jsonString);
    List<String> exportIds = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      exportIds.add(jsonArray.getString(i));
    }

    Iterator<Map.Entry<String, IdObject>> localIdIterator = idObjectMap.entrySet().iterator();

    for (String thisExportId : exportIds) {
      Map.Entry<String, IdObject> next = localIdIterator.next();
      idMap.put(next.getKey(), thisExportId);
    }

    return idMap;
  }

  private void sleepBetweenConnectionAttempts() {
    try {
      TimeUnit.SECONDS.sleep(timeToWaitInSecondsBetweenConnectionAttempts);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
