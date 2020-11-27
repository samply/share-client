package de.samply.share.client.quality.report.results;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Set;

public interface QualityResults {

  public QualityResult getResult(MdrIdDatatype mdrId, String value);

  public void put(MdrIdDatatype mdrId, String value, QualityResult result);

  public Set<String> getValues(MdrIdDatatype mdrId);

  public Set<MdrIdDatatype> getMdrIds();

  public void setAsValid(MdrIdDatatype mdrId, String value);

  public void addPatientLocalId(MdrIdDatatype mdrId, String value, String patientLocalId);

  public void addPatientDktkId(MdrIdDatatype mdrId, String value, String patientDktkId);

  public void addPatientLocalIds(MdrIdDatatype mdrId, String value, Set<String> patientLocalIds);

  public void addPatientDktkIds(MdrIdDatatype mdrId, String value, Set<String> patientDktkIds);

  public void updateValue(MdrIdDatatype mdrId, String oldValue, String newValue);

}
