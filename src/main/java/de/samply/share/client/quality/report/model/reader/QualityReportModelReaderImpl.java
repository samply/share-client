package de.samply.share.client.quality.report.model.reader;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.properties.PropertyUtils;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.web.mdrfaces.MdrContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QualityReportModelReaderImpl implements ModelReader {

  private static final String DATAELEMENTGROUP = "dataelementgroup";

  private final String[] mdrGroups;
  private final String[] additionalMdrElements;

  private final String languageCode;


  /**
   * Constructs a quality report model reader.
   */
  public QualityReportModelReaderImpl() {

    languageCode = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LANGUAGE_CODE);
    mdrGroups = PropertyUtils.getListOfProperties(EnumConfiguration.QUALITY_REPORT_MDR_GROUPS);
    additionalMdrElements = PropertyUtils
        .getListOfProperties(EnumConfiguration.QUALITY_REPORT_ADDITIONAL_MDR_DATA_ELEMENTS);

  }


  @Override
  public Model getModel() throws ModelReaderException {

    try {

      return getModelWithoutExceptions();

    } catch (ExecutionException | MdrInvalidResponseException | MdrConnectionException e) {
      throw new ModelReaderException(e);
    }
  }

  private Model getModelWithoutExceptions()
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    MdrClient mdrClient = getMdrClient();

    List<MdrIdAndValidations> mdrIdAndValidations = new ArrayList<>();

    mdrIdAndValidations = addMrdGroups(mdrIdAndValidations, mdrClient);
    mdrIdAndValidations = addAdditionalMrdElements(mdrIdAndValidations, mdrClient);

    Model model = new Model();
    model.setMdrIdAndValidations(mdrIdAndValidations);

    return model;

  }

  private List<MdrIdAndValidations> addMrdGroups(List<MdrIdAndValidations> mdrIdAndValidationsList,
      MdrClient mdrClient)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    for (String mdrGroup : mdrGroups) {

      List<MdrIdDatatype> mdrIdDatatypeList = new ArrayList<>();
      mdrIdDatatypeList = getElementsFromGroupAndSubgroups(mdrIdDatatypeList, mdrGroup, mdrClient);

      List<MdrIdAndValidations> temporalMdrIdAndValidations = getMdrIdAndValidationsList(mdrClient,
          mdrIdDatatypeList);

      mdrIdAndValidationsList.addAll(temporalMdrIdAndValidations);

    }

    return mdrIdAndValidationsList;
  }

  private List<MdrIdAndValidations> addAdditionalMrdElements(
      List<MdrIdAndValidations> mdrIdAndValidationsList, MdrClient mdrClient)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    for (String mdrElement : additionalMdrElements) {

      MdrIdDatatype mdrId = new MdrIdDatatype(mdrElement);
      MdrIdAndValidations mdrIdAndValidations = getMdrIdAndValidations(mdrClient, mdrId);
      mdrIdAndValidationsList.add(mdrIdAndValidations);

    }

    return mdrIdAndValidationsList;
  }


  private MdrClient getMdrClient() {

    return MdrContext.getMdrContext().getMdrClient();

  }

  private List<MdrIdDatatype> getElementsFromGroupAndSubgroups(List<MdrIdDatatype> theList,
      String groupKey, MdrClient mdrClient)
      throws MdrConnectionException, ExecutionException {

    List<Result> resultL = mdrClient.getMembers(groupKey, languageCode);
    for (Result r : resultL) {
      if (r.getType().equalsIgnoreCase(DATAELEMENTGROUP)) {
        theList = getElementsFromGroupAndSubgroups(theList, r.getId(), mdrClient);
      } else {
        theList.add(new MdrIdDatatype(r.getId()));
      }
    }

    return theList;
  }


  private List<MdrIdAndValidations> getMdrIdAndValidationsList(MdrClient mdrClient,
      List<MdrIdDatatype> mdrIds)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    List<MdrIdAndValidations> mdrIdAndValidationsList = new ArrayList<>();

    for (MdrIdDatatype mdrId : mdrIds) {

      MdrIdAndValidations mdrIdAndValidations = getMdrIdAndValidations(mdrClient, mdrId);
      mdrIdAndValidationsList.add(mdrIdAndValidations);
    }

    return mdrIdAndValidationsList;
  }

  private Validations getMdrIdAndValidationsList(MdrClient mdrClient, String mdrId)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {
    return mdrClient.getDataElementValidations(mdrId, languageCode);
  }

  private MdrIdAndValidations getMdrIdAndValidations(MdrClient mdrClient, MdrIdDatatype mdrId)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    Validations validations = getMdrIdAndValidationsList(mdrClient, mdrId.getLatestMdr());
    return new MdrIdAndValidations(mdrId, validations);
  }

}
