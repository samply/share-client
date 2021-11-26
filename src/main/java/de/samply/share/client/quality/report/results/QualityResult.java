package de.samply.share.client.quality.report.results;

import java.util.HashSet;
import java.util.Set;

public class QualityResult {


  private Integer numberOfPatients = 0;
  private boolean isValid = false;
  private final Set<String> patientLocalIds = new HashSet<>();
  private final Set<String> patientDktkIds = new HashSet<>();


  /**
   * Get number of patients.
   *
   * @return number of patients.
   *
   */
  public int getNumberOfPatients() {

    if (numberOfPatients == null) {
      numberOfPatients = Math.max(patientLocalIds.size(), patientDktkIds.size());
    }

    return numberOfPatients;

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
   * Add patient local id.
   *
   * @param patientLocalId patient local id.
   */
  public void addPatientLocalId(String patientLocalId) {

    if (patientLocalId != null) {
      patientLocalIds.add(patientLocalId);
    }

  }

  /**
   * Add set of patient local ids.
   *
   * @param patientLocalIds patient local ids.
   */
  public void addPatientLocalIds(Set<String> patientLocalIds) {

    if (patientLocalIds != null && patientLocalIds.size() > 0) {
      this.patientLocalIds.addAll(patientLocalIds);
      this.numberOfPatients = null;
    }

  }

  /**
   * Add patient global id.
   *
   * @param patientDktkId patient global id.
   */
  public void addPatientDktkId(String patientDktkId) {

    if (patientDktkId != null) {
      patientDktkIds.add(patientDktkId);
      this.numberOfPatients = null;
    }

  }

  /**
   * Add set of patient global ids .
   *
   * @param patientDktkIds patient global ids.
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
