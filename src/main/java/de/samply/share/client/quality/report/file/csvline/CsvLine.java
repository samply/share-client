package de.samply.share.client.quality.report.file.csvline;

public interface CsvLine {

  public String createLine();

  public void parseValuesOfLine(String line);

}
