package de.samply.share.client.quality.report.faces;

import java.util.List;

public interface QualityReportFileInfoManager {

  public List<QualityReportFileInfo> getQualityReportFiles()
      throws QualityReportFileInfoManagerException;

}
