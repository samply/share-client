package de.samply.share.client.util.connector;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CertificateReaderTest {

  // TODO: Integration Test instead of unit test.
  private final static String PROXY_HOST = "http://myproxy.com:1234"; // Change me!

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

  @Disabled
  @Test
  public void testExtractCertificateDate()
      throws CertificateReaderException, MalformedURLException, ParseException {

    String mdrUrl = "https://mdr.ccp-it.dktk.dkfz.de/";

    CertificateReader certificateReader = new CertificateReader(createProxy());
    Date date = certificateReader.extractCertificateValidationDate(mdrUrl);
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    Date expectedDate = sdf.parse("Sun Oct 02 01:59:59 CEST 2033");
    Assertions.assertEquals(expectedDate, date);

  }

}
