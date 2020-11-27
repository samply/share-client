package de.samply.share.client.quality.report.file.downloader;

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;


public class FileDownloaderImpl implements FileDownloader {

  private String sourceUrl;
  private String destinationFilePath;


  FileDownloaderImpl() {
  }

  public FileDownloaderImpl(String sourceUrl, String destinationFilePath) {
    this.sourceUrl = sourceUrl;
    this.destinationFilePath = destinationFilePath;
  }

  @Override
  public void download() throws FileDownloaderException {

    try {

      downloadWithoutExceptionManagement();

    } catch (IOException | URISyntaxException e) {
      throw new FileDownloaderException(e);
    }

  }

  private void download(URL sourceUrl) throws IOException, URISyntaxException {
    CloseableHttpClient httpClient = getHttpClient(sourceUrl);
    download(httpClient, sourceUrl);
  }

  private void download(CloseableHttpClient httpClient, URL url)
      throws IOException, URISyntaxException {

    try (CloseableHttpResponse httpResponse = getHttpResponse(httpClient, url)) {
      download(httpResponse.getEntity().getContent());
    }
  }

  private void download(InputStream inputStream) throws IOException {
    try (OutputStream outputStream = new BufferedOutputStream(
        new FileOutputStream(destinationFilePath))) {

      byte[] chunk = new byte[1024];
      int chunkSize;
      while ((chunkSize = inputStream.read(chunk)) != -1) {
        outputStream.write(chunk, 0, chunkSize);
      }
      outputStream.flush();
    }
  }

  private void downloadWithoutExceptionManagement() throws IOException, URISyntaxException {

    URL sourceUrl = new URL(this.sourceUrl);
    download(sourceUrl);

  }


  private CloseableHttpClient getHttpClient(URL url) {

    HttpConnector httpConnector = ApplicationBean.createHttpConnector();
    return httpConnector.getHttpClient(url);

  }

  private CloseableHttpResponse getHttpResponse(CloseableHttpClient httpClient, URL url)
      throws URISyntaxException, IOException {

    HttpGet httpGet = new HttpGet(url.toURI());
    return httpClient.execute(httpGet);

  }

  void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  void setDestinationFilePath(String destinationFilePath) {
    this.destinationFilePath = destinationFilePath;
  }
}
