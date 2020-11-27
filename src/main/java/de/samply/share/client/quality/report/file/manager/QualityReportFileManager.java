package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.results.QualityResults;

public interface QualityReportFileManager {

  public void writeFile(QualityResults qualityResults, String fileId)
      throws QualityReportFileManagerException;

  public QualityResults readFile(String fileId) throws QualityReportFileManagerException;

}
