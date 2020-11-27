package de.samply.share.client.quality.report.file.metadata.txtcolumn;

import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import java.util.Date;

public class MetadataTxtColumnManager001 implements MetadataTxtColumnManager {


  @Override
  public String createColumn(QualityReportMetadata qualityReportMetadata) {

    MetadataTxtColumn metadataTxtColumn = new MetadataTxtColumn();

    Date creationTimestamp = qualityReportMetadata.getCreationTimestamp();
    String fileId = qualityReportMetadata.getFileId();
    String sqlMappingVersion = qualityReportMetadata.getSqlMappingVersion();

    metadataTxtColumn.setTimestamp(creationTimestamp);
    metadataTxtColumn.setFileId(fileId);
    metadataTxtColumn.setSqlMappingVersion(sqlMappingVersion);

    return metadataTxtColumn.createColumn();

  }

  @Override
  public QualityReportMetadata parseValuesOfColumn(String column) {

    MetadataTxtColumn metadataTxtColumn = new MetadataTxtColumn();
    metadataTxtColumn.parseValuesOfColumn(column);
    Date timestamp = metadataTxtColumn.getTimestamp();
    String fileId = metadataTxtColumn.getFileId();
    String sqlMappingVersion = metadataTxtColumn.getSqlMappingVersion();

    QualityReportMetadata qualityReportMetadata = new QualityReportMetadata();
    qualityReportMetadata.setCreationTimestamp(timestamp);
    qualityReportMetadata.setFileId(fileId);
    qualityReportMetadata.setSqlMappingVersion(sqlMappingVersion);

    return qualityReportMetadata;

  }

}
