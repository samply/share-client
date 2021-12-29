package de.samply.share.client.util.connector;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.ComponentInfo;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.util.connector.exception.ComponentConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract connector for receiving basic information of components.
 */
public abstract class AbstractComponentBasicInfoConnector implements IcomponentBasicInfoConnector {

  private static final Logger logger = LoggerFactory.getLogger(IdManagerBasicInfoConnector.class);
  
  /**
   * The Url.
   */
  protected final URL url;
  private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
  private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
  private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10000;
  private int socketTimeout = 180000;
  private int connectTimeout = 180000;
  private int connectionRequestTimeout = 180000;
  /**
   * The Max number of connection attempts.
   */
  protected int maxNumberOfConnectionAttempts = 10;
  /**
   * The Time to wait in seconds between connection attempts.
   */
  protected int timeToWaitInSecondsBetweenConnectionAttempts = 60;
  private final transient HttpConnector httpConnector;
  /**
   * The Http host.
   */
  protected final HttpHost httpHost;
  /**
   * The Http client.
   */
  protected final CloseableHttpClient httpClient;
  /**
   * The Request config.
   */
  protected final RequestConfig requestConfig;
  
  /**
   * Constructor of connector for receiving basic information of components.
   *
   * @param enumConfiguration the EnumConfiguration of the component
   */
  public AbstractComponentBasicInfoConnector(EnumConfiguration enumConfiguration) {
    try {
      url = SamplyShareUtils.stringToUrl(
              ConfigurationUtil.getConfigurationElementValue(enumConfiguration));
      httpConnector = ApplicationBean.createHttpConnector();

      //Read configuration from database
      socketTimeout = getConfigValue(EnumConfiguration
              .ID_CONNECTOR_SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
      connectTimeout = getConfigValue(
              EnumConfiguration.ID_CONNECTOR_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
      connectionRequestTimeout = getConfigValue(EnumConfiguration
                      .ID_CONNECTOR_CONNECTION_REQUEST_TIMEOUT, DEFAULT_CONNECTION_REQUEST_TIMEOUT);
      maxNumberOfConnectionAttempts = getConfigValue(
              EnumConfiguration.ID_CONNECTOR_MAX_NUMBER_OF_CONNECTION_ATTEMPTS,
              maxNumberOfConnectionAttempts);
      timeToWaitInSecondsBetweenConnectionAttempts =
              getConfigValue(
                      EnumConfiguration
                              .ID_CONNECTOR_TIME_TO_WAIT_IN_SECONDS_BETWEEN_CONNECTION_ATTEMPTS,
                      timeToWaitInSecondsBetweenConnectionAttempts);

      requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
              .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(
                      connectionRequestTimeout).build();
      httpHost = SamplyShareUtils.getAsHttpHost(url);
      httpClient = httpConnector.getHttpClient(httpHost);

    } catch (MalformedURLException e) {
      logger.error("Could not initialize ComponentConnector" + enumConfiguration.toString());
      throw new RuntimeException(e);
    }
  }

  /**
   * Query basic imformation from the component in order to obtain name and version information.
   *
   * @return ComponentInfo with name and version information from the component
   */
  public ComponentInfo getComponentInfo() throws ComponentConnectorException {
    ResponseHandler<ComponentInfo> responseHandler = new ResponseHandler<ComponentInfo>() {

      @Override
      public ComponentInfo handleResponse(final HttpResponse response) throws IOException {
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
          return new Gson().fromJson(reader, ComponentInfo.class);
        } catch (JsonSyntaxException | JsonIOException e) {
          throw new IOException(
                  "JSON Exception caught while trying to unmarshal component info...");
        }
      }
    };

    ComponentInfo componentInfo;
    try {
      HttpGet httpGet = new HttpGet(url.toURI());
      httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
      componentInfo = httpClient.execute(httpGet, responseHandler);
    } catch (IOException | URISyntaxException e) {
      throw new ComponentConnectorException(e);
    }

    return componentInfo;
  }

  /**
   * Query basic imformation from the component in order to obtain name and version information.
   *
   * @return String with name and version information from the component
   */
  public String getComponentInfoString() throws ComponentConnectorException {
    ComponentInfo componentInfo = getComponentInfo();
    return componentInfo.getDistname() + "/" + componentInfo.getVersion();
  }

  /**
   * Check the reachability of component.
   *
   * @return a check result object with the outcome of the connection check
   */
  public CheckResult checkConnection() {
    CheckResult result = new CheckResult();
    result.setExecutionDate(new Date());

    try {
      HttpGet httpGet = new HttpGet(url.toURI());
      httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
      result.getMessages()
              .add(new Message(httpGet.getRequestLine().toString(),
                      "fa-long-arrow-right"));
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


  private int getConfigValue(EnumConfiguration enumConfiguration, int defaultConfiguration) {
    try {
      return Integer.parseInt(ConfigurationUtil.getConfigurationElementValue(enumConfiguration));
    } catch (Exception e) {
      return defaultConfiguration;
    }
  }

}
