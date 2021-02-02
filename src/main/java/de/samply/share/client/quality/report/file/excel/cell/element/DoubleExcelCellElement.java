package de.samply.share.client.quality.report.file.excel.cell.element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.poi.ss.usermodel.Cell;


public class DoubleExcelCellElement extends ExcelCellElement<Double> {

  public DoubleExcelCellElement(Double element) {
    this(element, 1);
  }

  /**
   * Represents an excel cell of type double.
   *
   * @param element          Double element to be written in the excel cell.
   * @param numberOfDecimals Number of decimals to be shown in excel cell.
   */
  public DoubleExcelCellElement(Double element, int numberOfDecimals) {

    super(element);
    this.element = roundDouble(this.element, numberOfDecimals);

  }

  @Override
  protected Cell setCellValue(Cell cell) {

    cell.setCellValue(element);

    return cell;

  }

  private Double roundDouble(Double number, int numberOfDecimals) {

    if (number != null) {

      BigDecimal bd = new BigDecimal(number);
      bd = bd.setScale(numberOfDecimals, RoundingMode.HALF_UP);
      number = bd.doubleValue();

    }

    return number;

  }

}
