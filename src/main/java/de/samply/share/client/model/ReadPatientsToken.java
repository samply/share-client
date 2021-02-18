package de.samply.share.client.model;

import java.util.ArrayList;
import java.util.List;

public class ReadPatientsToken {

  private String id;
  private String type;
  private Integer allowedUses;
  private Integer remainingUses;
  private TokenData data;
  private String uri;

  public static class TokenData {

    public List<ID> searchIds = new ArrayList<>();
    public List<String> resultIds = new ArrayList<>();

    public List<ID> getSearchIds() {
      return searchIds;
    }

    public void setSearchIds(List<ID> searchIds) {
      this.searchIds = searchIds;
    }

    public List<String> getResultIds() {
      return resultIds;
    }

    public void setResultIds(List<String> resultIds) {
      this.resultIds = resultIds;
    }
  }

  public static class ID {

    String idType;
    String idString;

    public String getIdType() {
      return idType;
    }

    public void setIdType(String idType) {
      this.idType = idType;
    }

    public String getIdString() {
      return idString;
    }

    public void setIdString(String idString) {
      this.idString = idString;
    }
  }



  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getAllowedUses() {
    return allowedUses;
  }

  public void setAllowedUses(Integer allowedUses) {
    this.allowedUses = allowedUses;
  }

  public Integer getRemainingUses() {
    return remainingUses;
  }

  public void setRemainingUses(Integer remainingUses) {
    this.remainingUses = remainingUses;
  }

  public TokenData getData() {
    return data;
  }

  public void setData(TokenData data) {
    this.data = data;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}
