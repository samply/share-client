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
   * Todo.
   *
   * @param qualityResults Todo.
   * @return Todo.
   */
  public ExcelRowContext createExcelRowContext(QualityResults qualityResults) {

    return new ExcelRowContext002(excelRowMapper, qualityResults);

  }

  /**
   * Todo.
   *
   * @param qualityResults           Todo.
   * @param asmQualityResults        Todo.
   * @param qualityResultsStatistics Todo.
   * @return Todo.
   */
  public ExcelRowContext createExcelRowContext(QualityResults qualityResults,
      AlphabeticallySortedMismatchedQualityResults asmQualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    return new ExcelRowContext002(excelRowMapper, qualityResults, asmQualityResults,
        qualityResultsStatistics);

  }


}
