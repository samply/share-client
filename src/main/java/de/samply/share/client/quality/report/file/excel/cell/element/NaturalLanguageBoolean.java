package de.samply.share.client.quality.report.file.excel.cell.element;

public enum NaturalLanguageBoolean {

  EN("yes", "no"),
  DE("ja", "nein"),
  COMPUTER(new Boolean(true).toString(), new Boolean(false).toString());


  private final String yes;
  private final String no;

  NaturalLanguageBoolean(String yes, String no) {
    this.yes = yes;
    this.no = no;
  }

  public String getValue(boolean value) {
    return (value) ? yes : no;
  }

}
