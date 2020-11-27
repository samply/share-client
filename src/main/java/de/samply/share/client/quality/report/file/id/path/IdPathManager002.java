package de.samply.share.client.quality.report.file.id.path;

import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager002;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern002;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager002;

public class IdPathManager002 extends
    IdPathManagerImpl<QualityResultCsvLineManager002, ExcelPattern002,
        MetadataTxtColumnManager002> {

  @Override
  public Class<QualityResultCsvLineManager002> getQualityResultCsvLineManagerClass() {
    return QualityResultCsvLineManager002.class;
  }

  @Override
  public Class<ExcelPattern002> getExcelPatternClass() {
    return ExcelPattern002.class;
  }

  @Override
  public Class<MetadataTxtColumnManager002> getMetadataTxtColumnManager() {
    return MetadataTxtColumnManager002.class;
  }

}
