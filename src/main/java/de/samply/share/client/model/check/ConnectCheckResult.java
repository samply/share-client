package de.samply.share.client.model.check;

public class ConnectCheckResult {

  private boolean reachable;
  private String name;
  private String version;

  public ConnectCheckResult() {
  }

  /**
   * Set the values for the component which getting checked.
   *
   * @param reachable if the component is reachable
   * @param name      name of the component
   * @param version   version of the component
   */
  public ConnectCheckResult(boolean reachable, String name, String version) {
    this.reachable = reachable;
    this.name = name;
    this.version = version;
  }

  public boolean isReachable() {
    return reachable;
  }

  public void setReachable(boolean reachable) {
    this.reachable = reachable;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
