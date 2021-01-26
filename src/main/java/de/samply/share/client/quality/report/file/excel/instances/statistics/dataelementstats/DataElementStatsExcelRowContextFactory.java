package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;

public class DataElementStatsExcelRowContextFactory {


  private final DataElementStatsExcelRowMapper excelRowMapper;

  /**
   * Todo.
   *
   * @param excelRowMapperUtils Todo.
   * @param dktkIdManager       Todo.
   * @param centraXxMapper      Todo.
   */
  public DataElementStatsExcelRowContextFactory(ExcelRowMapperUtils excelRowMapperUtils,
      DktkIdMdrIdConverter dktkIdManager, CentraxxMapper centraXxMapper) {

    this.excelRowMapper = new DataElementStatsExcelRowMapper(dktkIdManager, excelRowMapperUtils,
        centraXxMapper);

  }

  /**
   * Todo.
   *
   * @param qualityResults           Todo.
   * @param qualityResultsStatistics Todo.
   * @return Todo.
   */
  public DataElementStatsExcelRowContext createExcelRowContext(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    return new DataElementStatsExcelRowContext(qualityResults, qualityResultsStatistics,
        excelRowMapper);

  }

}
