package de.samply.share.client.quality.report.model.searcher;

import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelSearcher {

  Map<String, Validations> mdrIdAndValidationsMap = new HashMap<>();

  /**
   * Encapsulates a model, that consists of a list of mdr ids and its validations,
   * ans offers search methods like getValidation per mdr id or get centraxx key of
   * an mdr id.
   *
   * @param model model that consists of a list of mdr ids and validations.
   */
  public ModelSearcher(Model model) {

    List<MdrIdAndValidations> mdrIdAndValidationsList = model.getMdrIdAndValidations();

    for (MdrIdAndValidations mdrIdAndValidations : mdrIdAndValidationsList) {
      String key = getKey(mdrIdAndValidations.getMdrId());
      if (key != null) {
        mdrIdAndValidationsMap.put(key, mdrIdAndValidations.getValidations());
      }
    }

  }

  /**
   * Get validations of an mdr id.
   *
   * @param mdrId mdr id.
   * @return validations of the mdr id.
   */
  public Validations getValidations(MdrIdDatatype mdrId) {

    String key = getKey(mdrId);
    return mdrIdAndValidationsMap.get(key);

  }

  // Only the most recently version of the MDR element will be considered
  private String getKey(MdrIdDatatype mdrId) {
    return (mdrId == null) ? null : mdrId.getLatestCentraxx();
  }

}
