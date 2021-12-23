package de.samply.share.client.quality.report.chainlinks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainLinkItem implements Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(Cloneable.class);


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
   * Reset number of attempts and elapsed time.
   */
  public void resetAttempt() {

    attempt = 0;
    elapsedNanoTime = 0;

  }

  /**
   * Adds elapsed time of the chain link.
   *
   * @param elapsedNanoTime elapsed time in nanoseconds.
   */
  public void addElapsedNanoTime(long elapsedNanoTime) {

    this.elapsedNanoTime += elapsedNanoTime;

  }

  /**
   * Gets elapased time.
   *
   * @return Elapsed time in nanoseconds.
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
   * Clones Chain Link and resets operational values.
   *
   * @return Cloned chain link.
   */
  public ChainLinkItem clone() {

    try {

      return cloneAndResetOperationalValues();

    } catch (CloneNotSupportedException e) {
      logger.error(e.getMessage(),e);
      return null;
    }
  }

  /**
   * Clones and resets operational values.
   *
   * @return Cloned chain link item.
   * @throws CloneNotSupportedException Encapsulates exceptions of the class.
   */
  public ChainLinkItem cloneAndResetOperationalValues() throws CloneNotSupportedException {

    ChainLinkItem chainLinkContext = (ChainLinkItem) super.clone();
    chainLinkContext.resetConfigurationValues();
    chainLinkContext.resetAttempt();
    isAlreadyUsed = false;

    return chainLinkContext;

  }

  /**
   * Resets configuration values: is to be reused, is to be repeated, is to be forwarded.
   */
  public void resetConfigurationValues() {

    isToBeReused = false;
    isToBeRepeated = false;
    isToBeForwarded = true;

    chainLinkError = null;

  }

}
