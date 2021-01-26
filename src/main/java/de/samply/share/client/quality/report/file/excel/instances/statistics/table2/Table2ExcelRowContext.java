package de.samply.share.client.quality.report.file.excel.instances.statistics.table2;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextImpl;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Table2ExcelRowContext extends ExcelRowContextImpl<Table2ExcelRowParameters> {


  protected static final Logger logger = LogManager.getLogger(Table2ExcelRowContext.class);
  private final Table2ExcelRowMapper excelRowMapper;


  /**
   * Todo.
   *
   * @param qualityResults           Todo.
   * @param qualityResultsStatistics Todo.
   * @param excelRowMapper           Todo.
   */
  public Table2ExcelRowContext(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics, Table2ExcelRowMapper excelRowMapper) {

    this.excelRowMapper = excelRowMapper;
    fillOutExcelRowParametersTList(qualityResults, qualityResultsStatistics);

  }

  private void fillOutExcelRowParametersTList(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {

      Table2ExcelRowParameters excelRowParameters = createRowParameters(mdrId,
          qualityResultsStatistics);
      excelRowParametersList.add(excelRowParameters);

    }

  }

  private Table2ExcelRowParameters createRowParameters(MdrIdDatatype mdrId,
      QualityResultsStatistics qualityResultStatistics) {

    Table2ExcelRowParameters excelRowParameters = new Table2ExcelRowParameters();

    excelRowParameters.setMdrId(mdrId);
    excelRowParameters.setQualityResultsStatistics(qualityResultStatistics);

    return excelRowParameters;

  }


  @Override
  protected ExcelRowElements convert(Table2ExcelRowParameters table2ExcelRowParametersT)
      throws Exception {
    return excelRowMapper.convert(table2ExcelRowParametersT);
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }

  @Override
  public ExcelRowElements createEmptyExcelRowElements() {
    return new Table2ExcelRowElements();
  }

}
