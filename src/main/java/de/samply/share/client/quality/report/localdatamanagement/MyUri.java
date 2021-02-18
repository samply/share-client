package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.share.common.utils.SamplyShareUtils;
import java.util.HashMap;
import java.util.Map;

public class MyUri {

  private String basicUrl;
  private String urlSuffix;


  private final Map<String, String> parameters = new HashMap<>();


  public MyUri(String basicUrl, String urlSuffix) {
    this.basicUrl = basicUrl;
    this.urlSuffix = urlSuffix;
  }

  public void setBasicUrl(String basicUrl) {
    this.basicUrl = basicUrl;
  }

  public void setUrlSuffix(String urlSuffix) {
    this.urlSuffix = urlSuffix;
  }

  public void addParameter(String key, String value) {
    parameters.put(key, value);
  }

  /**
   * Print uri in quality report.
   *
   * @return uri to be displayed in quality report.
   */
  public String toString() {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(SamplyShareUtils.addTrailingSlash(basicUrl));
    if (urlSuffix != null) {
      stringBuilder.append(urlSuffix);
    }

    if (parameters.size() > 0) {

      stringBuilder.append('?');

      boolean isFirstElement = true;
      for (Map.Entry<String, String> parameter : parameters.entrySet()) {

        if (!isFirstElement) {
          stringBuilder.append(';');
        }

        stringBuilder.append(parameter.getKey())
            .append("=")
            .append(parameter.getValue());
        isFirstElement = false;

      }
    }

    return stringBuilder.toString();
  }

}
