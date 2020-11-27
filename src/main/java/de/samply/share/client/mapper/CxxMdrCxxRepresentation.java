package de.samply.share.client.mapper;


public class CxxMdrCxxRepresentation {

  Integer oid;
  String entitySource = "CENTRAXX";
  Integer creator = 1;
  Integer mdrRepresentationOid;
  String cxxClassName;
  String cxxValueName;

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

  public Integer getMdrRepresentationOid() {
    return mdrRepresentationOid;
  }

  public void setMdrRepresentationOid(Integer mdrRepresentationOid) {
    this.mdrRepresentationOid = mdrRepresentationOid;
  }

  public String getCxxClassName() {
    return cxxClassName;
  }

  public void setCxxClassName(String cxxClassName) {
    this.cxxClassName = cxxClassName;
  }

  public String getCxxValueName() {
    return cxxValueName;
  }

  public void setCxxValueName(String cxxValueName) {
    this.cxxValueName = cxxValueName;
  }

}
