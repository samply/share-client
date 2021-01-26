package de.samply.share.client.quality.report.file.csvline;

public interface CsvLine {

  String createLine();

  void parseValuesOfLine(String line);

}
