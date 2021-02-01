package de.samply.share.client.quality.report.results;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QualityResultsImpl implements QualityResults {

  private final Map<MdrIdDatatype, ValueAndResults> results = new HashMap<>();

  @Override
  public QualityResult getResult(MdrIdDatatype mdrId, String value) {

    ValueAndResults valueAndResults = results.get(mdrId);
    return (valueAndResults != null) ? valueAndResults.getResult(value) : null;

  }

  @Override
  public void put(MdrIdDatatype mdrId, String value, QualityResult result) {

    ValueAndResults valueAndResults = getValueAndResults(mdrId);
    valueAndResults.put(value, result);

  }

  private ValueAndResults getValueAndResults(MdrIdDatatype mdrId) {

    ValueAndResults valueAndResults = results.get(mdrId);
    if (valueAndResults == null) {
      valueAndResults = new ValueAndResults();
      results.put(mdrId, valueAndResults);
    }

    return valueAndResults;

  }

  @Override
  public Set<String> getValues(MdrIdDatatype mdrId) {

    ValueAndResults valueAndResults = results.get(mdrId);
    return (valueAndResults != null) ? valueAndResults.getValues() : null;

  }

  @Override
  public Set<MdrIdDatatype> getMdrIds() {
    return results.keySet();
  }

  private QualityResult getQualityResult(MdrIdDatatype mdrId, String value) {

    ValueAndResults valueAndResults = getValueAndResults(mdrId);
    QualityResult result = valueAndResults.getResult(value);

    if (result == null) {
      result = new QualityResult();
      valueAndResults.put(value, result);
    }

    return result;
  }

  @Override
  public void setAsValid(MdrIdDatatype mdrId, String value) {
    QualityResult result = getResult(mdrId, value);
    if (result != null) {
      result.setValid(true);
    }
  }

  @Override
  public void addPatientLocalId(MdrIdDatatype mdrId, String value, String patientLocalId) {

    QualityResult result = getQualityResult(mdrId, value);
    result.addPatientLocalId(patientLocalId);

  }

  @Override
  public void addPatientDktkId(MdrIdDatatype mdrId, String value, String patientDktkId) {

    QualityResult result = getQualityResult(mdrId, value);
    result.addPatientDktkId(patientDktkId);
  }

  @Override
  public void addPatientLocalIds(MdrIdDatatype mdrId, String value, Set<String> patientLocalIds) {

    QualityResult result = getQualityResult(mdrId, value);
    result.addPatientLocalIds(patientLocalIds);

  }

  @Override
  public void addPatientDktkIds(MdrIdDatatype mdrId, String value, Set<String> patientDktkIds) {

    QualityResult result = getQualityResult(mdrId, value);
    result.addPatientDktkIds(patientDktkIds);

  }

  @Override
  public void updateValue(MdrIdDatatype mdrId, String oldValue, String newValue) {

    ValueAndResults valueAndResults = results.get(mdrId);
    if (valueAndResults != null) {
      valueAndResults.updateValue(oldValue, newValue);
    }

  }

  private class ValueAndResults {

    private final Map<String, QualityResult> valueAndResults = new HashMap<>();

    public QualityResult getResult(String value) {
      return valueAndResults.get(value);
    }

    public Set<String> getValues() {

      HashSet<String> results = new HashSet<>();
      Set<String> valueAndResultsKeys = valueAndResults.keySet();
      results.addAll(valueAndResultsKeys);

      return results;

    }

    public void put(String value, QualityResult result) {
      valueAndResults.put(value, result);
    }

    public void updateValue(String oldValue, String newValue) {

      QualityResult result = getResult(oldValue);
      if (result != null) {
        valueAndResults.remove(oldValue);
        put(newValue, result);
      }

    }

  }


}
