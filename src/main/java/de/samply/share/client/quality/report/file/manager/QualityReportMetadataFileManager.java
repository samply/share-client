package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import java.util.List;

public interface QualityReportMetadataFileManager {

  void write(QualityReportMetadata qualityReportMetadata, String fileId)
      throws QualityReportFileManagerException;

  QualityReportMetadata read(String fileId) throws QualityReportFileManagerException;

  List<QualityReportMetadata> readAll() throws QualityReportFileManagerException;

}
