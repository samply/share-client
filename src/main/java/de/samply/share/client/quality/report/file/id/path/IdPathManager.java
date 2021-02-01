package de.samply.share.client.quality.report.file.id.path;

import java.util.List;

public interface IdPathManager {

  String getCsvFilePath(String fileId);

  String getExcelFilePath(String fileId);

  String getMetadataFilePath(String fileId);

  List<String> getAllMetadataFilePaths();

  String getFileId(String filePath);

  String getCurrentQualityReportVersion();


}
