package de.samply.share.client.quality.report.file.excel.cell.reference;

public class CellReferenceImpl implements CellReference {

  private String sheetName;
  private String column = "A";
  private int row = 1;

  @Override
  public String getLink() {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("'");
    stringBuilder.append(sheetName);
    stringBuilder.append("'!");
    stringBuilder.append(column);
    stringBuilder.append(row);

    return stringBuilder.toString();

  }

  public void setSheetName(String sheetName) {
    this.sheetName = sheetName;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public void setRow(int row) {
    this.row = row;
  }
}
