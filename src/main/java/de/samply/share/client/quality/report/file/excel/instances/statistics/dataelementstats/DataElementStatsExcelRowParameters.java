package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;

public class DataElementStatsExcelRowParameters {

  private QualityResultsStatistics qualityResultsStatistics;
  private MdrIdDatatype mdrId;

  public QualityResultsStatistics getQualityResultsStatistics() {
    return qualityResultsStatistics;
  }

  public void setQualityResultsStatistics(QualityResultsStatistics qualityResultsStatistics) {
    this.qualityResultsStatistics = qualityResultsStatistics;
  }

  public MdrIdDatatype getMdrId() {
    return mdrId;
  }

  public void setMdrId(MdrIdDatatype mdrId) {
    this.mdrId = mdrId;
  }


}
