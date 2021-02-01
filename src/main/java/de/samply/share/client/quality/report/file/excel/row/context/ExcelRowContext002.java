package de.samply.share.client.quality.report.file.excel.row.context;

import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements002;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapper002;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperException;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExcelRowContext002 extends ExcelRowContextImpl<ExcelRowParameters002> {

  protected static final Logger logger = LogManager.getLogger(ExcelRowContext002.class);

  private final ExcelRowMapper002 excelRowMapper;
  private final AlphabeticallySortedMismatchedQualityResults asmQualityResults;


  public ExcelRowContext002(ExcelRowMapper002 excelRowMapper, QualityResults qualityResults) {
    this(excelRowMapper, qualityResults, null, null);
  }

  /**
   * Todo.
   *
   * @param excelRowMapper           Todo.
   * @param qualityResults           Todo.
   * @param asmQualityResults        Todo.
   * @param qualityResultsStatistics Todo.
   */
  public ExcelRowContext002(ExcelRowMapper002 excelRowMapper, QualityResults qualityResults,
      AlphabeticallySortedMismatchedQualityResults asmQualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    this.excelRowMapper = excelRowMapper;
    this.asmQualityResults = asmQualityResults;
    fillOutExcelRowParametersTList(qualityResults, qualityResultsStatistics);

  }

  private void fillOutExcelRowParametersTList(QualityResults qualityResults,
      QualityResultsStatistics qualityResultsStatistics) {

    int numberOfQualityResults = getNumberOfQualityResults(qualityResults);
    PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfQualityResults,
        "analyzing quality results...");

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {

      for (String value : qualityResults.getValues(mdrId)) {

        percentageLogger.incrementCounter();
        QualityResult qualityResult = qualityResults.getResult(mdrId, value);

        ExcelRowParameters002 excelRowParameters = createRowParameters(mdrId, value, qualityResult,
            qualityResultsStatistics);
        excelRowParametersList.add(excelRowParameters);

      }

    }

  }

  private int getNumberOfQualityResults(QualityResults qualityResults) {

    int counter = 0;
    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
      counter += qualityResults.getValues(mdrId).size();
    }

    return counter;

  }

  private ExcelRowParameters002 createRowParameters(MdrIdDatatype mdrId, String value,
      QualityResult qualityResult, QualityResultsStatistics qualityResultsStatistics) {

    ExcelRowParameters002 rowParameters = new ExcelRowParameters002();
    Integer mismatchOrdinal = getMismatchOrdinal(mdrId, value);
    double percentageOfPatientsWithValueOutOfPatientsWithMdrId = qualityResultsStatistics
        .getPercentageOfPatientsWithValueOutOfPatientsWithMdrId(mdrId, value);
    double percentageOfPatientsWithValueOutOfTotalPatients = qualityResultsStatistics
        .getPercentageOfPatientsWithValueOutOfTotalPatients(mdrId, value);

    rowParameters.setMdrId(mdrId);
    rowParameters.setValue(value);
    rowParameters.setQualityResult(qualityResult);
    rowParameters.setMismatchOrdinal(mismatchOrdinal);
    rowParameters.setPercentageOutOfPatientWithDataElement(
        percentageOfPatientsWithValueOutOfPatientsWithMdrId);
    rowParameters
        .setPercentageOutOfTotalPatients(percentageOfPatientsWithValueOutOfTotalPatients);

    return rowParameters;

  }

  private Integer getMismatchOrdinal(MdrIdDatatype mdrId, String value) {

    if (asmQualityResults == null) {
      return null;
    }
    Integer ordinal = asmQualityResults.getOrdinal(mdrId, value);
    return (ordinal < 0) ? null : ordinal;

  }

  @Override
  public ExcelRowElements createEmptyExcelRowElements() {
    return new ExcelRowElements002();
  }


  @Override
  protected ExcelRowElements convert(ExcelRowParameters002 excelRowParameters002)
      throws Exception {
    try {
      return excelRowMapper.createExcelRowElements(excelRowParameters002);
    } catch (ExcelRowMapperException e) {
      throw new Exception(e);
    }
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }

}
