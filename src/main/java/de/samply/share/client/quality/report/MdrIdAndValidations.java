package de.samply.share.client.quality.report;

import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.common.utils.MdrIdDatatype;

public class MdrIdAndValidations {

  private MdrIdDatatype mdrId;
  private Validations validations;


  public MdrIdAndValidations(MdrIdDatatype mdrId, Validations validations) {
    this.mdrId = mdrId;
    this.validations = validations;
  }

  public MdrIdDatatype getMdrId() {
    return mdrId;
  }

  public void setMdrId(MdrIdDatatype mdrId) {
    this.mdrId = mdrId;
  }

  public Validations getValidations() {
    return validations;
  }

  public void setValidations(Validations validations) {
    this.validations = validations;
  }

}
