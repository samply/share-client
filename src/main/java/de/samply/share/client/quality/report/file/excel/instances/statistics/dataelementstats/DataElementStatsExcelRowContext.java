package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextImpl;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DataElementStatsExcelRowContext extends
    ExcelRowContextImpl<DataElementStatsExcelRowParameters> {


  protected static final Logger logger = LogManager
      .getLogger(DataElementStatsExcelRowContext.class);
  private final DataElementStatsExcelRowMapper excelRowMapper;


  /**
   * Todo.
   *
   * @param qualityResults           Todo.
   * @param qualityResultsStatistics Todo.
   * @param excelRowMapper           Todo.
   */
  public DataElementStatsExcelRowContext(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics,
      DataElementStatsExcelRowMapper excelRowMapper) {

    this.excelRowMapper = excelRowMapper;
    fillOutExcelRowParametersTList(qualityResults, qualityResultsStatistics);

  }

  private void fillOutExcelRowParametersTList(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    PercentageLogger percentageLogger = new PercentageLogger(logger,
        qualityResults.getMdrIds().size(), "analyzing quality results");

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {

      percentageLogger.incrementCounter();
      DataElementStatsExcelRowParameters excelRowParameters = createRowParameters(mdrId,
          qualityResultsStatistics);
      excelRowParametersList.add(excelRowParameters);

    }

  }

  private DataElementStatsExcelRowParameters createRowParameters(MdrIdDatatype mdrId,
      QualityResultsStatistics qualityResultStatistics) {

    DataElementStatsExcelRowParameters excelRowParameters =
        new DataElementStatsExcelRowParameters();

    excelRowParameters.setMdrId(mdrId);
    excelRowParameters.setQualityResultsStatistics(qualityResultStatistics);

    return excelRowParameters;

  }


  @Override
  protected ExcelRowElements convert(DataElementStatsExcelRowParameters table2ExcelRowParametersT)
      throws Exception {
    return excelRowMapper.convert(table2ExcelRowParametersT);
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }

  @Override
  public ExcelRowElements createEmptyExcelRowElements() {
    return new DataElementStatsExcelRowElements();
  }

}
