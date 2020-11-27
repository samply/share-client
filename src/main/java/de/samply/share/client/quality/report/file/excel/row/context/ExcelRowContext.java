package de.samply.share.client.quality.report.file.excel.row.context;

import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;

public interface ExcelRowContext extends Iterable<ExcelRowElements> {

  public ExcelRowElements createEmptyExcelRowElements();

  public Integer getNumberOfRows();

}
