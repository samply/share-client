package de.samply.share.client.model;

/**
 * Represents the information, the id manager provides when the info resource is read.
 */
public class IdManagerInfo {

  private String dist;
  private String version;

  public String getDist() {
    return dist;
  }

  public void setDist(String dist) {
    this.dist = dist;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
