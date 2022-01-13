package de.samply.share.client.model.centralsearch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Represents the upload stats as received from central search.
 */

@JacksonXmlRootElement(localName = "UploadStats")
public class UploadStats {

  @JacksonXmlProperty(localName = "LastUploadTimestamp")
  private String lastUploadTimestamp;

  public UploadStats() {
    super();
  }

  public UploadStats(String lastUploadTimestamp) {
    this.lastUploadTimestamp = lastUploadTimestamp;
  }

  public String getLastUploadTimestamp() {
    return lastUploadTimestamp;
  }

  public void setLastUploadTimestamp(String lastUploadTimestamp) {
    this.lastUploadTimestamp = lastUploadTimestamp;
  }

}
