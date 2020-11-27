package de.samply.share.client.mapper;


public class TeilerBaseViewColumn {

  String cxxTableAlias;
  String cxxTableColumn;
  String cxxFunction;
  String tbvColumnName;

  public String getCxxTableAlias() {
    return cxxTableAlias;
  }

  public void setCxxTableAlias(String cxxTableAlias) {
    this.cxxTableAlias = cxxTableAlias;
  }

  public String getCxxTableColumn() {
    return cxxTableColumn;
  }

  public void setCxxTableColumn(String cxxTableColumn) {
    this.cxxTableColumn = cxxTableColumn;
  }

  public String getCxxFunction() {
    return cxxFunction;
  }

  public void setCxxFunction(String cxxFunction) {
    this.cxxFunction = cxxFunction;
  }

  public String getTbvColumnName() {
    return tbvColumnName;
  }

  public void setTbvColumnName(String tbvColumnName) {
    this.tbvColumnName = tbvColumnName;
  }

}
