package de.samply.share.client.util.connector;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.IdManagerInfo;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

/**
 * A connector that handles all communication with the ID Manager.
 */
public class IdManagerConnector {

  private static final Logger logger = LogManager.getLogger(IdManagerConnector.class);

  private static final String EXPORT_ID_PATH = "getExportIds";
  private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
  private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
  private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10000;
  private final int maxNumberOfConnectionAttempts;
  private final int timeToWaitInSecondsBetweenConnectionAttempts;
  private final transient HttpConnector httpConnector;
  private final HttpHost httpHost;
  private final CloseableHttpClient httpClient;
  private final URL idManagerUrl;
  private final RequestConfig requestConfig;

  /**
   * Todo.
   */
  public IdManagerConnector() {
    try {
      idManagerUrl = SamplyShareUtils.stringToUrl(
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.ID_MANAGER_URL));
      httpConnector = ApplicationBean.createHttpConnector();

      int socketTimout = getSocketTimeout();
      int connectTimeout = getConnectTimeout();
      int connectionRequestTimeout = getConnectionRequestTimeout();
      maxNumberOfConnectionAttempts = getMaxNumberOfConnectionAttempts();
      timeToWaitInSecondsBetweenConnectionAttempts =
          getTimeToWaitInSecondsBetweenConnectionAttempts();

      requestConfig = RequestConfig.custom().setSocketTimeout(socketTimout)
          .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout)
          .build();
      httpHost = SamplyShareUtils.getAsHttpHost(idManagerUrl);
      httpClient = httpConnector.getHttpClient(httpHost);

    } catch (MalformedURLException e) {
      logger.error("Could not initialize IdManagerConnector");
      throw new RuntimeException(e);
    }

  }

  private int getSocketTimeout() {
    return getConfigValue(EnumConfiguration.ID_CONNECTOR_SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
  }

  private int getConnectTimeout() {
    return getConfigValue(EnumConfiguration.ID_CONNECTOR_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
  }

  private int getConnectionRequestTimeout() {
    return getConfigValue(EnumConfiguration.ID_CONNECTOR_CONNECTION_REQUEST_TIMEOUT,
        DEFAULT_CONNECTION_REQUEST_TIMEOUT);
  }

  private int getMaxNumberOfConnectionAttempts() {
    return getConfigValue(EnumConfiguration.ID_CONNECTOR_MAX_NUMBER_OF_CONNECTION_ATTEMPTS,
        maxNumberOfConnectionAttempts);
  }

  private int getTimeToWaitInSecondsBetweenConnectionAttempts() {
    return getConfigValue(
        EnumConfiguration.ID_CONNECTOR_TIME_TO_WAIT_IN_SECONDS_BETWEEN_CONNECTION_ATTEMPTS,
        timeToWaitInSecondsBetweenConnectionAttempts);
  }

  private int getConfigValue(EnumConfiguration enumConfiguration, int defaultConfiguration) {

    try {
      return Integer.parseInt(ConfigurationUtil.getConfigurationElementValue(enumConfiguration));
    } catch (Exception e) {
      return defaultConfiguration;
    }

  }

  /**
   * Get export ids for a collection of id objects.
   *
   * @param idObjectMap a map that holds local ids and id objects
   * @return a map with local ids and their export ids
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


  private void sleepBetweenConnectionAttempts() {

    try {
      TimeUnit.SECONDS.sleep(timeToWaitInSecondsBetweenConnectionAttempts);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  private HashMap<String, String> getExportIds_WithoutExceptionManagement(
      LinkedList<IdObject> idList, HashMap<String, IdObject> idObjectMap)
      throws IdManagerConnectorException, IOException, JSONException {

    HashMap<String, String> idMap = new HashMap<>();

    HttpPost httpPost = new HttpPost(
        SamplyShareUtils.addTrailingSlash(idManagerUrl.getPath()) + EXPORT_ID_PATH);
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


  /**
   * Get version information.
   *
   * @return a name/version combo of the id manager
   */
  public String getUserAgentInfo() throws IdManagerConnectorException {
    IdManagerInfo idManagerInfo = getIdManagerInfo();
    return idManagerInfo.getDist() + "/" + idManagerInfo.getVersion();
  }

  /**
   * Query the info resource from the id manager in order to obtain version information.
   *
   * @return Name and version information from the id manager
   */
  private IdManagerInfo getIdManagerInfo() throws IdManagerConnectorException {
    ResponseHandler<IdManagerInfo> responseHandler = new ResponseHandler<IdManagerInfo>() {

      @Override
      public IdManagerInfo handleResponse(final HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
          throw new HttpResponseException(
              statusLine.getStatusCode(),
              statusLine.getReasonPhrase());
        }
        if (entity == null) {
          throw new ClientProtocolException("Response contains no content");
        }
        try (InputStream instream = entity.getContent()) {
          Reader reader = new InputStreamReader(instream, Consts.UTF_8);
          return new Gson().fromJson(reader, IdManagerInfo.class);
        } catch (JsonSyntaxException | JsonIOException e) {
          throw new IOException(
              "JSON Exception caught while trying to unmarshal IdManager info...");
        }
      }
    };

    try {
      HttpGet httpGet = new HttpGet(idManagerUrl.toURI());
      httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
      return httpClient.execute(httpGet, responseHandler);
    } catch (IOException | URISyntaxException e) {
      throw new IdManagerConnectorException(e);
    }
  }

  /**
   * Check the reachability of the ID Manager.
   *
   * @return a check result object with the outcome of the connection check
   */
  public CheckResult checkConnection() {
    CheckResult result = new CheckResult();
    result.setExecutionDate(new Date());

    try {
      HttpGet httpGet = new HttpGet(idManagerUrl.toURI());
      httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
      result.getMessages()
          .add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));
      CloseableHttpResponse response = httpClient.execute(httpHost, httpGet);
      HttpEntity entity = response.getEntity();
      EntityUtils.consume(entity);
      result.getMessages()
          .add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode >= 200 && statusCode < 400) {
        result.setSuccess(true);
      } else {
        result.setSuccess(false);
        result.getMessages().add(new Message(EntityUtils.toString(entity), "fa-bolt"));
      }
    } catch (URISyntaxException | IOException e) {
      result.setSuccess(false);
      result.getMessages().add(new Message(e.getMessage(), "fa-bolt"));
    }

    return result;
  }
}
