package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperException;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.statistics.GeneralRehearsalStatistics;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;


public class DataElementStatsExcelRowMapper {


  private final ExcelRowMapperUtils excelRowMapperUtils;
  private final DktkIdMdrIdConverter dktkIdManager;
  private final CentraxxMapper centraXxMapper;

  /**
   * ToDo.
   *
   * @param dktkIdManager       ToDo.
   * @param excelRowMapperUtils ToDo.
   * @param centraXxMapper      ToDo.
   */
  public DataElementStatsExcelRowMapper(DktkIdMdrIdConverter dktkIdManager,
      ExcelRowMapperUtils excelRowMapperUtils, CentraxxMapper centraXxMapper) {

    this.excelRowMapperUtils = excelRowMapperUtils;
    this.dktkIdManager = dktkIdManager;
    this.centraXxMapper = centraXxMapper;

  }

  /**
   * ToDo.
   *
   * @param excelRowParameters ToDo.
   * @return ToDo.
   * @throws ExcelRowMapperException ToDo.
   */
  public DataElementStatsExcelRowElements convert(
      DataElementStatsExcelRowParameters excelRowParameters) throws ExcelRowMapperException {

    DataElementStatsExcelRowElements excelRowElements =
        new FormattedDataElementStatsExcelRowElements();

    QualityResultsStatistics qualityResultsStatistics = excelRowParameters
        .getQualityResultsStatistics();
    MdrIdDatatype mdrId = excelRowParameters.getMdrId();

    String dktkId = dktkIdManager.getDktkId(mdrId);
    String mdrDataElement = excelRowMapperUtils.getMdrDatenElement(mdrId);

    if (qualityResultsStatistics instanceof GeneralRehearsalStatistics) {

      GeneralRehearsalStatistics generalRehearsalStatistics =
          (GeneralRehearsalStatistics) qualityResultsStatistics;

      String priorization = centraXxMapper.getGeneralRehearsalPriorization(mdrId);

      boolean generalRehearsalAContainedInQR = generalRehearsalStatistics
          .getGeneralRehearsalAContainedInQR(mdrId);
      boolean generalRehearsalBLowMismatch = generalRehearsalStatistics
          .getGeneralRehearsalBLowMismatch(mdrId);
      boolean generalRehearsalAAndB = generalRehearsalStatistics
          .getGeneralRehearsalAAndB(mdrId);

      excelRowElements.setGeneralRehearsalPriorization(priorization);
      excelRowElements.setGeneralRehearsalAContainedInQR(generalRehearsalAContainedInQR);
      excelRowElements.setGeneralRehearsalBLowMismatch(generalRehearsalBLowMismatch);
      excelRowElements.setGeneralRehearsalAAndB(generalRehearsalAAndB);

    }

    excelRowElements.setDktkId(dktkId);
    excelRowElements.setMdrDatenElement(mdrDataElement);
    String cxxDatenElement = centraXxMapper.getCentraXxAttribute(mdrId);
    String mdrLink = excelRowMapperUtils.getMdrLink(mdrId);

    int numberOfPatientsWithMdrId = qualityResultsStatistics.getNumberOfPatientsWithMdrId(mdrId);
    excelRowElements.setMdrLink(mdrLink, mdrId);
    excelRowElements.setNumberOfPatientsWithDataElement(numberOfPatientsWithMdrId);
    double percentageOfPatientsWithMdrIdOutOfTotalPatients = qualityResultsStatistics
        .getPercentageOfPatientsWithMdrIdOutOfTotalPatients(mdrId);
    excelRowElements.setCxxDatenElement(cxxDatenElement);
    excelRowElements.setPercentageOfPatientsWithDataElementOutOfTotalPatients(
        percentageOfPatientsWithMdrIdOutOfTotalPatients);
    int numberOfPatientsWithMatchOnlyWithMdrId = qualityResultsStatistics
        .getNumberOfPatientsWithMatchOnlyWithMdrId(mdrId);
    excelRowElements
        .setNumberOfPatientsWithMatchOnlyForDataElement(numberOfPatientsWithMatchOnlyWithMdrId);
    double percentageOfPatientsWithMatchOnlyWithMdrIdOutOfPatientsWithMdrId =
        qualityResultsStatistics
            .getPercentageOfPatientsWithMatchOnlyWithMdrIdOutOfPatientsWithMdrId(mdrId);
    excelRowElements
        .setPercentageOfPatientsWithMatchOnlyForDataElementOutOfPatientsWithDataElement(
            percentageOfPatientsWithMatchOnlyWithMdrIdOutOfPatientsWithMdrId);
    double percentageOfPatientsWithMatchOnlyWithMdrIdOutOfTotalPatients = qualityResultsStatistics
        .getPercentageOfPatientsWithMatchOnlyWithMdrIdoutOfTotalPatients(mdrId);
    int numberOfPatientsWithAnyMismatchWithMdrId = qualityResultsStatistics
        .getNumberOfPatientsWithAnyMismatchWithMdrId(mdrId);
    excelRowElements.setPercentageOfPatientsWithMatchOnlyForDataElementOutOf_TotalPatients(
        percentageOfPatientsWithMatchOnlyWithMdrIdOutOfTotalPatients);
    excelRowElements.setNumberOfPatientsWithAnyMismatchForDataElement(
        numberOfPatientsWithAnyMismatchWithMdrId);
    double percentageOfPatientsWithAnyMismatchWithMdrIdOutOfPatientsWithMdrId =
        qualityResultsStatistics
            .getPercentageOfPatientsWithAnyMismatchWithMdrIdoutOfPatientsWithMdrId(mdrId);
    double percentageOfPatientsWithAnyMismatchWithMdrIdOutOfTotalPatients = qualityResultsStatistics
        .getPercentageOfPatientsWithAnyMismatchWithMdrIdOutOfTotalPatients(mdrId);
    excelRowElements
        .setPercentageOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement(
            percentageOfPatientsWithAnyMismatchWithMdrIdOutOfPatientsWithMdrId);
    excelRowElements.setPercentageOfPatientsWithAnyMismatchForDataElementOutOfTotalPatients(
        percentageOfPatientsWithAnyMismatchWithMdrIdOutOfTotalPatients);

    return excelRowElements;

  }


}
