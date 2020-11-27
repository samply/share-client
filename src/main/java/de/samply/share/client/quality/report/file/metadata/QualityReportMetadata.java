package de.samply.share.client.quality.report.file.metadata;

import java.util.Date;

public class QualityReportMetadata {

  private Date creationTimestamp;
  private String fileId;
  private String sqlMappingVersion;
  private String qualityReportVersion = "001";

  public Date getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(Date creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getSqlMappingVersion() {
    return sqlMappingVersion;
  }

  public void setSqlMappingVersion(String sqlMappingVersion) {
    this.sqlMappingVersion = sqlMappingVersion;
  }

  public String getQualityReportVersion() {
    return qualityReportVersion;
  }

  public void setQualityReportVersion(String qualityReportVersion) {
    this.qualityReportVersion = qualityReportVersion;
  }
}
