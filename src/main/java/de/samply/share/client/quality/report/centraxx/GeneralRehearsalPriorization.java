package de.samply.share.client.quality.report.centraxx;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.Map;

public class GeneralRehearsalPriorization {

  private final Map<String, String> mdrIdKeyPriorizationMap = new HashMap<>();

  private String getMdrIdKey(MdrIdDatatype mdrId) {
    return (mdrId != null) ? mdrId.getNamespace() + ':' + mdrId.getId() : null;
  }

  /**
   * Gets the priority of an mdr id.
   *
   * @param mdrId mdr id in question.
   * @return priority of the mdr id.
   */
  public String getPriorization(MdrIdDatatype mdrId) {

    String mdrIdKey = getMdrIdKey(mdrId);
    return mdrIdKeyPriorizationMap.get(mdrIdKey);

  }

  public void setPriorization(String mdrIdKey, String priorization) {
    mdrIdKeyPriorizationMap.put(mdrIdKey, priorization);
  }

}
