package de.samply.share.client.quality.report.results.operations;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.QualityResultsImpl;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Container;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import java.util.List;
import java.util.Set;

public class QualityResultsAnalyzer {


  /**
   * Analyzes quality result and updates quality results with the new information.
   *
   * @param qualityResults Quality Results.
   * @param queryResult    Quality Result.
   * @return Quality Results updated.
   */
  public QualityResults analyze(QualityResults qualityResults, QueryResult queryResult) {

    if (queryResult != null) {
      for (Patient patient : queryResult.getPatient()) {

        QualityResults temporaryQualityResults = new QualityResultsImpl();
        analyze(patient, temporaryQualityResults);
        qualityResults = updateQualityResults(temporaryQualityResults, qualityResults);

      }
    }
    return qualityResults;
  }

  private void analyze(Patient patient, QualityResults qualityResults) {
    analyze(patient, patient.getAttribute(), patient.getContainer(), qualityResults);
  }

  private void analyze(Patient patient, Container container, QualityResults qualityResults) {
    analyze(patient, container.getAttribute(), container.getContainer(), qualityResults);
  }

  private void analyze(Patient patient, List<Attribute> attributeList,
      List<Container> containerList, QualityResults qualityResults) {

    for (Attribute attribute : attributeList) {
      analyze(patient, attribute, qualityResults);
    }

    for (Container container : containerList) {
      analyze(patient, container, qualityResults);
    }

  }

  private void analyze(Patient patient, Attribute attribute, QualityResults qualityResults) {

    MdrIdDatatype mdrId = new MdrIdDatatype(attribute.getMdrKey());
    String value = attribute.getValue().getValue();

    qualityResults.addPatientLocalId(mdrId, value, patient.getId());
    qualityResults.addPatientDktkId(mdrId, value, patient.getDktkId());

  }


  private QualityResults updateQualityResults(QualityResults temporaryQualityResults,
      QualityResults finalQualityResults) {

    for (MdrIdDatatype mdrId : temporaryQualityResults.getMdrIds()) {

      for (String value : temporaryQualityResults.getValues(mdrId)) {

        QualityResult temporaryQualityResult = temporaryQualityResults.getResult(mdrId, value);
        updateQualityResults(mdrId, value, temporaryQualityResult, finalQualityResults);

      }
    }

    return finalQualityResults;

  }

  private void updateQualityResults(MdrIdDatatype mdrId, String value,
      QualityResult temporaryQualityResult, QualityResults finalQualityResults) {

    Set<String> patientLocalIds = temporaryQualityResult.getPatientLocalIds();
    Set<String> patientDktkIds = temporaryQualityResult.getPatientDktkIds();

    finalQualityResults.addPatientLocalIds(mdrId, value, patientLocalIds);
    finalQualityResults.addPatientDktkIds(mdrId, value, patientDktkIds);


  }

}
