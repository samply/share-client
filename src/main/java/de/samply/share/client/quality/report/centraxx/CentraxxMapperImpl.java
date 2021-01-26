package de.samply.share.client.quality.report.centraxx;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.Map;

public class CentraxxMapperImpl implements CentraxxMapper {

  private final Map<String, String> centraXxDataElements = new HashMap<>();
  private final Map<AttributeValueKey, String> centraXxAttributeValues = new HashMap<>();
  private final GeneralRehearsalPriorization generalRehearsalPriorization =
      new GeneralRehearsalPriorization();
  private final FileLoader fileLoader = new FileLoader();

  /**
   * Todo.
   *
   * @throws CentraxxMapperException Todo.
   */
  public CentraxxMapperImpl() throws CentraxxMapperException {

    loadCentraXxDataelements();
    loadCentraXxValues();
    loadGeneralRehearsalPriorization();

  }

  @Override
  public String getCentraXxAttribute(MdrIdDatatype mdrId) {
    return centraXxDataElements.get(mdrId.toString());
  }

  @Override
  public String getCentraXxValue(MdrIdDatatype mdrId, String mdrValue) {

    AttributeValueKey attributeValueKey = new AttributeValueKey(mdrId, mdrValue);
    return centraXxAttributeValues.get(attributeValueKey);

  }

  @Override
  public String getGeneralRehearsalPriorization(MdrIdDatatype mdrId) {
    return generalRehearsalPriorization.getPriorization(mdrId);
  }


  private void loadCentraXxDataelements() throws CentraxxMapperException {

    FileLoader.FilenameReader filenameReader = () -> fileLoader
        .getConfigurationFilename(EnumConfiguration.QUALITY_REPORT_CENTRAXX_DATAELEMENTS_FILE);
    FileLoader.LineLoader lineLoader = (line) -> loadLineOfCentraXxDataElements(line);

    fileLoader.load(filenameReader, lineLoader);

  }

  private void loadCentraXxValues() throws CentraxxMapperException {

    FileLoader.FilenameReader filenameReader = () -> fileLoader
        .getConfigurationFilename(EnumConfiguration.QUALITY_REPORT_CENTRAXX_VALUES_FILE);
    FileLoader.LineLoader lineLoader = (line) -> loadLineOfCentraXxValues(line);

    fileLoader.load(filenameReader, lineLoader);

  }

  private void loadGeneralRehearsalPriorization() throws CentraxxMapperException {

    FileLoader.FilenameReader filenameReader = () -> fileLoader.getConfigurationFilename(
        EnumConfiguration.QUALITY_REPORT_GENERAL_REHEARSAL_PRIORITATION_FILE);
    FileLoader.LineLoader lineLoader = (line) -> loadLineOfRehearsalPriorization(line);

    fileLoader.load(filenameReader, lineLoader);

  }

  private void loadLineOfCentraXxDataElements(String line) {

    String[] split = line.split("\t");

    if (split.length >= 2) {

      String mdrId = split[0];
      String dataElement = split[1];

      putDataElement(mdrId, dataElement);

    }
  }

  private void putDataElement(String mdrId, String dataElement) {

    String oldDataElement = centraXxDataElements.get(mdrId);
    if (oldDataElement != null) {
      dataElement = addElement(oldDataElement, dataElement);
    }

    centraXxDataElements.put(mdrId, dataElement);
  }

  private String addElement(String oldElement, String newElement) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(oldElement);
    stringBuilder.append(" | ");
    stringBuilder.append(newElement);

    return stringBuilder.toString();

  }

  private void loadLineOfCentraXxValues(String line) {

    String[] split = line.split("\t");

    if (split.length >= 3) {

      String mdrId = split[0];
      String mdrValue = split[1];
      String centraXxValue = split[2];

      AttributeValueKey attributeValueKey = new AttributeValueKey(mdrId, mdrValue);

      putValue(attributeValueKey, centraXxValue);

    }

  }

  private void putValue(AttributeValueKey attributeValueKey, String value) {

    String oldValue = centraXxAttributeValues.get(attributeValueKey);
    if (oldValue != null) {
      value = addElement(oldValue, value);
    }

    centraXxAttributeValues.put(attributeValueKey, value);
  }

  private void loadLineOfRehearsalPriorization(String line) {

    String[] split = line.split(";");

    if (split.length >= 2) {

      String mdrIdKey = split[0];
      String priorization = split[1];

      generalRehearsalPriorization.setPriorization(mdrIdKey, priorization);

    }
  }


}
