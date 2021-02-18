package de.samply.share.client.quality.report.file.metadata.txtcolumn;

import de.samply.share.client.quality.report.file.id.filename.QualityReportFilePattern;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;

public interface MetadataTxtColumnManager extends QualityReportFilePattern {

  String createColumn(QualityReportMetadata qualityReportMetadata);

  QualityReportMetadata parseValuesOfColumn(String column);


}
