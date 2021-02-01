package de.samply.share.client.quality.report.file.txtcolumn;

import java.util.HashMap;
import java.util.Map;


public class AnonymTxtColumn implements TxtColumn {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private final Map<String, String> values = new HashMap<>();

  public void addElement(String key, String value) {
    values.put(key, value);
  }

  public String getElement(String key) {
    return values.get(key);
  }

  @Override
  public String createColumn() {

    StringBuilder stringBuilder = new StringBuilder();

    for (Map.Entry<String, String> entry : values.entrySet()) {

      stringBuilder.append(entry.getKey());
      stringBuilder.append('=');
      stringBuilder.append(entry.getValue());
      stringBuilder.append(LINE_SEPARATOR);

    }

    return stringBuilder.toString();


  }

  @Override
  public void parseValuesOfColumn(String column) {

    String[] split = column.split(LINE_SEPARATOR);

    for (String line : split) {
      parseValuesOfLine(line);
    }

  }

  private void parseValuesOfLine(String line) {

    if (line != null && line.length() > 0) {

      String[] split = line.split("=");

      if (split.length == 2 && split[1].length() > 0) {

        String key = split[0];
        String value = split[1];

        values.put(key, value);
      }

    }

  }


}
