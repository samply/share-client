package de.samply.share.client.util.connector.centraxx;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CxxMappingElement {

  private final Map<String, Set<String>> mdrValueCxxValueMap = new HashMap<>();
  private MdrIdDatatype mdrId;
  private String mdrName;
  private String teilerBaseViewColumn;

  public MdrIdDatatype getMdrId() {
    return mdrId;
  }

  public void setMdrId(MdrIdDatatype mdrIdDatatype) {
    this.mdrId = mdrIdDatatype;
  }

  public String getMdrName() {
    return mdrName;
  }

  public void setMdrName(String mdrName) {
    this.mdrName = mdrName;
  }

  public String getTeilerBaseViewColumn() {
    return teilerBaseViewColumn;
  }

  public void setTeilerBaseViewColumn(String teilerBaseViewColumn) {
    this.teilerBaseViewColumn = teilerBaseViewColumn;
  }


  public Set<String> getCxxValues(String mdrValue) {
    return mdrValueCxxValueMap.get(mdrValue);
  }

  /**
   * Add mapping of attribute values between mdr data element and cxx data element.
   *
   * @param mdrValue mdr attribute value.
   * @param cxxValue centraxx attribute value.
   */
  public void addValue(String mdrValue, String cxxValue) {

    Set<String> cxxValues = mdrValueCxxValueMap.get(mdrValue);

    if (cxxValues == null) {

      cxxValues = new HashSet<>();
      mdrValueCxxValueMap.put(mdrValue, cxxValues);

    }

    cxxValues.add(cxxValue);

  }

}
