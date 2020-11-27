package de.samply.share.client.quality.report.file.excel.row.elements;

import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyle;
import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyleImpl;
import de.samply.share.client.quality.report.file.excel.cell.style.RightAlignedCellStyle;

public class FormattedExcelRowElements002 extends ExcelRowElements002 {


  @Override
  public void setNumberOfPatients(int numberOfPatients) {

    super.setNumberOfPatients(numberOfPatients);

    ExcelCellStyle excelCellStyle = new ExcelCellStyleImpl();
    excelCellStyle = new RightAlignedCellStyle(excelCellStyle);

    addExcelCellStyle(excelCellStyle, ElementOrder.NUMBER_OF_PATIENTS.ordinal());

  }

}
