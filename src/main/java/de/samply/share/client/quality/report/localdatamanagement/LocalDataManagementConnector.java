package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

public class LocalDataManagementConnector {

  private int socketTimeout = 100000;
  private int connectTimeout = 100000;


  CloseableHttpResponse getResponse(String uri, HttpUriRequest request) throws IOException {

    request.setHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLdm());
    return getHttpConnector().getHttpClient(uri).execute(request);

  }

  HttpPost createHttpPost(String uri, HttpEntity httpEntity) {

    HttpPost httpPost = new HttpPost(uri);
    httpPost.setHeader("Content-Type", "application/xml");
    httpPost.setHeader("Accept", "application/xml");
    httpPost.setEntity(httpEntity);

    return httpPost;

  }

  HttpGet createHttpGet(String uri) {

    HttpGet httpGet = new HttpGet(uri);
    httpGet.setHeader(HttpHeaders.AUTHORIZATION, CredentialsUtil.getBasicAuthStringForLdm());

    RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
        .setConnectTimeout(connectTimeout).build();
    httpGet.setConfig(requestConfig);

    return httpGet;

  }

  private HttpConnector getHttpConnector() {
    return ApplicationBean.createHttpConnector();
  }

  public String getLocalDataManagementUrl() {
    return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL);
  }

  public String getLocalDataManagementUrlBase() {
    return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL_BASE);
  }


  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }
}
