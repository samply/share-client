package de.samply.share.client.quality.report.results;

import java.util.HashSet;
import java.util.Set;

public class QualityResult {


  private int numberOfPatients = 0;
  private boolean isValid = false;
  private final Set<String> patientLocalIds = new HashSet<>();
  private final Set<String> patientDktkIds = new HashSet<>();


  public int getNumberOfPatients() {
    return (numberOfPatients > 0) ? numberOfPatients
        : Math.max(patientLocalIds.size(), patientDktkIds.size());
  }

  public void setNumberOfPatients(int numberOfPatients) {
    this.numberOfPatients = numberOfPatients;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }


  /**
   * Todo.
   *
   * @param patientLocalId Todo.
   */
  public void addPatientLocalId(String patientLocalId) {

    if (patientLocalId != null) {
      patientLocalIds.add(patientLocalId);
    }

  }

  /**
   * Todo.
   *
   * @param patientLocalIds Todo.
   */
  public void addPatientLocalIds(Set<String> patientLocalIds) {

    if (patientLocalIds != null && patientLocalIds.size() > 0) {
      this.patientLocalIds.addAll(patientLocalIds);
    }

  }

  /**
   * Todo.
   *
   * @param patientDktkId Todo.
   */
  public void addPatientDktkId(String patientDktkId) {

    if (patientDktkId != null) {
      patientDktkIds.add(patientDktkId);
    }

  }

  /**
   * Todo.
   *
   * @param patientDktkIds Todo.
   */
  public void addPatientDktkIds(Set<String> patientDktkIds) {

    if (patientDktkIds != null && patientDktkIds.size() > 0) {
      this.patientDktkIds.addAll(patientDktkIds);
    }

  }


  public Set<String> getPatientLocalIds() {
    return patientLocalIds;
  }

  public Set<String> getPatientDktkIds() {
    return patientDktkIds;
  }


}
