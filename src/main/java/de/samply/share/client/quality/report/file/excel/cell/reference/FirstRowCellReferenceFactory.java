package de.samply.share.client.quality.report.file.excel.cell.reference;

public class FirstRowCellReferenceFactory {

  private static final int numberOfLetters = 26;

  /**
   * Todo.
   *
   * @param ordinal Todo.
   * @return Todo.
   */
  public String getExcelColumn(int ordinal) {
    return org.apache.poi.hssf.util.CellReference.convertNumToColString(ordinal);
    //        ordinal--;
    //        return (ordinal >= 0 && ordinal < numberOfLetters) ? "" + ((char) ('A' + ordinal)) :
    //        getExcelColumn(ordinal / numberOfLetters) +
    //        getExcelColumn(ordinal % numberOfLetters + 1);

  }

  /**
   * Todo.
   *
   * @param sheetName     Todo.
   * @param columnOrdinal Todo.
   * @return Todo.
   */
  public CellReference createCellReference(String sheetName, int columnOrdinal) {

    CellReferenceImpl cellReference = new CellReferenceImpl();
    String column = getExcelColumn(columnOrdinal);
    cellReference.setColumn(column);
    cellReference.setSheetName(sheetName);

    return cellReference;

  }

}
