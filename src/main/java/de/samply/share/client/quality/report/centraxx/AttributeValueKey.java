package de.samply.share.client.quality.report.centraxx;

import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class AttributeValueKey {

  private final String mdrId;
  private final String value;


  public AttributeValueKey(String mdrId, String value) {
    this.mdrId = mdrId;
    this.value = value;
  }

  public AttributeValueKey(MdrIdDatatype mdrId, String value) {
    this.value = value;
    this.mdrId = mdrId.toString();
  }

  @Override
  public int hashCode() {

    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.append(mdrId);
    hashCodeBuilder.append(value);

    return hashCodeBuilder.toHashCode();

  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null || !(obj instanceof AttributeValueKey)) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    AttributeValueKey attributeValueKey = (AttributeValueKey) obj;

    EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(mdrId, attributeValueKey.getMdrId());
    equalsBuilder.append(value, attributeValueKey.getValue());

    return equalsBuilder.isEquals();

  }

  public String getMdrId() {
    return mdrId;
  }

  public String getValue() {
    return value;
  }
}
