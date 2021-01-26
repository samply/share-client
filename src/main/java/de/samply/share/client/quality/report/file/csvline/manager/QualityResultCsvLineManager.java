package de.samply.share.client.quality.report.file.csvline.manager;

import de.samply.share.client.quality.report.file.id.filename.QualityReportFilePattern;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

public interface QualityResultCsvLineManager extends QualityReportFilePattern {

  String createLine(MdrIdDatatype mdrId, String value, QualityResult qualityResult)
      throws QualityResultCsvLineManagerException;

  QualityResults parseLineAndAddToQualityResults(String line, QualityResults qualityResults);

}
