package de.samply.share.client.util.connector;

import ca.uhn.fhir.util.TestUtil;
import de.samply.common.config.HostAuth;
import de.samply.common.config.Proxy;
import de.samply.share.client.util.CertificateReader;
import de.samply.share.client.util.CertificateReaderException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Disabled
@Testcontainers
public class CertificateReaderTest {
  private static final Logger logger = LoggerFactory.getLogger(
      CertificateReaderTest.class);

  // TODO: Integration Test instead of unit test.
  private final static String PROXY_HOST = "http://myproxy.com:1234"; // Change me!

  @Container
  @SuppressWarnings("resource")
  private static final GenericContainer<?> broker = new GenericContainer<>("nginx")
      .withFileSystemBind(getPath("nginx.conf"), "/etc/nginx/conf.d/broker.conf")
      .withFileSystemBind(getPath("broker.crt"), "/etc/nginx/broker.crt")
      .withFileSystemBind(getPath("broker.key"), "/etc/nginx/broker.key")
      .withExposedPorts(443)
      .withLogConsumer(new Slf4jLogConsumer(logger));



  private HostAuth createHostAuth() throws MalformedURLException {

    HostAuth hostAuth = new HostAuth();
    hostAuth.setUrl(new URL(PROXY_HOST));
    return hostAuth;

  }

  private Proxy createProxy() throws MalformedURLException {

    Proxy proxy = new Proxy();
    proxy.setHttp(createHostAuth());

    return proxy;

  }


  @Test
  public void testExtractCertificateDateWithoutProxy()
      throws CertificateReaderException, MalformedURLException, ParseException {

    String mdrUrl = "https://localhost/";

    CertificateReader certificateReader = new CertificateReader(new Proxy());
    Date date = certificateReader.extractCertificateValidationDate(mdrUrl);

    Assertions.assertEquals(new Date(133,9, 2, 1,59,59), date);

  }
  static String getPath(String name) {
    return Objects.requireNonNull(CertificateReaderTest.class.getResource(name)).getPath();
  }

}

