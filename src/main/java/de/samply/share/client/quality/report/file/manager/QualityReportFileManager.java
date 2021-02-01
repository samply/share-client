package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.results.QualityResults;

public interface QualityReportFileManager {

  void writeFile(QualityResults qualityResults, String fileId)
      throws QualityReportFileManagerException;

  QualityResults readFile(String fileId) throws QualityReportFileManagerException;

}
