package de.samply.share.client.quality.report.model;

import de.samply.share.client.quality.report.MdrIdAndValidations;
import java.util.List;


public class Model {

  private List<MdrIdAndValidations> mdrIdAndValidations;


  public List<MdrIdAndValidations> getMdrIdAndValidations() {
    return mdrIdAndValidations;
  }

  public void setMdrIdAndValidations(List<MdrIdAndValidations> mdrIdAndValidations) {
    this.mdrIdAndValidations = mdrIdAndValidations;
  }

}
