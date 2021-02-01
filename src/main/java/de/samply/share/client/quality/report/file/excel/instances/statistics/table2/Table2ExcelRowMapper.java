package de.samply.share.client.quality.report.file.excel.instances.statistics.table2;

import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperException;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import de.samply.share.common.utils.MdrIdDatatype;


public class Table2ExcelRowMapper {


  private final ExcelRowMapperUtils excelRowMapperUtils;
  private final DktkIdMdrIdConverter dktkIdManager;
  private final CentraxxMapper centraXxMapper;

  /**
   * Excel row mapper.
   *
   * @param dktkIdManager       Map mdr data element - mdr slot DKTK-ID.
   * @param excelRowMapperUtils Additional mapping information.
   * @param centraXxMapper      Map mdr data element - centraxx data element.
   */
  public Table2ExcelRowMapper(DktkIdMdrIdConverter dktkIdManager,
      ExcelRowMapperUtils excelRowMapperUtils, CentraxxMapper centraXxMapper) {

    this.excelRowMapperUtils = excelRowMapperUtils;
    this.dktkIdManager = dktkIdManager;
    this.centraXxMapper = centraXxMapper;

  }

  /**
   * Convert excel row parameters to excel row elements.
   *
   * @param excelRowParameters excel row parameters.
   * @return excel row elements.
   * @throws ExcelRowMapperException Encapsulates exception of the class.
   */
  public Table2ExcelRowElements convert(Table2ExcelRowParameters excelRowParameters)
      throws ExcelRowMapperException {

    Table2ExcelRowElements excelRowElements = new Table2ExcelRowElements();

    QualityResultsStatistics qualityResultsStatistics = excelRowParameters
        .getQualityResultsStatistics();
    MdrIdDatatype mdrId = excelRowParameters.getMdrId();

    String dktkId = dktkIdManager.getDktkId(mdrId);
    String mdrDataElement = excelRowMapperUtils.getMdrDatenElement(mdrId);
    String cxxDatenElement = centraXxMapper.getCentraXxAttribute(mdrId);
    String mdrLink = excelRowMapperUtils.getMdrLink(mdrId);

    int patientsForId = qualityResultsStatistics.getNumberOfPatientsWithMdrId(mdrId);
    int patientsForValidation = qualityResultsStatistics.getNumberOfPatientsForValidation(mdrId);
    Double ratio = (patientsForValidation > 0) ? 100.0d * (double) patientsForValidation
        / (double) patientsForId : 0;

    int numberOfMatchingPatientsWithMdrId = qualityResultsStatistics
        .getNumberOfMatchingPatientsWithMdrId(mdrId);
    int numberOfMismatchingPatientsWithMdrId = qualityResultsStatistics
        .getNumberOf_MismatchingPatientsWithMdrId(mdrId);

    double percentageOfMatchingPatientsWithMdrIdOutOfPatientsWithMdrId = qualityResultsStatistics
        .getPercentageOfMatchingPatientsWithMdrIdOutOfPatientsWithMdrId(mdrId);
    double percentageOfMismatchingPatientsWithMdrIdOutOfPatientsWithMdrId = qualityResultsStatistics
        .getPercentageOfMismatchingPatientsWithMdrIdOutOfPatientsWithMdrId(mdrId);

    double percentageOfPatientsOutOfTotalNumberOfPatientsForADataelement = qualityResultsStatistics
        .getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement(mdrId);

    excelRowElements.setDktkId(dktkId);
    excelRowElements.setMdrDatenElement(mdrDataElement);
    excelRowElements.setCxxDatenElement(cxxDatenElement);
    excelRowElements.setMdrLink(mdrLink, mdrId);
    excelRowElements.setPatientsForId(patientsForId);
    excelRowElements.setPatientsForValidation(patientsForValidation);
    excelRowElements.setRatio(ratio);
    excelRowElements.setNumberOfPatientsWithMatch(numberOfMatchingPatientsWithMdrId);
    excelRowElements.setNumberOfPatientsWithMismatch(numberOfMismatchingPatientsWithMdrId);
    excelRowElements.setPercentageOfPatientsWithMatch(
        percentageOfMatchingPatientsWithMdrIdOutOfPatientsWithMdrId);
    excelRowElements.setPercentageOfPatientsWithMismatch(
        percentageOfMismatchingPatientsWithMdrIdOutOfPatientsWithMdrId);
    excelRowElements.setPercentageOfTotalPatients(
        percentageOfPatientsOutOfTotalNumberOfPatientsForADataelement);

    return excelRowElements;

  }


}
