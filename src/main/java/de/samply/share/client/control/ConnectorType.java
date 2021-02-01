package de.samply.share.client.control;

public enum ConnectorType {
  DKTK("DKTK.Teiler"), SAMPLY("Connector"), UNKNOWN("Samply.Share");

  private final String name;

  ConnectorType(String name) {
    this.name = name;
  }

  /**
   * Return the ConnectorType withe the connector name depends on the project name.
   *
   * @param name the project name
   * @return ConnectorType
   */
  public static ConnectorType from(String name) {
    if (name.equalsIgnoreCase("dktk")) {
      return DKTK;
    }

    if (name.equalsIgnoreCase("samply")) {
      return SAMPLY;
    }

    return UNKNOWN;
  }

  public String getDisplayName() {
    return name;
  }
}
