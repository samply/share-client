package de.samply.share.client.quality.report.results;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Set;

public interface QualityResults {

  QualityResult getResult(MdrIdDatatype mdrId, String value);

  void put(MdrIdDatatype mdrId, String value, QualityResult result);

  Set<String> getValues(MdrIdDatatype mdrId);

  Set<MdrIdDatatype> getMdrIds();

  void setAsValid(MdrIdDatatype mdrId, String value);

  void addPatientLocalId(MdrIdDatatype mdrId, String value, String patientLocalId);

  void addPatientDktkId(MdrIdDatatype mdrId, String value, String patientDktkId);

  void addPatientLocalIds(MdrIdDatatype mdrId, String value, Set<String> patientLocalIds);

  void addPatientDktkIds(MdrIdDatatype mdrId, String value, Set<String> patientDktkIds);

  void updateValue(MdrIdDatatype mdrId, String oldValue, String newValue);

}
