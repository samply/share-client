package de.samply.share.client.model.check;

/**
 * A class that holds count and execution time for a check.
 */
public class ReferenceQueryCheckResult {

  private int count;
  private long executionTimeMilis;

  public ReferenceQueryCheckResult() {
    count = -1;
    executionTimeMilis = -1;
  }

  /**
   * Get the count.
   *
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * Set the count.
   *
   * @param count the count to set
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * Get the execution time in milliseconds.
   *
   * @return the executionTimeMilis
   */
  public long getExecutionTimeMilis() {
    return executionTimeMilis;
  }

  /**
   * Set the execution time in milliseconds.
   *
   * @param executionTimeMilis the executionTimeMilis to set
   */
  public void setExecutionTimeMilis(long executionTimeMilis) {
    this.executionTimeMilis = executionTimeMilis;
  }
}
