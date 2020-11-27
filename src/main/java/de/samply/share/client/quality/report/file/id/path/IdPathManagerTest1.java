package de.samply.share.client.quality.report.file.id.path;

import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerImplTest1;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern002;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager002;

public class IdPathManagerTest1 extends
    IdPathManagerImpl<QualityResultCsvLineManagerImplTest1, ExcelPattern002,
        MetadataTxtColumnManager002> {

  @Override
  public Class<QualityResultCsvLineManagerImplTest1> getQualityResultCsvLineManagerClass() {
    return QualityResultCsvLineManagerImplTest1.class;
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
