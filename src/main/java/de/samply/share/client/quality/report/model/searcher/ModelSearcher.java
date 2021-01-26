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
   * Todo.
   *
   * @param model Todo.
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
   * Todo.
   *
   * @param mdrId Todo.
   * @return Todo.
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
