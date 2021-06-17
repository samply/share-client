package de.samply.share.client.quality.report.file.excel.cell.reference;

public class FirstRowCellReferenceFactory {

  private static final int numberOfLetters = 26;

  /**
   * Get excel column of first row cell reference.
   *
   * @param ordinal Number of column.
   * @return excel column.
   */
  public String getExcelColumn(int ordinal) {
    return org.apache.poi.ss.util.CellReference.convertNumToColString(ordinal);
    //        ordinal--;
    //        return (ordinal >= 0 && ordinal < numberOfLetters) ? "" + ((char) ('A' + ordinal)) :
    //        getExcelColumn(ordinal / numberOfLetters) +
    //        getExcelColumn(ordinal % numberOfLetters + 1);

  }

  /**
   * Create cell reference.
   *
   * @param sheetName     Name of the Excel sheet.
   * @param columnOrdinal Number of column.
   * @return Excel cell reference.
   */
  public CellReference createCellReference(String sheetName, int columnOrdinal) {

    CellReferenceImpl cellReference = new CellReferenceImpl();
    String column = getExcelColumn(columnOrdinal);
    cellReference.setColumn(column);
    cellReference.setSheetName(sheetName);

    return cellReference;

  }

}
