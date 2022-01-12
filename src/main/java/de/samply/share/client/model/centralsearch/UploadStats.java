package de.samply.share.client.model.centralsearch;

/**
 * Represents the upload stats as received from central search.
 */

public class UploadStats {


  private String lastUploadTimestamp;

  public UploadStats(String lastUploadTimestamp) {
    this.lastUploadTimestamp = lastUploadTimestamp;
  }

  public String getLastUploadTimestamp() {
    return lastUploadTimestamp;
  }

}
