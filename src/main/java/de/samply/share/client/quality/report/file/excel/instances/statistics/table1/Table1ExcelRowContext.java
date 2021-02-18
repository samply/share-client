package de.samply.share.client.quality.report.file.excel.instances.statistics.table1;

import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.results.statistics.QualityResultsStatistics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table1ExcelRowContext implements ExcelRowContext {


  private static final String
      PERCENTAGE_OF_COMPLETELY_MATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS = "Alle OK";
  private static final String
      PERCENTAGE_OF_NOT_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS =
      "Mit Fehlern";
  private static final String
      PERCENTAGE_OF_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS =
      "Nur Fehlerhaft";
  private static final String
      PERCENTAGE_OF_NOT_MAPPED_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS = "Nicht vorhanden";

  private final Table1ExcelRowMapper excelRowMapper = new Table1ExcelRowMapper();
  private final List<ExcelRowElements> excelRowElementsList = new ArrayList<>();


  public Table1ExcelRowContext(QualityResultsStatistics qualityResultsStatistics) {
    fillExcelRowElementsList(qualityResultsStatistics);
  }

  private void fillExcelRowElementsList(QualityResultsStatistics qualityResultsStatistics) {

    double percentageOfCompletelyMatchingDataelementsOutOfAllDataelements =
        qualityResultsStatistics
            .getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements();
    ExcelRowElements excelRowElements1 = excelRowMapper.createExcelRowElements(
        PERCENTAGE_OF_COMPLETELY_MATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS,
        percentageOfCompletelyMatchingDataelementsOutOfAllDataelements);

    double percentageOfNotCompletelyMismatchingDataelementsOutOfAllDataelements =
        qualityResultsStatistics
            .getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements();
    ExcelRowElements excelRowElements2 = excelRowMapper.createExcelRowElements(
        PERCENTAGE_OF_NOT_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS,
        percentageOfNotCompletelyMismatchingDataelementsOutOfAllDataelements);

    double percentageOfCompletelyMismatchingDataelementsOutOfAllDataelements =
        qualityResultsStatistics
            .getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements();
    ExcelRowElements excelRowElements3 = excelRowMapper.createExcelRowElements(
        PERCENTAGE_OF_COMPLETELY_MISMATCHING_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS,
        percentageOfCompletelyMismatchingDataelementsOutOfAllDataelements);

    double percentageOfNotMappedDataelementsOutOfAllDataelements = qualityResultsStatistics
        .getPercentageOf_NotMappedDataelements_outOf_AllDataelements();
    ExcelRowElements excelRowElements4 = excelRowMapper
        .createExcelRowElements(PERCENTAGE_OF_NOT_MAPPED_DATA_ELEMENTS_OUT_OF_ALL_DATA_ELEMENTS,
            percentageOfNotMappedDataelementsOutOfAllDataelements);

    excelRowElementsList.add(excelRowElements1);
    excelRowElementsList.add(excelRowElements2);
    excelRowElementsList.add(excelRowElements3);
    excelRowElementsList.add(excelRowElements4);

  }

  @Override
  public ExcelRowElements createEmptyExcelRowElements() {
    return new Table1ExcelRowElements();
  }

  @Override
  public Integer getNumberOfRows() {
    return excelRowElementsList.size();
  }

  @Override
  public Iterator<ExcelRowElements> iterator() {
    return excelRowElementsList.iterator();
  }

}
