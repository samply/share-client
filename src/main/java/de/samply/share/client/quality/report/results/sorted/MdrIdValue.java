package de.samply.share.client.quality.report.results.sorted;

import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MdrIdValue {

  private final MdrIdDatatype mdrId;
  private final String value;

  public MdrIdValue(MdrIdDatatype mdrId, String value) {
    this.mdrId = mdrId;
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if (obj == null || !(obj instanceof MdrIdValue)) {
      return false;
    }

    MdrIdValue mdrIdValue2 = (MdrIdValue) obj;

    if (mdrIdValue2.getMdrId() == null || mdrIdValue2.getValue() == null) {
      return false;
    }

    return mdrId.equals(mdrIdValue2.getMdrId()) && value.equals(mdrIdValue2.getValue());

  }

  @Override
  public int hashCode() {

    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

    hashCodeBuilder.append(mdrId.toString());
    hashCodeBuilder.append(value);

    return hashCodeBuilder.toHashCode();

  }

  public MdrIdDatatype getMdrId() {
    return mdrId;
  }

  public String getValue() {
    return value;
  }

}
