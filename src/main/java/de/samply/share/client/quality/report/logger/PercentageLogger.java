package de.samply.share.client.quality.report.logger;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.Logger;

/**
 * TODO: Move class (and PercentageLogger of samply.common-ldmclient-centraxx) to
 * <p>samply.share.common</p>
 *
 * <p>PercentageLogger is intended to log the progress of another process, that consists of
 * "numberOfElements" steps and that is described through "description".</p>
 *
 * <p>Example:</p>
 *
 * <p>doing something in several steps...</p>
 * <p>10%</p>
 * <p>40%</p>
 * <p>70%</p>
 * <p>90%</p>
 * <p>100%</p>
 */

public class PercentageLogger {

  private final Logger logger;
  private final int numberOfElements;
  private final String description;

  private int counter = 0;
  private int lastPercentage = 0;

  private boolean isStarted = false;

  /**
   * Calculates percentage of remaining steps and logs it.
   *
   * @param logger           logger
   * @param numberOfElements number of elements
   * @param description      description of the operation
   */
  public PercentageLogger(Logger logger, int numberOfElements, String description) {

    Preconditions.checkArgument(numberOfElements > 0, "numberOfElements is negative");

    this.logger = logger;
    this.numberOfElements = numberOfElements;
    this.description = description;

  }

  /**
   * Logs description.
   */
  public void start() {
    logger.debug(description);
    isStarted = true;
  }

  /**
   * Increments one step.
   */
  public void incrementCounter() {

    if (!isStarted) {
      start();
    }

    int percentage = 100 * ++counter / numberOfElements;

    if (lastPercentage != percentage) {

      lastPercentage = percentage;
      if (percentage % 10 == 0) {
        logger.debug(percentage + " %");
      }

    }

  }


}
