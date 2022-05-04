package de.samply.share.client.util;

import de.samply.common.config.HostAuth;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;


public class CertificateReader {

  private final Optional<Proxy> proxy;

  /**
   * Read Certificate from url.
   *
   * @param proxy Proxy.
   */
  public CertificateReader(de.samply.common.config.Proxy proxy) {
    this.proxy = convert(proxy);
  }

  /**
   * Extracts cetificate validation date.
   *
   * @param url url with the certificate to be extracted.
   * @return Date of certificate.
   * @throws CertificateReaderException Encapsulates inner exceptions.
   */

  public Date extractCertificateValidationDate(String url) throws CertificateReaderException {

    try {
      return extractCertificateValidationDateWithoutManagementException(url);
    } catch (IOException e) {
      throw new CertificateReaderException(e);
    }

  }

  private Date extractCertificateValidationDateWithoutManagementException(String url)
      throws IOException {

    Date certificateValidationDate = null;

    if (url.contains("https")) {

      HttpsURLConnection httpsUrlConnection = this.proxy.isPresent()
          ? (HttpsURLConnection) new URL(url).openConnection(this.proxy.get()) :
          (HttpsURLConnection) new URL(url).openConnection();

      httpsUrlConnection.connect();
      Certificate[] serverCertificates = httpsUrlConnection.getServerCertificates();
      httpsUrlConnection.disconnect();

      if (serverCertificates != null && serverCertificates.length > 0) {
        Certificate certificate = serverCertificates[serverCertificates.length - 1];
        if (certificate instanceof X509Certificate) {
          certificateValidationDate = ((X509Certificate) certificate).getNotAfter();
        }
      }

    }

    return certificateValidationDate;
  }

  private static Optional<Proxy> convert(de.samply.common.config.Proxy proxy) {

    final HostAuth hostAuth = (proxy.getHttp() != null) ? proxy.getHttp() : proxy.getHttps();

    if (hostAuth != null) {

      if (hostAuth.getUsername() != null && hostAuth.getPassword() != null) {
        // This is not nice
        Authenticator.setDefault(new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(hostAuth.getUsername(),
                hostAuth.getPassword().toCharArray());
          }
        });
      }

      return Optional.of(new Proxy(Proxy.Type.HTTP,
          new InetSocketAddress(hostAuth.getUrl().getHost(), hostAuth.getUrl().getPort())));
    }

    return Optional.empty();
  }

}
