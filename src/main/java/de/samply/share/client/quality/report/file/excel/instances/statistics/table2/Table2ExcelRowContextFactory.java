package de.samply.share.client.quality.report.file.excel.instances.statistics.table2;

import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;

public class Table2ExcelRowContextFactory {


  private final Table2ExcelRowMapper excelRowMapper;


  /**
   * Excel row context.
   *
   * @param excelRowMapperUtils Exel row mapper utils.
   * @param dktkIdManager       Map mdr id - slot DKTK-ID.
   * @param centraXxMapper      Map mdr data element - centraxx data element.
   */
  public Table2ExcelRowContextFactory(ExcelRowMapperUtils excelRowMapperUtils,
      DktkIdMdrIdConverter dktkIdManager, CentraxxMapper centraXxMapper) {

    this.excelRowMapper = new Table2ExcelRowMapper(dktkIdManager, excelRowMapperUtils,
        centraXxMapper);

  }


  /**
   * Create excel row context.
   *
   * @param qualityResults           Quality results.
   * @param qualityResultsStatistics Quality result statistics.
   * @return excel rot context.
   */
  public Table2ExcelRowContext createExcelRowContext(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    return new Table2ExcelRowContext(qualityResults, qualityResultsStatistics, excelRowMapper);

  }

}
