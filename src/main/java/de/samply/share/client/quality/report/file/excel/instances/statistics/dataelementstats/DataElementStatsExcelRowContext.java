package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextImpl;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.PercentageLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataElementStatsExcelRowContext extends
    ExcelRowContextImpl<DataElementStatsExcelRowParameters> {


  protected static final Logger logger = LoggerFactory
      .getLogger(DataElementStatsExcelRowContext.class);
  private final DataElementStatsExcelRowMapper excelRowMapper;


  /**
   * Data element statistics for an excel row.
   *
   * @param qualityResults           Group of patient ids and validation of pair data element -
   *                                 attribute.
   * @param qualityResultsStatistics Statistics of the quality results.
   * @param excelRowMapper           Adds additional information to the quality results, like the
   *                                 centraxx mapping.
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
  public ExcelRowElements createEmptyExcelRowElements() {
    return new DataElementStatsExcelRowElements();
  }

}
