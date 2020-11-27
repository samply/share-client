package de.samply.share.client.quality.report.file.excel.instances.patientids;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import java.util.Collection;

public class PatientLocalIdsExcelRowContext extends PatientIdsExcelRowContext {

  public PatientLocalIdsExcelRowContext(
      AlphabeticallySortedMismatchedQualityResults qualityResults) {
    super(qualityResults);
  }

  @Override
  protected Collection<String> getPatientIds(QualityResult qualityResult) {
    return qualityResult.getPatientLocalIds();
  }

}
