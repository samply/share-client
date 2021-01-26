package de.samply.share.client.quality.report.properties;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.util.ArrayList;
import java.util.List;

public class PropertyUtils {

  private static final String[] possibleSeparators = {",", ";"};

  /**
   * Todo.
   *
   * @param enumConfiguration Todo.
   * @return Todo.
   */
  public static String[] getListOfProperties(EnumConfiguration enumConfiguration) {

    String configurationElementValue = ConfigurationUtil
        .getConfigurationElementValue(enumConfiguration);
    return getListOfProperties(configurationElementValue);

  }

  private static String[] getListOfProperties(String propertiesList) {

    List<String> properties = new ArrayList<>();

    if (propertiesList != null && propertiesList.length() > 0) {

      String separator = getSeparator(propertiesList);
      if (separator == null) {

        properties.add(propertiesList);

      } else {

        String[] splittedProperties = propertiesList.split(separator);

        for (String property : splittedProperties) {
          property = cleanProperty(property);
          properties.add(property);
        }

      }
    }

    return properties.toArray(new String[properties.size()]);

  }

  private static String cleanProperty(String property) {
    return property.replaceAll("\\s+", "");
  }

  private static String getSeparator(String list) {

    for (String separator : possibleSeparators) {
      if (list.contains(separator)) {
        return separator;
      }
    }

    return null;
  }
}
