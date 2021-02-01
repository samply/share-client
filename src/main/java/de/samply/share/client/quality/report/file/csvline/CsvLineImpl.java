package de.samply.share.client.quality.report.file.csvline;

import java.util.Map;
import org.apache.commons.collections.FastHashMap;

public class CsvLineImpl implements CsvLine {


  private char csvFileSeparator = '\t';
  private final int maxNumberOfElements;

  private final Map<Integer, String> elements = new FastHashMap();


  public CsvLineImpl(int maxNumberOfElements) {
    this.maxNumberOfElements = maxNumberOfElements;
  }

  protected void addElement(int order, String element) {
    if (element != null && order < maxNumberOfElements) {
      elements.put(order, element);
    }
  }

  protected String getElement(int order) {
    return (order < maxNumberOfElements) ? elements.get(order) : null;
  }

  @Override
  public String createLine() {

    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < maxNumberOfElements; i++) {

      String element = elements.get(i);
      if (element != null) {
        stringBuilder.append(element);
      }
      if (i + 1 < maxNumberOfElements) {
        stringBuilder.append(csvFileSeparator);
      }

    }

    stringBuilder.append('\n');

    return stringBuilder.toString();

  }

  @Override
  public void parseValuesOfLine(String line) {

    if (line != null) {

      line = line.substring(0, line.length() - 1); // ignore new line character
      String[] splitLine = line.split(String.valueOf(csvFileSeparator));

      for (int i = 0; i < maxNumberOfElements; i++) {

        if (i < splitLine.length) {

          String element = splitLine[i];
          if (element != null && element.length() > 0) {
            elements.put(i, element);
          }

        }

      }

    }
  }

  public void setCsvFileSeparator(char csvFileSeparator) {
    this.csvFileSeparator = csvFileSeparator;
  }
}
