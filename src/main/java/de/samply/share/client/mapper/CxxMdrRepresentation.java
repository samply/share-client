package de.samply.share.client.mapper;


public class CxxMdrRepresentation {

  Integer oid;
  String entitySource = "CENTRAXX";
  Integer creator = 1;
  Integer mdrMappingOid;
  String mdrPermittedValue;

  public Integer getOid() {
    return oid;
  }

  public void setOid(Integer oid) {
    this.oid = oid;
  }

  public String getEntitySource() {
    return entitySource;
  }

  public void setEntitySource(String entitySource) {
    this.entitySource = entitySource;
  }

  public Integer getCreator() {
    return creator;
  }

  public void setCreator(Integer creator) {
    this.creator = creator;
  }

  public Integer getMdrMappingOid() {
    return mdrMappingOid;
  }

  public void setMdrMappingOid(Integer mdrMappingOid) {
    this.mdrMappingOid = mdrMappingOid;
  }

  public String getMdrPermittedValue() {
    return mdrPermittedValue;
  }

  public void setMdrPermittedValue(String mdrPermittedValue) {
    this.mdrPermittedValue = mdrPermittedValue;
  }

}
