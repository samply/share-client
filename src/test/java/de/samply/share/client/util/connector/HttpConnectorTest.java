package de.samply.share.client.util.connector;

import de.samply.common.config.Configuration;
import de.samply.common.config.HostAuth;
import de.samply.common.config.Proxy;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.model.dto.UserAgent;
import de.samply.share.common.utils.Constants;
import de.samply.share.common.utils.ProjectInfo;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class HttpConnectorTest {


  /*
    For testing the apache http client and the jersey 3 client, we installed a local proxy
    with the following configuration:
  */
  private final String HTTP_PROXY_URL = "http://localhost:808";
  private final String HTTP_PROXY_USERNAME = "test";
  private final String HTTP_PROXY_PASSWORD = "test";

  /*
    We tested both http clients with an external URL (MDR) and an internal URL (CentraXX)
   */
  private final String URL_BASE1 = "https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr";
  private final String URL_PATH1 = "/dataelements/urn:dktk:dataelement:1:3";
  private final String URL_BASE2 = "http://dktk-bridge-dev:8080/centraxx/rest";
  private final String URL_PATH2 = "/info";



  private HostAuth createHostAuth() throws MalformedURLException {

    HostAuth hostAuth = new HostAuth();

    hostAuth.setUrl(new URL(HTTP_PROXY_URL));
    hostAuth.setUsername(HTTP_PROXY_USERNAME);
    hostAuth.setPassword(HTTP_PROXY_PASSWORD);

    return hostAuth;

  }

  private Proxy createProxy() throws MalformedURLException {

    Proxy proxy = new Proxy();

    HostAuth hostAuth = createHostAuth();
    proxy.setHttp(hostAuth);
    proxy.setHttps(hostAuth);
    proxy.setBypassProxyOnPrivateNetwork(false);

    return proxy;

  }

  private Configuration createConfiguration() throws MalformedURLException {

    Configuration configuration = new Configuration();

    Proxy proxy = createProxy();
    configuration.setProxy(proxy);

    return configuration;

  }

  private CredentialsProvider createCredentialsProvider() {

    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope("localhost", 808),
        new UsernamePasswordCredentials(HTTP_PROXY_USERNAME, HTTP_PROXY_PASSWORD));
    return credentialsProvider;

  }

  private UserAgent createUserAgent() {
    return new UserAgent(ProjectInfo.INSTANCE.getProjectName(), "Samply.Share",
        ProjectInfo.INSTANCE.getVersionString());
  }


  private HttpConnector createHttpConnector() throws MalformedURLException {

    CredentialsProvider credentialsProvider = createCredentialsProvider();
    Configuration configuration = createConfiguration();
    UserAgent userAgent = createUserAgent();

    HttpConnector httpConnector = new HttpConnector(
        ConfigurationUtil.getHttpConfigParams(configuration, userAgent),
        credentialsProvider, 180);
    httpConnector
        .addCustomHeader(Constants.HEADER_XML_NAMESPACE, Constants.VALUE_XML_NAMESPACE_COMMON);

    return httpConnector;

  }

  private RequestConfig createRequestConfig(){

    Builder builder = RequestConfig.custom();
    HttpHost proxy = new HttpHost("localhost", 808, "http");
    builder.setProxy(proxy);

    return builder.build();

  }

  private String convertToString(CloseableHttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    return EntityUtils.toString(entity, "UTF-8");
  }


  @Disabled
  @Test
  public void testCloseableHttpClient() throws IOException {

    System.out.println("Internal Closeable HTTP Client Test");

    HttpConnector httpConnector = createHttpConnector();
    CloseableHttpClient httpClient = httpConnector.getHttpClientForHttp();
    RequestConfig requestConfig = createRequestConfig();

    HttpGet httpGet1 = new HttpGet(URL_BASE1+URL_PATH1);
    httpGet1.setConfig(requestConfig);
    httpGet1.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    httpGet1.setHeader(HttpHeaders.ACCEPT_LANGUAGE,"DE");

    HttpGet httpGet2 = new HttpGet(URL_BASE2+URL_PATH2);
    httpGet2.setConfig(requestConfig);

    String response1 = convertToString(httpClient.execute(httpGet1));
    String response2 = convertToString(httpClient.execute(httpGet2));

    assert(response1.contains("urn:dktk:dataelement:1:3"));
    assert(response2.contains("Version"));

  }

  @Disabled
  @Test
  public void testJerseyHttpClient() throws MalformedURLException {

    System.out.println("Jersey HTTP Client test");

    HttpConnector httpConnector = createHttpConnector();
    Client httpClient = httpConnector.getJakartaClientForHttp();

    String response1 = httpClient.target(URL_BASE1).path(URL_PATH1)
        .request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT_LANGUAGE, "DE").get(String.class);

    String response2 = httpClient.target(URL_BASE2).path(URL_PATH2)
        .request(MediaType.APPLICATION_JSON)
        .get(String.class);

    assert(response1.contains("urn:dktk:dataelement:1:3"));
    assert(response2.contains("Version"));

  }


}
