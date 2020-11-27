package de.samply.share.client.quality.report.file.id.path;

import java.util.List;

public interface IdPathManager {

  public String getCsvFilePath(String fileId);

  public String getExcelFilePath(String fileId);

  public String getMetadataFilePath(String fileId);

  public List<String> getAllMetadataFilePaths();

  public String getFileId(String filePath);

  public String getCurrentQualityReportVersion();


}
