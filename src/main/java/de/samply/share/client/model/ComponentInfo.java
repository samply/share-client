package de.samply.share.client.model;

/**
 * Represents the information, the id manager provides when the info resource is read.
 */
public class ComponentInfo {

  private String distname;
  private String version;

  public String getDistname() {
    return distname;
  }

  public void setDistname(String dist) {
    this.distname = dist;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
