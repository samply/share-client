package de.samply.share.client.model.centralsearch;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents the upload stats as received from central search.
 */
@Root(name = "UploadStats")
public class UploadStats {

  @Element(name = "LastUploadTimestamp")
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
}
