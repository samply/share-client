package de.samply.share.client.quality.report.file.excel.row.context;

import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapper002;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;

public class ExcelRowContextFactory002 {

  private final ExcelRowMapper002 excelRowMapper;

  public ExcelRowContextFactory002(ExcelRowMapper002 excelRowMapper) {
    this.excelRowMapper = excelRowMapper;
  }

  /**
   * Creates excel row context.
   *
   * @param qualityResults quality results for the quality report.
   * @return excel row context with information for the excel row.
   */
  public ExcelRowContext createExcelRowContext(QualityResults qualityResults) {

    return new ExcelRowContext002(excelRowMapper, qualityResults);

  }

  /**
   * Creates excel row context.
   *
   * @param qualityResults           quality results for the quality report.
   * @param asmQualityResults        alphabetically sorted quality results.
   * @param qualityResultsStatistics quality result statistics.
   * @return excel row context with information for the excel row.
   */
  public ExcelRowContext createExcelRowContext(QualityResults qualityResults,
      AlphabeticallySortedMismatchedQualityResults asmQualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    return new ExcelRowContext002(excelRowMapper, qualityResults, asmQualityResults,
        qualityResultsStatistics);

  }


}
