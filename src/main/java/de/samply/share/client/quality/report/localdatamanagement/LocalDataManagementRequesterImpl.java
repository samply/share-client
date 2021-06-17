package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.util.connector.LdmConnectorCentraxxExtension;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Error;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.View;
import de.samply.share.utils.QueryConverter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LocalDataManagementRequesterImpl extends LocalDataManagementConnector implements
    LocalDataManagementRequester {


  private static final Logger logger = LogManager.getLogger(LocalDataManagementRequesterImpl.class);

  private final QueryResultStatisticClassGetter queryResultStatisticClassGetter =
      new QueryResultStatisticClassGetter();
  private final ErrorClassGetter errorClassGetter = new ErrorClassGetter();


  @Override
  public LocalDataManagementResponse<String> postViewAndGetLocationUrlStatisticsOnly(View view)
      throws LocalDataManagementRequesterException {
    return postViewAndGetLocationUrl(view, true);
  }

  @Override
  public LocalDataManagementResponse<String> postViewAndGetLocationUrl(View view)
      throws LocalDataManagementRequesterException {
    return postViewAndGetLocationUrl(view, false);
  }

  private LocalDataManagementResponse<String> postViewAndGetLocationUrl(View view,
      boolean statisticsOnly) throws LocalDataManagementRequesterException {

    try {

      return postViewAndGetLocationUrlWithoutExceptions(view, statisticsOnly);

    } catch (Exception e) {
      throw new LocalDataManagementRequesterException(e);
    }
  }

  private LocalDataManagementResponse<String> postViewAndGetLocationUrlWithoutExceptions(View view,
      boolean statisticsOnly)
      throws JAXBException, UnsupportedEncodingException, LocalDataManagementRequesterException {

    String localDataManagementUrl = SamplyShareUtils.addTrailingSlash(getLocalDataManagementUrl());
    localDataManagementUrl += getLocalDataManagementUrlBase();

    MyUri myUri = new MyUri(localDataManagementUrl,
        LocalDataManagementUrlSuffixAndParameters.REQUESTS_URL_SUFFIX);
    if (statisticsOnly) {
      myUri.addParameter(LocalDataManagementUrlSuffixAndParameters.STATISTICS_ONLY_PARAMETER,
          "true");
    }
    String uri = myUri.toString();

    de.samply.share.model.ccp.View ccpView = QueryConverter.convertCommonViewToCcpView(view);
    String ccpViewS = QueryConverter.viewToXml(ccpView);
    HttpEntity httpEntity = new StringEntity(ccpViewS);

    HttpPost httpPost = createHttpPost(uri, httpEntity);

    return getLocationHeader(localDataManagementUrl, httpPost);

  }


  private LocalDataManagementResponse<String> getLocationHeader(String localDataManagementUrl,
      HttpPost httpPost) throws LocalDataManagementRequesterException {

    try (CloseableHttpResponse response = getResponse(localDataManagementUrl, httpPost)) {

      LocalDataManagementResponse<String> ldmResponse = new LocalDataManagementResponse<>();

      int statusCode = response.getStatusLine().getStatusCode();
      ldmResponse.setStatusCode(statusCode);

      Header location = response.getFirstHeader("Location");
      if (location != null) {
        ldmResponse.setResponse(location.getValue());
      }

      return ldmResponse;


    } catch (Exception e) {
      throw new LocalDataManagementRequesterException(e);
    }

  }

  @Override
  public LocalDataManagementResponse<QueryResultStatistic> getQueryResultStatistic(
      String locationUrl) throws LocalDataManagementRequesterException {

    MyUri myUri = new MyUri(locationUrl,
        LocalDataManagementUrlSuffixAndParameters.STATISTICS_URL_SUFFIX);

    return getQueryResultStatistic(myUri);
  }

  private LocalDataManagementResponse<QueryResultStatistic> getQueryResultStatistic(MyUri myUri)
      throws LocalDataManagementRequesterException {
    return getLocalDataManagementResponse(myUri, queryResultStatisticClassGetter);
  }

  @Override
  public LocalDataManagementResponse<QueryResult> getQueryResult(String locationUrl, int page)
      throws LocalDataManagementRequesterException {

    MyUri myUri = new MyUri(locationUrl,
        LocalDataManagementUrlSuffixAndParameters.RESULTS_URL_SUFFIX);
    myUri.addParameter(LocalDataManagementUrlSuffixAndParameters.PAGE_PARAMETER,
        String.valueOf(page));

    return getQueryResult(myUri);
  }

  private LocalDataManagementResponse<QueryResult> getQueryResult(MyUri myUri)
      throws LocalDataManagementRequesterException {
    return getLocalDataManagementResponse(myUri, (x) -> QueryResult.class);
  }

  @Override
  public LocalDataManagementResponse<String> getSqlMappingVersion()
      throws LocalDataManagementRequesterException {

    try {
      return getSqlMappingVersion_WithoutManagementException();
    } catch (Exception e) {
      throw new LocalDataManagementRequesterException(e);
    }

  }

  private LocalDataManagementResponse<String> getSqlMappingVersion_WithoutManagementException() {
    if (!ApplicationUtils.isDktk()) {
      return null;
    }
    String version = ((LdmConnectorCentraxxExtension) ApplicationBean.getLdmConnector())
        .getMappingVersion();
    LocalDataManagementResponse<String> localDataManagementResponse =
        new LocalDataManagementResponse<>();
    localDataManagementResponse.setStatusCode(HttpStatus.SC_OK);
    localDataManagementResponse.setResponse(version);
    return localDataManagementResponse;
  }

  private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse(MyUri myUri,
      ClassGetter classGetter) throws LocalDataManagementRequesterException {

    String uri = myUri.toString();
    HttpGet httpGet = createHttpGet(uri);

    return getLocalDataManagementResponse(uri, httpGet, classGetter);
  }

  private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse(String url,
      HttpGet httpGet, ClassGetter classGetter) throws LocalDataManagementRequesterException {

    try (CloseableHttpResponse response = getResponse(url, httpGet)) {

      return getLocalDataManagementResponse(response, classGetter);

    } catch (Exception e) {
      throw new LocalDataManagementRequesterException(e);
    }

  }

  private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse(
      CloseableHttpResponse response, ClassGetter classGetter)
      throws IOException, JAXBException, LocalDataManagementRequesterException {

    int statusCode = response.getStatusLine().getStatusCode();

    HttpEntity ccpEntity = response.getEntity();
    String entityOutput = EntityUtils.toString(ccpEntity, Consts.UTF_8);

    LocalDataManagementResponse<T> ldmResponse = new LocalDataManagementResponse<>();
    ldmResponse.setStatusCode(statusCode);

    if (statusCode == HttpStatus.SC_OK) {

      T resp = createTObject(entityOutput, classGetter);
      ldmResponse.setResponse(resp);

    } else if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {

      Error error = createTObject(entityOutput, errorClassGetter);
      ldmResponse.setError(error);
    }

    EntityUtils.consume(ccpEntity);

    return ldmResponse;

  }

  private <T> T createTObject(String entity, ClassGetter classGetter)
      throws JAXBException, LocalDataManagementRequesterException {

    Object object = createObject(entity, classGetter);
    return (classGetter instanceof ClassGetterWithConverter)
        ? ((ClassGetterWithConverter<T>) classGetter).convertToOutputClass(object) : (T) object;

  }

  private Object createObject(String entity, ClassGetter classGetter) throws JAXBException {

    try {
      return createObject_WithoutCastException(entity, classGetter);
    } catch (ClassCastException exception) {

      logger.info("[--ClassCastExeption-----");
      logger.info(entity);
      logger.info("------------------------]");

      throw exception;

    }

  }

  private Object createObject_WithoutCastException(String entity, ClassGetter classGetter)
      throws JAXBException {

    JAXBContext jaxbContext = JAXBContext.newInstance(classGetter.getInputClass(entity));
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    Object object = jaxbUnmarshaller.unmarshal(new StringReader(entity));

    return (object instanceof JAXBElement) ? ((JAXBElement) object).getValue() : object;

  }

  private interface ClassGetter {

    Class<?> getInputClass(String entity);
  }

  private interface ClassGetterWithConverter<T> extends ClassGetter {

    T convertToOutputClass(Object object) throws LocalDataManagementRequesterException;
  }

  private static class QueryResultStatisticClassGetter implements
      ClassGetterWithConverter<QueryResultStatistic> {

    @Override
    public QueryResultStatistic convertToOutputClass(Object object) {

      QueryResultStatistic result = null;

      if (object instanceof QueryResultStatistic) {

        result = (QueryResultStatistic) object;

      } else if (object instanceof de.samply.common.ldmclient.centraxx.model.QueryResultStatistic) {

        result = new QueryResultStatistic();
        de.samply.common.ldmclient.centraxx.model.QueryResultStatistic input =
            (de.samply.common.ldmclient.centraxx.model.QueryResultStatistic) object;

        result.setNumberOfPages(input.getNumberOfPages());
        result.setRequestId(input.getRequestId());
        result.setTotalSize(input.getTotalSize());

      }

      return result;

    }

    @Override
    public Class<?> getInputClass(String entity) {
      return (entity.contains("http://de.kairos.centraxx/ccp/QueryResultStatistic"))
          ? de.samply.common.ldmclient.centraxx.model.QueryResultStatistic.class
          : QueryResultStatistic.class;

    }

  }

  private static class ErrorClassGetter implements ClassGetterWithConverter<Error> {

    @Override
    public Class getInputClass(String entity) {
      return de.samply.share.model.ccp.Error.class;
    }

    @Override
    public Error convertToOutputClass(Object object) throws LocalDataManagementRequesterException {
      de.samply.share.model.ccp.Error error = (de.samply.share.model.ccp.Error) object;
      return convert(error);
    }

    private Error convert(de.samply.share.model.ccp.Error error)
        throws LocalDataManagementRequesterException {
      try {
        return QueryConverter.convertCcpErrorToCommonError(error);
      } catch (JAXBException e) {
        throw new LocalDataManagementRequesterException(e);
      }
    }
  }


}
