package de.samply.share.client.util.connector;

import com.google.common.base.Splitter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.centraxx.LdmClientCentraxx;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.util.MdrUtils;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.client.util.connector.centraxx.CxxMappingParser;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.Error;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.And;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Eq;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.View;
import de.samply.share.model.common.ViewFields;
import de.samply.share.model.common.Where;
import de.samply.share.utils.QueryConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the LdmConnector interface for centraxx backends.
 */
public class LdmConnectorCentraxx extends
    AbstractLdmConnectorView<LdmClientCentraxx, QueryResult, QueryResultStatistic, Error,
        de.samply.share.model.ccp.View> implements LdmConnectorCentraxxExtension {

  private static final Logger logger = LoggerFactory.getLogger(LdmConnectorCentraxx.class);
  private final String mappingPath = "mapping";
  private final CxxMappingParser cxxMappingParser = new CxxMappingParser();
  private MdrMappedElements mdrMappedElements;

  public LdmConnectorCentraxx(boolean useCaching) {
    super(useCaching);
  }

  public LdmConnectorCentraxx(boolean useCaching, int maxCacheSize) {
    super(useCaching, maxCacheSize);
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  @Override
  public boolean isLdmCentraxx() {
    return true;
  }

  @Override
  boolean useAuthorizationForLdm() {
    return true;
  }

  @Override
  LdmClientCentraxx createLdmClient(CloseableHttpClient httpClient, String baseUrl,
      boolean useCaching) throws LdmClientException {

    LdmClientCentraxx ldmClientCentraxx = new LdmClientCentraxx(httpClient, getBaseUrl(),
        useCaching);
    ldmClientCentraxx
        .addHttpHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLdm());

    return ldmClientCentraxx;

  }

  @Override
  LdmClientCentraxx createLdmClient(CloseableHttpClient httpClient, String baseUrl,
      boolean useCaching, int maxCacheSize) throws LdmClientException {

    LdmClientCentraxx ldmClientCentraxx = new LdmClientCentraxx(httpClient, getBaseUrl(),
        useCaching, maxCacheSize);
    ldmClientCentraxx
        .addHttpHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLdm());

    return ldmClientCentraxx;

  }

  private View createView(Query query, boolean completeMdsViewFields) throws LdmConnectorException {
    return createView(query, null, completeMdsViewFields, false);
  }

  @Override
  View createView(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields,
      boolean includeAdditionalViewfields) throws LdmConnectorException {
    View view = new View();
    // Substitute BETWEEN and IN operators
    Query fixedQuery = QueryConverter.substituteOperators(query);
    view.setQuery(fixedQuery);

    ViewFields viewFields;
    try {
      viewFields = MdrUtils.getViewFields(completeMdsViewFields);
    } catch (MdrConnectionException | ExecutionException e) {
      throw new LdmConnectorException(e);
    }

    // Add additional viewfields, as defined in the config
    String additionalViewfields = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.INQUIRY_ADDITIONAL_MDRKEYS);
    if (includeAdditionalViewfields && !SamplyShareUtils.isNullOrEmpty(additionalViewfields)) {
      List<String> viewFieldList = Splitter.on(';').splitToList(additionalViewfields);
      for (String viewField : viewFieldList) {
        viewFields.getMdrKey().add(viewField);
      }
    }

    viewFields = filterNotExistentMdrIdsInViewFields(viewFields);

    view.setViewFields(viewFields);
    if (!SamplyShareUtils.isNullOrEmpty(removeKeysFromView)) {
      view = QueryConverter.removeAttributesFromView(view, removeKeysFromView);
    }
    return view;
  }

  private ViewFields filterNotExistentMdrIdsInViewFields(ViewFields viewFields) {

    if (viewFields != null) {

      List<String> mdrKeyList = viewFields.getMdrKey();
      if (mdrKeyList != null) {

        MdrMappedElements mdrMappedElements = getMdrMappedElements();

        List<String> filteredMdrKeyList = new ArrayList<>(mdrKeyList);

        for (String mdrId : filteredMdrKeyList) {
          if (!mdrMappedElements.isMapped(mdrId)) {
            mdrKeyList.remove(mdrId);
          }
        }
      }
    }

    return viewFields;
  }

  @Override
  String extractQueryResultId(QueryResult queryResult) {
    return queryResult.getId();
  }

  @Override
  void marshalQueryResult(QueryResult queryResult, File xmlFile, Marshaller marshaller)
      throws JAXBException {
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    ObjectFactory objectFactory = new ObjectFactory();
    marshaller.marshal(objectFactory.createQueryResult(queryResult), xmlFile);
  }

  @Override
  View createViewForMonitoring(boolean dktkFlagged) throws LdmConnectorException {
    MdrIdDatatype mdrKeyDktkConsent =
        new MdrIdDatatype(
            ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CONSENT_DKTK));
    View view = new View();
    Query query = new Query();
    ObjectFactory objectFactory = new ObjectFactory();
    Where where = new Where();
    And and = new And();

    if (dktkFlagged) {
      Attribute attrDktkFlag = new Attribute();
      attrDktkFlag.setMdrKey(mdrKeyDktkConsent.getLatestCentraxx());
      attrDktkFlag.setValue(objectFactory.createValue("true"));

      Eq equals = new Eq();
      equals.setAttribute(attrDktkFlag);
      and.getAndOrEqOrLike().add(equals);
      where.getAndOrEqOrLike().add(and);
    }

    query.setWhere(where);
    view.setQuery(query);
    try {
      view.setViewFields(MdrUtils.getViewFields(false));
    } catch (MdrConnectionException | ExecutionException e) {
      throw new LdmConnectorException(e);
    }
    return view;
  }

  /**
   * Create the reference view that is used to get the amount of patients for that query.
   *
   * @param referenceQuery the reference query, as received from the broker
   * @return the constructed view object that can be posted to centraxx
   */
  @Override
  View createReferenceViewForMonitoring(Query referenceQuery) throws LdmConnectorException {
    return createView(referenceQuery, true);
  }

  /**
   * Retrieve the version of the used mapping script from centraxx.
   *
   * @return whatever is written as revision information in this reply
   */
  @Override
  public String getMappingVersion() {
    EnumConfiguration mdrKey = EnumConfiguration.MDR_KEY_CENTRAXX_MAPPING_VERSION;

    return getMappingForMdrItem(mdrKey);
  }

  /**
   * Retrieve the date of the used mapping script from centraxx.
   *
   * @return whatever is written as revision information in this reply
   */
  @Override
  public String getMappingDate() {
    EnumConfiguration mdrKey = EnumConfiguration.MDR_KEY_CENTRAXX_MAPPING_DATE;

    return getMappingForMdrItem(mdrKey);
  }

  private String getBaseUrl() {

    String base2 = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL_BASE);
    return baseUrl + base2;

  }

  private String getMappingForMdrItem(EnumConfiguration mdrKey) {
    String centraxxMappingMdrKey = ConfigurationUtil.getConfigurationElementValue(mdrKey);
    MdrIdDatatype mappingMdrItem = new MdrIdDatatype(centraxxMappingMdrKey);

    HttpGet httpGet = new HttpGet(
        getBaseUrl() + mappingPath + '/' + mappingMdrItem.getLatestCentraxx());
    httpGet.setHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLdm());

    try (CloseableHttpResponse response = httpClient.execute(host, httpGet)) {
      HttpEntity entity = response.getEntity();
      String mappingInfo = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);

      JsonParser jsonParser = new JsonParser();
      JsonArray jsonArray = jsonParser.parse(mappingInfo).getAsJsonArray();

      if (jsonArray != null && jsonArray.size() > 0) {
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        return jsonObject.get("urnRevision").getAsString();
      } else {
        return "undefined";
      }
    } catch (Exception e) {
      logger.warn("Exception caught while trying to get centraxx mapping for key '" + mdrKey
          + "'", e);
      return "undefined";
    }
  }

  @Override
  public List<CxxMappingElement> getMapping() {

    HttpGet httpGet = new HttpGet(getBaseUrl() + mappingPath);
    httpGet.setHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLdm());

    return getMapping(httpGet);
  }

  private List<CxxMappingElement> getMapping(HttpGet httpGet) {

    try (CloseableHttpResponse response = httpClient.execute(host, httpGet)) {

      HttpEntity entity = response.getEntity();
      String cxxMapping = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);

      return cxxMappingParser.parse(cxxMapping);

    } catch (Exception e) {

      logger.warn("Exception caught while trying to get centraxx mapping", e);
      return new ArrayList<>();

    }

  }

  private MdrMappedElements getMdrMappedElements() {

    if (mdrMappedElements == null) {
      mdrMappedElements = new MdrMappedElements(this);
    }

    return mdrMappedElements;

  }
}
