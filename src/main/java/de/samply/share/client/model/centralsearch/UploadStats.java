package de.samply.share.client.model.centralsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the upload stats as received from central search.
 */

public class UploadStats {

  @JsonProperty("LastUploadTimestamp")
  private String lastUploadTimestamp;

  public UploadStats() {
  }

  public UploadStats(String lastUploadTimestamp) {
    this.lastUploadTimestamp = lastUploadTimestamp;
  }

  public String getLastUploadTimestamp() {
    return lastUploadTimestamp;
  }

}
