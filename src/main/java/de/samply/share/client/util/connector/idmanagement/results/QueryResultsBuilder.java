package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResultsBuilder {

  private int pageSize = -1; // pageSize will be ignored if it is not set
  private final Map<String, Patient> patientIdPatient = new HashMap<>();


  /**
   * Todo David.
   * @param queryResult Todo David
   * @throws QueryResultsBuilderException QueryResultsBuilderException
   */
  public void addQueryResult(QueryResult queryResult) throws QueryResultsBuilderException {

    for (Patient patient : queryResult.getPatient()) {
      addPatient(patient);
    }

  }

  /**
   * Todo David.
   * @param patient Todo David
   * @throws QueryResultsBuilderException QueryResultsBuilderException
   */
  public void addPatient(Patient patient) throws QueryResultsBuilderException {

    String patientId = getPatientId(patient);
    Patient currentPatient = patientIdPatient.get(patientId);

    if (currentPatient == null) {
      patientIdPatient.put(patientId, patient);
    } else {
      mergePatient(currentPatient, patient);
    }

  }

  /**
   * Todo David.
   * @return Todo David
   */
  public QueryResult getNextQueryResult() {

    QueryResult queryResult = null;

    List<String> patientIds = new ArrayList<>(patientIdPatient.keySet());
    if (patientIds.size() > 0) {

      queryResult = new QueryResult();
      List<Patient> patients = queryResult.getPatient();

      int counter = pageSize;
      for (String patientId : patientIds) {

        if (counter == 0) {
          break;
        } else {
          counter--;
          Patient patient = patientIdPatient.get(patientId);
          patients.add(patient);
          patientIdPatient.remove(patientId);

        }

      }

    }

    return queryResult;

  }

  private void mergePatient(Patient currentPatient, Patient newPatient)
      throws QueryResultsBuilderException {
    try {
      mergePatient_WithoutManagementException(currentPatient, newPatient);
    } catch (QueryResultParserException e) {
      throw new QueryResultsBuilderException(e);
    }
  }

  private void mergePatient_WithoutManagementException(Patient currentPatient, Patient newPatient)
      throws QueryResultParserException {

    PatientMerger patientMerger = new PatientMerger();
    patientMerger.merge(currentPatient, newPatient);

  }

  private String getPatientId(Patient patient) {
    return patient.getId();
  }


  /**
   * Todo David.
   * @param pageSize Todo David
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }


}
