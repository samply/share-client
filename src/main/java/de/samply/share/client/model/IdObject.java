package de.samply.share.client.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * An ID Object, as needed to request export ids from the id manager.
 */
public class IdObject {

  @JsonProperty("idType")
  private final String idType;
  @JsonProperty("idString")
  private final String idString;

  public IdObject(String idType, String idString) {
    this.idType = idType;
    this.idString = idString;
  }

  public String getIdType() {
    return idType;
  }

  public String getIdString() {
    return idString;
  }

  @Override
  public String toString() {
    return "IdObject [idType=" + idType + ", idString=" + idString + "]";
  }
}
