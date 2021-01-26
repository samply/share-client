package de.samply.share.client.model.centralsearch;

/**
 * Define upper and lower bounds for an upload.
 */
public class DateRestriction {

  private String lastUpload;
  private String serverTime;

  public String getLastUpload() {
    return lastUpload;
  }

  public void setLastUpload(String lastUpload) {
    this.lastUpload = lastUpload;
  }

  public String getServerTime() {
    return serverTime;
  }

  public void setServerTime(String serverTime) {
    this.serverTime = serverTime;
  }
}
