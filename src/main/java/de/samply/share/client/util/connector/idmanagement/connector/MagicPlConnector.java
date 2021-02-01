package de.samply.share.client.util.connector.idmanagement.connector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.idmanagement.utils.IdManagementUtils;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MagicPlConnector implements IdManagementConnector {

  private static final Logger logger = LogManager.getLogger(MagicPlConnector.class);
  private static final String URL_PREFIX_READ_PATIENTS = "paths/readPatients";
  private static final String HTTP_HEADERS_API_KEY = "apiKey";

  private final String magicPlBaseUrl;
  private final String magicPlApiKey;

  private final CloseableHttpClient httpClient;
  private final Gson gson = new Gson();

  /**
   * Todo David.
   */
  public MagicPlConnector() {
    this.magicPlBaseUrl = SamplyShareUtils
        .addTrailingSlash(
            ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.ID_MANAGER_URL));
    this.magicPlApiKey = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_API_KEY);
    try {
      HttpHost httpHost = SamplyShareUtils.getAsHttpHost(magicPlBaseUrl);
      this.httpClient = ApplicationBean.createHttpConnector().getHttpClient(httpHost);
    } catch (MalformedURLException e) {
      String message = "Initialization of connection to MagicPl failed: Malformed magicPl url: {0}";
      logger.fatal(message, e.getMessage());
      throw new RuntimeException(message, e);
    }
  }

  // To be replaced by myGetIds
  @Override
  public Map<IdObject, List<IdObject>> getIds(List<IdObject> searchIds, List<String> resultIdTypes)
      throws IdManagementConnectorException {

    Map<IdObject, List<IdObject>> results = new HashMap<>();

    for (IdObject searchId : searchIds) {
      for (String resultIdType : resultIdTypes) {

        Map<IdObject, List<IdObject>> tempResults = myGetIds(searchId, resultIdType);

        for (Map.Entry<IdObject, List<IdObject>> mapEntry : tempResults.entrySet()) {

          List<IdObject> resultsValue = results.get(mapEntry.getKey());
          if (resultsValue != null) {
            resultsValue.addAll(mapEntry.getValue());
          } else {
            results.put(mapEntry.getKey(), mapEntry.getValue());
          }

        }

      }
    }

    return results;

  }

  private Map<IdObject, List<IdObject>> myGetIds(IdObject searchId, String resultIdType)
      throws IdManagementConnectorException {

    try {
      return myGetIds_WithoutManagementException(searchId, resultIdType);
    } catch (NullPointerException e) {
      return new HashMap<>();
    }
  }

  /**
   * Todo David.
   * @param searchIds Todo David.
   * @param resultIdTypes Todo David.
   * @return Todo David.
   * @throws IdManagementConnectorException IdManagementConnectorException
   */
  public Map<IdObject, List<IdObject>> myGetIds(List<IdObject> searchIds,
      List<String> resultIdTypes)
      throws IdManagementConnectorException {

    //read patients from local IDM
    List<Patient> patients = readPatients(searchIds, resultIdTypes,
        IdManagementUtils.isLocalIdType(searchIds.get(0).getIdType()));
    //check output
    /*
    if (!patients.isEmpty() && patients.size() != searchIds.size()) {
    throw new IdManagementConnectorException("can't get export ids from local IDM: different
    size of the given"
     + " search id list '" + searchIds.size() + "' and returned patient list '" + patients.size()
     + "' ");
        }
     */
    //prepare result
    Map<IdObject, List<IdObject>> result = new HashMap<>();
    if (!patients.isEmpty()) {
      for (int i = 0; i < searchIds.size(); i++) {
        Patient patient = patients.get(i);
        if (patient != null && patient.ids != null) {
          List<IdObject> returnedIds = patient.ids.stream()
              .map(id -> new IdObject(id.idType, id.idString))
              .collect(Collectors.toList());
          result.put(searchIds.get(i), returnedIds);
        }
      }
    }
    return result;
  }


  private Map<IdObject, List<IdObject>> myGetIds_WithoutManagementException(IdObject searchId,
      String resultIdType) throws IdManagementConnectorException {

    List<IdObject> searchIds = new ArrayList<>();
    List<String> resultIdTypes = new ArrayList<>();

    searchIds.add(searchId);
    resultIdTypes.add(resultIdType);

    return myGetIds(searchIds, resultIdTypes);

  }

  @Override
  public List<IdObject> getAllIds(String searchIdType, String resultIdType)
      throws IdManagementConnectorException {
    //read patients from local IDM
    List<Patient> patients = readPatients(
        Collections.singletonList(new IdObject(searchIdType, "*")),
        Collections.singletonList(resultIdType), IdManagementUtils.isLocalIdType(searchIdType));

    //prepare result
    return (patients.isEmpty()) ? new ArrayList<>() : patients.stream()
        .filter(p -> p != null && p.ids != null && !p.ids.isEmpty())
        .map(p -> p.ids.get(0))
        .map(id -> new IdObject(id.idType, id.idString))
        .collect(Collectors.toList());
  }

  @Override
  public List<IdObject> getAllLocalIds(String searchIdType)
      throws IdManagementConnectorException {
    return getAllIds(searchIdType, IdManagementUtils.getDefaultPatientLocalIdType());
  }

  @Override
  public Map<IdObject, IdObject> getExportIds(List<IdObject> searchIds)
      throws IdManagementConnectorException {
    //validate input
    boolean isLocalIdType = IdManagementUtils.isLocalIdType(searchIds.get(0).getIdType());
    String exportIdType = isLocalIdType
        ? IdManagementUtils.getDefaultLocalExportIdType()
        : IdManagementUtils.getDefaultGlobalExportIdType();

    //read patients from local IDM
    List<Patient> patients = readPatients(searchIds, Collections.singletonList(exportIdType),
        isLocalIdType);

    //check output
    if (patients.size() != searchIds.size()) {
      throw new IdManagementConnectorException(
          "can't get export ids from local IDM: different size of the given"
              + " search id list '" + searchIds.size() + "' and returned patient list '" + patients
              .size()
              + "' ");
    }

    //prepare result
    Map<IdObject, IdObject> result = new HashMap<>();
    for (int i = 0; i < searchIds.size(); i++) {
      Patient patient = patients.get(i);
      if (patient != null && patient.ids != null && !patient.ids.isEmpty()) {
        Id id = patient.ids.get(0);
        result.put(searchIds.get(i), new IdObject(id.idType, id.idString));
      }
    }
    return result;
  }

  @Override
  public Map<IdObject, IdObject> getRandomExportIds(List<IdObject> searchIds)
      throws IdManagementConnectorException {

    Map<IdObject, IdObject> randomExportIds = new HashMap<>();

    for (IdObject searchId : searchIds) {

      IdObject randomExportId = generateRandomExportId();
      randomExportIds.put(searchId, randomExportId);

    }

    return randomExportIds;
  }

  private IdObject generateRandomExportId() {

    String idType = IdManagementUtils.getDefaultRandomLocalExportIdType();
    String idString = Utils.getRandomExportid(IdManagementUtils.CENTRAL_MDS_DB_PUBKEY_FILENAME);

    return new IdObject(idType, idString);

  }

  // TODO: TO DELETE: use only myReadPatients when magic pl can accept search ids with not defined
  //  resultIdTypes
  private List<Patient> readPatients(List<IdObject> searchIds, List<String> resultIdTypes,
      boolean isLocalIdType) throws IdManagementConnectorException {

    List<Patient> patients = new ArrayList<>();

    for (IdObject searchId : searchIds) {

      List<IdObject> tempSearchIds = new ArrayList<>();
      tempSearchIds.add(searchId);
      List<Patient> tempPatients = myReadPatients(tempSearchIds, resultIdTypes, isLocalIdType);
      if (tempPatients.isEmpty()) {
        patients.add(null);
      } else {
        patients.addAll(tempPatients);
      }

    }

    return patients;

  }

  private List<Patient> myReadPatients(List<IdObject> searchIds, List<String> resultIdTypes,
      boolean isLocalIdType)
      throws IdManagementConnectorException {
    // validate input
    if (searchIds.isEmpty() || resultIdTypes.isEmpty()) {
      logger.warn(
          "can't get export ids from local IDM: the provided search id or result id list is "
              + "empty");
      return new ArrayList<>();
    }
    /*
    if (!searchIds.stream().allMatch(id -> isLocalIdType ==
    IdManagementUtils.isLocalIdType(id.getIdType()))) {
    throw new IdManagementConnectorException("can't get export ids from local IDM: the type of the
    provided " + "search ids should either be local or global");}
    if (!resultIdTypes.stream().allMatch(idType -> isLocalIdType ==
    IdManagementUtils.isLocalIdType(idType))) {
    throw new IdManagementConnectorException("can't get export ids from local IDM: the type of the
     provided "+ "result ids should either be local or global");
    }
    */
    // prepare request
    HttpPost httpPost = createHttpPost();
    httpPost.setEntity(
        new StringEntity(buildRequestBody(searchIds, resultIdTypes), ContentType.APPLICATION_JSON));

    try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      if (statusCode == 400 || statusCode == 404) {
        return new ArrayList<>();
      } else if (statusCode > 400) {
        throw new IdManagementConnectorException(
            "Can't read patients from local IDM: " + statusLine.getReasonPhrase());
      }

      // parse result from json
      String patientsJsonString = EntityUtils.toString(response.getEntity());
      Type collectionType = new TypeToken<List<Patient>>() {
      }.getType();
      return gson.fromJson(gson.fromJson(patientsJsonString, JsonObject.class).get("patients"),
          collectionType);
    } catch (IOException e) {
      throw new IdManagementConnectorException("Can't read patients from local IDM", e);
    }
  }

  private HttpPost createHttpPost() {
    HttpPost httpPost = new HttpPost(magicPlBaseUrl + URL_PREFIX_READ_PATIENTS);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    httpPost.setHeader(HTTP_HEADERS_API_KEY, magicPlApiKey);
    return httpPost;
  }

  private String buildRequestBody(List<IdObject> searchIds, List<String> resultIds) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("searchIds", gson.toJson(searchIds).replaceAll("\"",
        "\\\""));
    jsonObject.addProperty("resultIds", gson.toJson(resultIds).replaceAll("\"",
        "\\\""));
    return gson.toJson(jsonObject);
  }

  private static class Patient {

    JsonObject fields;
    List<Id> ids;
  }

  private static class Id {

    String idType;
    String idString;
    boolean tentative;
    String uri;
  }
}
