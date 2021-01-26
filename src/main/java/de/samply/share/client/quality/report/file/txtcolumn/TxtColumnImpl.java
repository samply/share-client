package de.samply.share.client.quality.report.file.txtcolumn;

import java.util.Map;
import org.apache.commons.collections.FastHashMap;

public abstract class TxtColumnImpl implements TxtColumn {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private final int maxNumberOfElements;
  private final Map<Integer, String> elements = new FastHashMap();

  public TxtColumnImpl(int maxNumberOfElements) {
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

  protected abstract String getElementTitle(int order);

  protected abstract Integer getElementTitleOrder(String elementTitle);

  @Override
  public String createColumn() {

    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < maxNumberOfElements; i++) {

      stringBuilder.append(getElementTitle(i));
      stringBuilder.append('=');

      String element = getElement(i);
      if (element != null) {
        stringBuilder.append(element);
      }

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

        String title = split[0];
        String value = split[1];

        Integer elementTitleOrder = getElementTitleOrder(title);

        if (elementTitleOrder != null) {
          addElement(elementTitleOrder, value);
        }
      }

    }

  }

}
