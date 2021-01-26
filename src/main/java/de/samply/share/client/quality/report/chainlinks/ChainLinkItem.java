package de.samply.share.client.quality.report.chainlinks;


public class ChainLinkItem implements Cloneable {


  protected boolean isToBeReused;
  protected boolean isToBeRepeated;
  protected boolean isToBeForwarded;
  protected boolean isAlreadyUsed = false;
  private int attempt = 0;
  private long elapsedNanoTime = 0;
  private ChainLinkError chainLinkError;


  public ChainLinkItem() {
    resetConfigurationValues();
  }

  public boolean isToBeRepeated() {
    return isToBeRepeated;
  }

  public boolean isToBeReused() {
    return isToBeReused;
  }

  public void setToBeRepeated() {
    isToBeRepeated = true;
  }

  public void setToBeReused() {
    isToBeReused = true;
  }

  public boolean isToBeForwarded() {
    return isToBeForwarded;
  }

  public ChainLinkError getChainLinkError() {
    return chainLinkError;
  }

  public void setChainLinkError(ChainLinkError chainLinkError) {
    this.chainLinkError = chainLinkError;
  }

  public void setNotToBeForwarded() {
    isToBeForwarded = false;
  }

  public int getAttempt() {
    return attempt;
  }

  public void incrementAttempt() {
    this.attempt++;
  }

  /**
   * Todo.
   */
  public void resetAttempt() {

    attempt = 0;
    elapsedNanoTime = 0;

  }

  /**
   * Todo.
   *
   * @param elapsedNanoTime Todo.
   */
  public void addElapsedNanoTime(long elapsedNanoTime) {

    this.elapsedNanoTime += elapsedNanoTime;

  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public long getElapsedNanoTime() {
    return elapsedNanoTime;
  }

  public boolean isAlreadyUsed() {
    return isAlreadyUsed;
  }

  public void setAlreadyUsed() {
    isAlreadyUsed = true;
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public ChainLinkItem clone() {

    try {

      return cloneAndResetOperationalValues();

    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Todo.
   *
   * @return Todo.
   * @throws CloneNotSupportedException Todo.
   */
  public ChainLinkItem cloneAndResetOperationalValues() throws CloneNotSupportedException {

    ChainLinkItem chainLinkContext = (ChainLinkItem) super.clone();
    chainLinkContext.resetConfigurationValues();
    chainLinkContext.resetAttempt();
    isAlreadyUsed = false;

    return chainLinkContext;

  }

  /**
   * Todo.
   */
  public void resetConfigurationValues() {

    isToBeReused = false;
    isToBeRepeated = false;
    isToBeForwarded = true;

    chainLinkError = null;

  }

}
