package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.results.QualityResult;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ValidValueFilterQualityResults {

  private final Set<String> validValues = new HashSet<>();
  private final QualityResult validQualityResult = new QualityResult();
  private final Map<String, QualityResult> invalidQualityResults = new HashMap<>();

  /**
   * Todo.
   *
   * @param value         Todo.
   * @param qualityResult Todo.
   */
  public void addValueAndQualityResult(String value, QualityResult qualityResult) {

    if (qualityResult.isValid()) {
      addValidValueAndQualityResult(value, qualityResult);
    } else {
      addInvalidValueAndQualityResult(value, qualityResult);
    }

  }

  private void addValidValueAndQualityResult(String value, QualityResult qualityResult) {

    validValues.add(value);

    validQualityResult.setValid(true);

    //int numberOfPatients = validQualityResult.getNumberOfPatients();
    //numberOfPatients += qualityResult.getNumberOfPatients();
    //validQualityResult.setNumberOfPatients(numberOfPatients);
    validQualityResult.getPatientLocalIds().addAll(qualityResult.getPatientLocalIds());
    validQualityResult.getPatientDktkIds().addAll(qualityResult.getPatientDktkIds());

  }

  private void addInvalidValueAndQualityResult(String value, QualityResult qualityResult) {
    invalidQualityResults.put(value, qualityResult);
  }

  public QualityResult getResult(String value) {
    return (validValues.contains(value)) ? validQualityResult : invalidQualityResults.get(value);
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public Set<String> getValues() {

    Set<String> values = new HashSet<>(invalidQualityResults.keySet());
    if (validValues.size() > 0) {

      Iterator<String> iterator = validValues.iterator();
      String value = iterator.next();

      while (value.length() == 0 && iterator.hasNext()) {
        value = iterator.next();
      }

      values.add(value);
    }

    return values;

  }

}
