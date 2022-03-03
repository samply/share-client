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
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class HttpConnectorTest {

  private HostAuth createHostAuth() throws MalformedURLException {

    HostAuth hostAuth = new HostAuth();

    hostAuth.setUrl(new URL("http://localhost:808"));
    hostAuth.setUsername("test");
    hostAuth.setPassword("test");

    return hostAuth;

  }

  private Proxy createProxy() throws MalformedURLException {

    Proxy proxy = new Proxy();

    HostAuth hostAuth = createHostAuth();
    proxy.setHttp(hostAuth);
    proxy.setHttps(hostAuth);

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
        ConfigurationUtil.getHttpConfigParams(configuration, userAgent), credentialsProvider, 180);
    httpConnector
        .addCustomHeader(Constants.HEADER_XML_NAMESPACE, Constants.VALUE_XML_NAMESPACE_COMMON);

    return httpConnector;

  }

  @Disabled
  @Test
  public void testCloseableHttpClient() throws IOException {

    HttpConnector httpConnector = createHttpConnector();
    CloseableHttpClient httpClient = httpConnector.getHttpClientForHttp();
    String url1 = "https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr/dataelements/urn:dktk:dataelement:1:3";
    String url2 = "http://dktk-bridge-dev.dkfz-heidelberg.de:8080/centraxx/rest/info/";

    HttpGet httpGet1 = new HttpGet(url1);
    HttpGet httpGet2 = new HttpGet(url2);

    CloseableHttpResponse response1 = httpClient.execute(httpGet1);
    CloseableHttpResponse response2 = httpClient.execute(httpGet2);

    System.out.println("hello");

  }

  @Disabled
  @Test
  public void testJerseyHttpClient() throws MalformedURLException {

    HttpConnector httpConnector = createHttpConnector();

    String url1 = "https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr";
    String path1 = "/dataelements/urn:dktk:dataelement:1:3";
    String url2 = "http://dktk-bridge-dev.dkfz-heidelberg.de:8080/centraxx/rest";
    String path2 = "/info";

    Client httpClient1 = httpConnector.getJakartaClient(url1);
    Client httpClient2 = httpConnector.getJakartaClient(url2);

    String response1 = httpClient1.target(url1).path(path1).request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT_LANGUAGE, "DE").get(String.class);

    String response2 = httpClient1.target(url2).path(path2).request(MediaType.APPLICATION_JSON)
        .get(String.class);

    assert(response1.contains("urn:dktk:dataelement:1:3"));
    assert(response2.contains("Version"));

    System.out.println("Jersey test");
  }


}
