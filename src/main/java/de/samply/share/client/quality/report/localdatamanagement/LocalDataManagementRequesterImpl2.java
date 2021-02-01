package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
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


public class LocalDataManagementRequesterImpl2 extends LocalDataManagementConnector implements
    LocalDataManagementRequester {


  private static final Logger logger = LogManager.getLogger(LocalDataManagementRequesterImpl.class);


  private final LdmUnmarshaller<QueryResult> queryResultLdmUnmarshaller = new LdmUnmarshallerImpl<>(
      QueryResult.class);
  private final ErrorLdmUnmarshaller errorLdmUnmarshaller = new ErrorLdmUnmarshaller();
  private final QueryResultStatisticLdmUnmarshaller queryResultStatisticLdmUnmarshaller =
      new QueryResultStatisticLdmUnmarshaller();


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
    return getLocalDataManagementResponse(myUri, queryResultStatisticLdmUnmarshaller);
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
    return getLocalDataManagementResponse(myUri, queryResultLdmUnmarshaller);
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
    LdmConnectorCentraxx ldmConnector = (LdmConnectorCentraxx) ApplicationBean.getLdmConnector();
    String version = ldmConnector.getMappingVersion();
    LocalDataManagementResponse<String> localDataManagementResponse =
        new LocalDataManagementResponse<>();
    localDataManagementResponse.setStatusCode(HttpStatus.SC_OK);
    localDataManagementResponse.setResponse(version);
    return localDataManagementResponse;
  }


  private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse(MyUri myUri,
      LdmUnmarshaller<T> ldmUnmarshaller) throws LocalDataManagementRequesterException {

    String uri = myUri.toString();
    HttpGet httpGet = createHttpGet(uri);

    return getLocalDataManagementResponse(uri, httpGet, ldmUnmarshaller);
  }

  private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse(String url,
      HttpGet httpGet, LdmUnmarshaller<T> ldmUnmarshaller)
      throws LocalDataManagementRequesterException {

    try (CloseableHttpResponse response = getResponse(url, httpGet)) {

      return getLocalDataManagementResponse(response, ldmUnmarshaller);

    } catch (Exception e) {
      throw new LocalDataManagementRequesterException(e);
    }

  }

  private <T> LocalDataManagementResponse<T> getLocalDataManagementResponse(
      CloseableHttpResponse response, LdmUnmarshaller<T> ldmUnmarshaller)
      throws LocalDataManagementRequesterException, IOException {

    int statusCode = response.getStatusLine().getStatusCode();

    HttpEntity ccpEntity = response.getEntity();
    String entityOutput = EntityUtils.toString(ccpEntity, Consts.UTF_8);

    LocalDataManagementResponse<T> ldmResponse = new LocalDataManagementResponse<>();
    ldmResponse.setStatusCode(statusCode);

    if (statusCode == HttpStatus.SC_OK) {

      T resp = ldmUnmarshaller.unmarshall(entityOutput);
      ldmResponse.setResponse(resp);

    } else if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {

      Error error = errorLdmUnmarshaller.unmarshall(entityOutput);
      ldmResponse.setError(error);
    }

    EntityUtils.consume(ccpEntity);

    return ldmResponse;

  }

  private interface LdmUnmarshaller<T> {

    T unmarshall(String entity) throws LocalDataManagementRequesterException;
  }

  private class LdmUnmarshallerImpl<T> implements LdmUnmarshaller<T> {

    private final Class<T> classz;

    public LdmUnmarshallerImpl(Class<T> classz) {
      this.classz = classz;
    }

    @Override
    public T unmarshall(String entity) throws LocalDataManagementRequesterException {
      return (T) createObject(entity);
    }

    protected Class getInputClass(String entity) {
      return classz;
    }

    protected Object createObject(String entity) throws LocalDataManagementRequesterException {

      try {
        return createObject_WithoutCastException(entity);
      } catch (ClassCastException e) {

        logger.info("[--ClassCastExeption-----");
        logger.info(entity);
        logger.info("------------------------]");

        throw new LocalDataManagementRequesterException(e);

      } catch (JAXBException e) {
        throw new LocalDataManagementRequesterException(e);
      }

    }

    private Object createObject_WithoutCastException(String entity) throws JAXBException {

      JAXBContext jaxbContext = JAXBContext.newInstance(getInputClass(entity));
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      Object object = jaxbUnmarshaller.unmarshal(new StringReader(entity));

      return (object instanceof JAXBElement) ? ((JAXBElement) object).getValue() : object;

    }


  }

  private class ErrorLdmUnmarshaller extends LdmUnmarshallerImpl<Error> {

    public ErrorLdmUnmarshaller() {
      super(Error.class);
    }

    @Override
    protected Class getInputClass(String entity) {
      return de.samply.share.model.ccp.Error.class;
    }

    @Override
    public Error unmarshall(String entity) throws LocalDataManagementRequesterException {

      de.samply.share.model.ccp.Error error = (de.samply.share.model.ccp.Error) createObject(
          entity);
      return convertCcpErrorToCommonError(error);

    }

    private Error convertCcpErrorToCommonError(de.samply.share.model.ccp.Error error)
        throws LocalDataManagementRequesterException {
      try {
        return QueryConverter.convertCcpErrorToCommonError(error);
      } catch (JAXBException e) {
        throw new LocalDataManagementRequesterException(e);
      }
    }

  }

  private class QueryResultStatisticLdmUnmarshaller extends
      LdmUnmarshallerImpl<QueryResultStatistic> {

    public QueryResultStatisticLdmUnmarshaller() {
      super(QueryResultStatistic.class);
    }

    @Override
    public QueryResultStatistic unmarshall(String entity)
        throws LocalDataManagementRequesterException {

      Object object = createObject(entity);
      QueryResultStatistic queryResultStatistic = convertToOutputClass(object);

      return queryResultStatistic;

    }


    @Override
    protected Class getInputClass(String entity) {
      return (entity.contains("http://de.kairos.centraxx/ccp/QueryResultStatistic"))
          ? de.samply.common.ldmclient.centraxx.model.QueryResultStatistic.class
          : QueryResultStatistic.class;

    }

    private QueryResultStatistic convertToOutputClass(Object object) {

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

  }


}
