package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;

public class DataElementStatsExcelRowContextFactory {


  private final DataElementStatsExcelRowMapper excelRowMapper;

  /**
   * Creates context with data element statistics for an excel row.
   *
   * @param excelRowMapperUtils Todo.
   * @param dktkIdManager       Get slot DKTK-ID for mdr id.
   * @param centraXxMapper      Todo.
   */
  public DataElementStatsExcelRowContextFactory(ExcelRowMapperUtils excelRowMapperUtils,
      DktkIdMdrIdConverter dktkIdManager, CentraxxMapper centraXxMapper) {

    this.excelRowMapper = new DataElementStatsExcelRowMapper(dktkIdManager, excelRowMapperUtils,
        centraXxMapper);

  }

  /**
   * Creates excel row with mdr data elements, validation and statistics.
   *
   * @param qualityResults           patient ids and validation of pair data element - attribute.
   * @param qualityResultsStatistics statistics of quality result.
   * @return context with the whole information.
   */
  public DataElementStatsExcelRowContext createExcelRowContext(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    return new DataElementStatsExcelRowContext(qualityResults, qualityResultsStatistics,
        excelRowMapper);

  }

}
