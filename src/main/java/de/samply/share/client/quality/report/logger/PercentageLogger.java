package de.samply.share.client.quality.report.logger;

import org.apache.logging.log4j.Logger;

public class PercentageLogger {

  private final Logger logger;
  private final int numberOfElements;
  private int counter = 0;
  private int lastPercentage = 0;

  /**
   * Todo.
   *
   * @param logger           Todo.
   * @param numberOfElements Todo.
   * @param description      Todo.
   */
  public PercentageLogger(Logger logger, int numberOfElements, String description) {

    this.logger = logger;
    this.numberOfElements = numberOfElements;
    if (numberOfElements > 0) {
      logger.debug(description);
    }

  }

  /**
   * Todo.
   */
  public void incrementCounter() {
    if (numberOfElements > 0) {
      counter++;
      Double percentage = 100.0D * ((double) counter) / ((double) numberOfElements);
      int ipercentage = percentage.intValue();
      if (lastPercentage != ipercentage) {
        lastPercentage = ipercentage;
        if (ipercentage % 10 == 0) {
          logger.debug(ipercentage + " %");
        }
      }
    }
  }

}
