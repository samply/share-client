package de.samply.share.client.quality.report.file.excel.cell.reference;

public class FirstRowCellReferenceFactoryForOneSheet extends FirstRowCellReferenceFactory {

  private final String sheetName;

  public FirstRowCellReferenceFactoryForOneSheet(String sheetName) {
    this.sheetName = sheetName;
  }

  /**
   * Todo.
   *
   * @param columnOrdinal Todo.
   * @return Todo.
   */
  public CellReference createCellReference(int columnOrdinal) {

    return createCellReference(sheetName, columnOrdinal);

  }
}
