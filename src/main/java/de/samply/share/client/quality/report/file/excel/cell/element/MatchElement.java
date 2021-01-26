package de.samply.share.client.quality.report.file.excel.cell.element;

public class MatchElement {

  public static final String MATCH = "match";
  public static final String MISMATCH = "mismatch";
  public static final String NOT_MAPPED = "not mapped";
  public static final String NOT_FOUND = "not found";

  private boolean isNotMapped = false;
  private int numberOfElements = 0;
  private boolean isMatch = false;

  public MatchElement(int numberOfElements, boolean isMatch) {
    this.numberOfElements = numberOfElements;
    this.isMatch = isMatch;
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public String toString() {

    String result = null;

    if (isNotMapped) {
      result = NOT_MAPPED;
    } else if (numberOfElements <= 0) {
      result = NOT_FOUND;
    } else if (isMatch) {
      result = MATCH;
    } else {
      result = MISMATCH;
    }

    return result;

  }

  public void setNotMapped() {
    isNotMapped = true;
  }


}
