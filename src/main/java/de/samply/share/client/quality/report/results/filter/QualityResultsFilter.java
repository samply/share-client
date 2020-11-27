package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Set;

public class QualityResultsFilter implements QualityResults {

  protected QualityResults qualityResults;


  public QualityResultsFilter(QualityResults qualityResults) {
    this.qualityResults = qualityResults;
  }

  @Override
  public QualityResult getResult(MdrIdDatatype mdrId, String value) {
    return qualityResults.getResult(mdrId, value);
  }

  @Override
  public void put(MdrIdDatatype mdrId, String value, QualityResult result) {
    qualityResults.put(mdrId, value, result);
  }

  @Override
  public Set<String> getValues(MdrIdDatatype mdrId) {
    return qualityResults.getValues(mdrId);
  }

  @Override
  public Set<MdrIdDatatype> getMdrIds() {
    return qualityResults.getMdrIds();
  }

  @Override
  public void setAsValid(MdrIdDatatype mdrId, String value) {
    qualityResults.setAsValid(mdrId, value);
  }

  @Override
  public void addPatientLocalId(MdrIdDatatype mdrId, String value, String patientLocalId) {
    qualityResults.addPatientLocalId(mdrId, value, patientLocalId);
  }

  @Override
  public void addPatientDktkId(MdrIdDatatype mdrId, String value, String patientDktkId) {
    qualityResults.addPatientDktkId(mdrId, value, patientDktkId);
  }

  @Override
  public void addPatientLocalIds(MdrIdDatatype mdrId, String value, Set<String> patientLocalIds) {
    qualityResults.addPatientLocalIds(mdrId, value, patientLocalIds);
  }

  @Override
  public void addPatientDktkIds(MdrIdDatatype mdrId, String value, Set<String> patientDktkIds) {
    qualityResults.addPatientDktkIds(mdrId, value, patientDktkIds);
  }

  @Override
  public void updateValue(MdrIdDatatype mdrId, String oldValue, String newValue) {
    qualityResults.updateValue(mdrId, oldValue, newValue);
  }

}
