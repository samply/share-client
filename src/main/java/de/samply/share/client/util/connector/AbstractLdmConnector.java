package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.common.ldmclient.AbstractLdmClient;
import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.model.LdmQueryResult;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorRuntimeException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.QueryResultStatistic;
import de.samply.share.model.common.Result;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;

public abstract class AbstractLdmConnector<
    T_LDM_CLIENT extends AbstractLdmClient<T_RESULT, ResultStatisticsT, ErrorT>,
    PostParameterT extends AbstractLdmPostQueryParameter,
    QueryT,
    T_RESULT extends Result & Serializable,
    ResultStatisticsT extends Serializable,
    ErrorT extends Serializable> implements LdmConnector<QueryT, PostParameterT, T_RESULT> {

  private static final int TIMEOUT_LDM_IN_SECONDS = 5 * 60;

  T_LDM_CLIENT ldmClient;
  CloseableHttpClient httpClient;
  String baseUrl;
  HttpHost host;
  private transient HttpConnector httpConnector;

  AbstractLdmConnector(boolean useCaching) {
    init(useCaching);
  }

  AbstractLdmConnector(boolean useCaching, int maxCacheSize) {
    init(useCaching, maxCacheSize);
  }

  abstract boolean useAuthorizationForLdm();

  private void init(boolean useCaching) {
    initBasic();

    try {
      this.ldmClient = createLdmClient(httpClient, baseUrl, useCaching);
    } catch (LdmClientException e) {
      throw new LdmConnectorRuntimeException(e);
    }
  }


  private void init(boolean useCaching, int maxCacheSize) throws LdmConnectorRuntimeException {
    initBasic();

    try {
      this.ldmClient = createLdmClient(httpClient, baseUrl, useCaching, maxCacheSize);
    } catch (LdmClientException e) {
      throw new LdmConnectorRuntimeException(e);
    }
  }

  private void initBasic() throws LdmConnectorRuntimeException {

    this.baseUrl = SamplyShareUtils.addTrailingSlash(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL));

    if (useAuthorizationForLdm()) {
      this.httpConnector = ApplicationBean
          .createHttpConnector(TargetType.TT_LDM, TIMEOUT_LDM_IN_SECONDS);
    } else {
      this.httpConnector = ApplicationBean.createHttpConnector();
    }

    try {
      this.host = SamplyShareUtils.getAsHttpHost(baseUrl);
    } catch (MalformedURLException e) {
      throw new LdmConnectorRuntimeException(e);
    }
    this.httpClient = httpConnector.getHttpClient(host);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T_RESULT getResults(String location) throws LdmConnectorException {
    try {
      return ldmClient.getResult(location);
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public LdmQueryResult getStatsOrError(String location) throws LdmConnectorException {
    try {
      return ldmClient.getStatsOrError(location);
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QueryResultStatistic getQueryResultStatistic(String location)
      throws LdmConnectorException {
    try {
      return ldmClient.getQueryResultStatistic(location);
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getResultCount(String location) throws LdmConnectorException {
    try {
      return ldmClient.getResultCount(location);
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getPageCount(String location) throws LdmConnectorException {
    try {
      return ldmClient.getQueryResultStatistic(location).getNumberOfPages();
    } catch (Exception e) {
      throw new LdmConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeQueryResultPageToDisk(T_RESULT queryResult, int index) throws IOException {
    File dir = (File) ProjectInfo.INSTANCE.getServletContext().getAttribute(TEMPDIR);
    File xmlFile = new File(
        dir + System.getProperty("file.separator") + extractQueryResultId(queryResult) + "_" + index
            + "_transformed" + XML_SUFFIX);

    try {
      final JAXBContext context = JAXBContext.newInstance(QueryResult.class);
      final Marshaller marshaller = context.createMarshaller();
      marshalQueryResult(queryResult, xmlFile, marshaller);
    } catch (JAXBException e) {
      throw new IOException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUserAgentInfo() throws LdmConnectorException {
    try {
      return ldmClient.getUserAgentInfo();
    } catch (LdmClientException e) {
      throw new LdmConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CheckResult checkConnection() {
    CheckResult result = new CheckResult();
    result.setExecutionDate(new Date());
    HttpGet httpGet = new HttpGet(baseUrl + "rest/info/");
    result.getMessages()
        .add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));

    try (CloseableHttpResponse response = httpClient.execute(host, httpGet)) {
      HttpEntity entity = response.getEntity();
      EntityUtils.consume(entity);

      result.getMessages()
          .add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
      int statusCode = response.getStatusLine().getStatusCode();

      if (statusCode >= 200 && statusCode < 400) {
        result.setSuccess(true);
      } else {
        result.setSuccess(false);
        result.getMessages().add(new Message(EntityUtils.toString(entity), "fa-long-arrow-left"));
      }
    } catch (IOException ioe) {
      result.getMessages().add(new Message("IOException: " + ioe.getMessage(), "fa-bolt"));
      result.setSuccess(false);
    }
    return result;
  }

  abstract T_LDM_CLIENT createLdmClient(
      CloseableHttpClient httpClient, String baseUrl, boolean useCaching) throws LdmClientException;

  abstract T_LDM_CLIENT createLdmClient(
      CloseableHttpClient httpClient, String baseUrl, boolean useCaching, int maxCacheSize)
      throws LdmClientException;

  abstract void marshalQueryResult(T_RESULT queryResult, File xmlFile, Marshaller marshaller)
      throws JAXBException;

  abstract String extractQueryResultId(T_RESULT queryResult);

  abstract Logger getLogger();

  private void handleLdmClientException(LdmClientException e) throws LdmConnectorException {
    if (isLdmCentraxx()) {
      throw new LdmConnectorException(e);
    } else if (isLdmSamplystoreBiobank()) {
      e.printStackTrace();
    }
  }
}
