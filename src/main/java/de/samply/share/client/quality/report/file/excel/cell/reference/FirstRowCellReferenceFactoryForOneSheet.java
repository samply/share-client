package de.samply.share.client.quality.report.file.excel.cell.reference;

public class FirstRowCellReferenceFactoryForOneSheet extends FirstRowCellReferenceFactory {

  private final String sheetName;

  public FirstRowCellReferenceFactoryForOneSheet(String sheetName) {
    this.sheetName = sheetName;
  }

  /**
   * Creates an excel cell reference.
   *
   * @param columnOrdinal Number of column.
   * @return excel cell reference.
   */
  public CellReference createCellReference(int columnOrdinal) {

    return createCellReference(sheetName, columnOrdinal);

  }
}
