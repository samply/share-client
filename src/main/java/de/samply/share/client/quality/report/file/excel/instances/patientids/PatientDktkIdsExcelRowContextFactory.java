package de.samply.share.client.quality.report.file.excel.instances.patientids;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;

public class PatientDktkIdsExcelRowContextFactory {

  public ExcelRowContext createExcelRowContext(
      AlphabeticallySortedMismatchedQualityResults qualityResults) {
    return new PatientDktkIdsExcelRowContext(qualityResults);
  }

}
