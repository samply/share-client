package de.samply.share.client.quality.report.file.excel.instances.statistics.table1;

import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;

public class Table1ExcelRowMapper {

  /**
   * Todo.
   *
   * @param dataElementGroup Todo.
   * @param percentage       Todo.
   * @return Todo.
   */
  public ExcelRowElements createExcelRowElements(String dataElementGroup, Double percentage) {

    Table1ExcelRowElements excelRowElements = new Table1ExcelRowElements();
    excelRowElements.addDataElementGroup(dataElementGroup);
    excelRowElements.addPercentrage(percentage);

    return excelRowElements;

  }

}
