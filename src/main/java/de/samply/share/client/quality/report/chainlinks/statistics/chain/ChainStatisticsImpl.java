package de.samply.share.client.quality.report.chainlinks.statistics.chain;

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticsConsumer;
import de.samply.share.client.util.Utils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChainStatisticsImpl implements ChainStatistics {

  private static final double SECONDS_IN_A_MINUTE = 60.0;
  private static final int MINUTES_IN_AN_HOUR = 60;
  private static final int MINUTES_IN_A_DAY = MINUTES_IN_AN_HOUR * 24;
  private static final double NANOSECONDS_IN_A_SECOND = 1000000000.0;


  private final List<ChainLinkStatisticsConsumer> chainLinkStatisticsConsumerList =
      new ArrayList<>();
  private final long startNanoTime;
  private final Date startDate;
  private Double globalPercentage;
  private final Double minimumPercentageToBeConsidered;


  {
    startNanoTime = System.nanoTime();
    startDate = new Date();
    globalPercentage = 0.0;
    minimumPercentageToBeConsidered = getMinimumPercentageToBeConsidered();

  }

  /**
   * Todo.
   *
   * @param chainLinkStatisticsConsumer Todo.
   */
  public void addChainLinkStatisticsConsumer(
      ChainLinkStatisticsConsumer chainLinkStatisticsConsumer) {

    if (chainLinkStatisticsConsumer != null) {
      chainLinkStatisticsConsumerList.add(chainLinkStatisticsConsumer);
    }

  }


  @Override
  public int getPercentage() {

    long elapsedTime = System.nanoTime() - startNanoTime;
    long estimatedNanoTimeToBeCompleted = getEstimatedNanoTimeToBeCompleted();

    double percentage = 100.0 * elapsedTime / (elapsedTime + estimatedNanoTimeToBeCompleted);
    double maxPercentageToBeShown = getMaxPercentageToBeShown();

    if (globalPercentage < percentage) {
      globalPercentage = percentage;
    }
    if (maxPercentageToBeShown > minimumPercentageToBeConsidered
        && globalPercentage > maxPercentageToBeShown) {
      globalPercentage = maxPercentageToBeShown;
    }

    return globalPercentage.intValue();

  }

  private double getMinimumPercentageToBeConsidered() {
    return 5.0 + Math.random() * (10.0 - 5.0);
  }

  private double getMaxPercentageToBeShown() {

    int numberOfConsumers = chainLinkStatisticsConsumerList.size();
    int numberOfFinalizedConsumers = 0;

    for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer :
        chainLinkStatisticsConsumerList) {
      if (chainLinkStatisticsConsumer.isFinalized()) {
        numberOfFinalizedConsumers++;
      }
    }

    return (numberOfConsumers > 0) ? (((double) numberOfFinalizedConsumers)
        / ((double) numberOfConsumers)) : 0;


  }

  @Override
  public String getEstimatedTimeToBeCompleted() {

    int estimatedTimeToBeCompletedInMinutes = getEstimatedTimeToBeCompletedInMinutes();
    return printTimeInMinutes(estimatedTimeToBeCompletedInMinutes);

  }

  private String printTimeInMinutes(int timeInMinutes) {

    StringBuilder stringBuilder = new StringBuilder();

    if (timeInMinutes >= MINUTES_IN_A_DAY) {

      int estimatedTimeToBeCompletedInDays = timeInMinutes / MINUTES_IN_A_DAY;

      stringBuilder.append(estimatedTimeToBeCompletedInDays);
      stringBuilder.append(" days ");

      timeInMinutes = timeInMinutes - estimatedTimeToBeCompletedInDays * MINUTES_IN_A_DAY;

    }

    if (timeInMinutes >= MINUTES_IN_AN_HOUR) {

      int estimatedTimeToBeCompletedInHours = timeInMinutes / MINUTES_IN_AN_HOUR;

      stringBuilder.append(estimatedTimeToBeCompletedInHours);
      stringBuilder.append(" h ");

      timeInMinutes = timeInMinutes - estimatedTimeToBeCompletedInHours * MINUTES_IN_AN_HOUR;


    }

    stringBuilder.append(timeInMinutes);
    stringBuilder.append(" min");

    return stringBuilder.toString();

  }


  private int getEstimatedTimeToBeCompletedInMinutes() {

    long estimatedNanoTimeToBeCompleted = getEstimatedNanoTimeToBeCompleted();
    return convertNanosecondsToMinutes(estimatedNanoTimeToBeCompleted);

  }

  private int convertNanosecondsToMinutes(long nanoseconds) {

    double nanosecondD = (double) nanoseconds;

    double minutesD = nanosecondD / (SECONDS_IN_A_MINUTE * NANOSECONDS_IN_A_SECOND);

    return Double.valueOf(minutesD).intValue();

  }


  private long getEstimatedNanoTimeToBeCompleted() {

    long estimatedTimeToBeCompleted = 0;

    for (int i = chainLinkStatisticsConsumerList.size() - 1; i >= 0; i--) {

      ChainLinkStatisticsConsumer chainLinkStatisticsConsumer = chainLinkStatisticsConsumerList
          .get(i);

      if (!chainLinkStatisticsConsumer.isFinalized()) {

        estimatedTimeToBeCompleted += chainLinkStatisticsConsumer.getRemainingNanoTime();

        if (chainLinkStatisticsConsumer.isProcessingElements()) {
          return estimatedTimeToBeCompleted;
        }

      }

    }

    return estimatedTimeToBeCompleted;
  }

  @Override
  public boolean isFinalized() {

    for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer :
        chainLinkStatisticsConsumerList) {

      if (!chainLinkStatisticsConsumer.isFinalized()) {
        return false;
      }

    }

    return true;
  }

  @Override
  public List<String> getMessages() {

    List<String> messages = new ArrayList<>();
    for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer :
        chainLinkStatisticsConsumerList) {

      if (!chainLinkStatisticsConsumer.isFinalized() && chainLinkStatisticsConsumer
          .isProcessingElements()) {
        messages.add(chainLinkStatisticsConsumer.getMessage());
      }

    }

    return messages;

  }

  @Override
  public boolean isAccurate() {

    return isConsumerWithMaximalWorkloadBeingProcessed();

  }

  private boolean isConsumerWithMaximalWorkloadBeingProcessed() {

    ChainLinkStatisticsConsumer consumerWithMaximalWorkload = null;

    for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer :
        chainLinkStatisticsConsumerList) {

      if (consumerWithMaximalWorkload == null) {

        consumerWithMaximalWorkload = chainLinkStatisticsConsumer;

      } else {

        if (chainLinkStatisticsConsumer.getNumberOfItems() > consumerWithMaximalWorkload
            .getNumberOfItems()) {
          consumerWithMaximalWorkload = chainLinkStatisticsConsumer;
        }

      }


    }

    return consumerWithMaximalWorkload.isProcessingElements() || consumerWithMaximalWorkload
        .isFinalized();

  }

  @Override
  public String getTimeConsumed() {

    long currentNanoTime = System.nanoTime();
    long elapsedNanoTime = currentNanoTime - startNanoTime;

    int elapsedTimeInMinutes = convertNanosecondsToMinutes(elapsedNanoTime);

    return printTimeInMinutes(elapsedTimeInMinutes);

  }

  @Override
  public String getStartTime() {
    return Utils.convertDate(startDate);
  }

}
