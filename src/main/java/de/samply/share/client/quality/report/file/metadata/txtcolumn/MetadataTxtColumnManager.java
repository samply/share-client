package de.samply.share.client.quality.report.file.metadata.txtcolumn;

import de.samply.share.client.quality.report.file.id.filename.QualityReportFilePattern;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;

public interface MetadataTxtColumnManager extends QualityReportFilePattern {

  public String createColumn(QualityReportMetadata qualityReportMetadata);

  public QualityReportMetadata parseValuesOfColumn(String column);


}
