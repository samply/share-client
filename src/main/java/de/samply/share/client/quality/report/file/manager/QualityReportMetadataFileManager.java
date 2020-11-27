package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import java.util.List;

public interface QualityReportMetadataFileManager {

  public void write(QualityReportMetadata qualityReportMetadata, String fileId)
      throws QualityReportFileManagerException;

  public QualityReportMetadata read(String fileId) throws QualityReportFileManagerException;

  public List<QualityReportMetadata> readAll() throws QualityReportFileManagerException;

}
