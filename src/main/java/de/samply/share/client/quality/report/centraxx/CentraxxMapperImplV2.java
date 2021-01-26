package de.samply.share.client.quality.report.centraxx;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Set;

public class CentraxxMapperImplV2 implements CentraxxMapper {

  private static final String CXX_VALUES_SEPARATOR = " | ";
  private final MdrMappedElements mdrMappedElements;
  private final FileLoader fileLoader = new FileLoader();
  private final GeneralRehearsalPriorization generalRehearsalPriorization =
      new GeneralRehearsalPriorization();


  /**
   * Todo.
   *
   * @param mdrMappedElements Todo.
   * @throws CentraxxMapperException Todo.
   */
  public CentraxxMapperImplV2(MdrMappedElements mdrMappedElements) throws CentraxxMapperException {

    this.mdrMappedElements = mdrMappedElements;
    loadGeneralRehearsalPriorization();

  }

  @Override
  public String getCentraXxAttribute(MdrIdDatatype mdrId) {

    CxxMappingElement cxxMappingElement = mdrMappedElements.getCxxMappingElement(mdrId);
    return (cxxMappingElement != null) ? cxxMappingElement.getTeilerBaseViewColumn() : null;

  }

  @Override
  public String getCentraXxValue(MdrIdDatatype mdrId, String mdrValue) {

    String result = null;

    CxxMappingElement cxxMappingElement = mdrMappedElements.getCxxMappingElement(mdrId);

    if (cxxMappingElement != null) {

      Set<String> cxxValues = cxxMappingElement.getCxxValues(mdrValue);

      if (cxxValues != null && cxxValues.size() > 0) {

        StringBuilder stringBuilder = new StringBuilder();
        cxxValues.forEach((x) -> stringBuilder.append(x + CXX_VALUES_SEPARATOR));

        int index = stringBuilder.lastIndexOf(CXX_VALUES_SEPARATOR);
        result = stringBuilder.substring(0, index);

      }

    }

    return result;

  }

  @Override
  public String getGeneralRehearsalPriorization(MdrIdDatatype mdrId) {
    return generalRehearsalPriorization.getPriorization(mdrId);
  }

  private void loadGeneralRehearsalPriorization() throws CentraxxMapperException {

    FileLoader.FilenameReader filenameReader = () -> fileLoader.getConfigurationFilename(
        EnumConfiguration.QUALITY_REPORT_GENERAL_REHEARSAL_PRIORITATION_FILE);
    FileLoader.LineLoader lineLoader = (line) -> loadLineOfRehearsalPriorization(line);

    fileLoader.load(filenameReader, lineLoader);

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
