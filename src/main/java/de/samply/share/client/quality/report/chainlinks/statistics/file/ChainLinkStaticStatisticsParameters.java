package de.samply.share.client.quality.report.chainlinks.statistics.file;

public class ChainLinkStaticStatisticsParameters {

  private static final String SEPARATOR = ":";

  private Long averageNanoTime;
  private Integer averageNumberOfItems;

  public ChainLinkStaticStatisticsParameters() {
  }

  /**
   * Parameters of chain link statistics.
   *
   * @param line Line with average time in nanoseconds and number of items.
   */
  public ChainLinkStaticStatisticsParameters(String line) {

    if (line != null) {
      String[] split = line.split(SEPARATOR);

      if (split.length == 2) {

        averageNanoTime = convertToLong(split[0]);
        averageNumberOfItems = convertToInteger(split[1]);
      }
    }

  }


  /**
   * Prints chain link statistics parameters as an string.
   *
   * @return String containing chain link statistics parameters, like
   * average time in nanoseconds and average number of items.
   */
  public String toString() {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(averageNanoTime);
    stringBuilder.append(SEPARATOR);
    stringBuilder.append(averageNumberOfItems);

    return stringBuilder.toString();

  }

  private Long convertToLong(String number) {

    try {

      return Long.valueOf(number);

    } catch (Exception e) {
      return null;
    }

  }

  private Integer convertToInteger(String number) {

    try {

      return Integer.valueOf(number);

    } catch (Exception e) {
      return null;
    }

  }

  public Long getAverageNanoTime() {
    return averageNanoTime;
  }

  public void setAverageNanoTime(Long averageNanoTime) {
    this.averageNanoTime = averageNanoTime;
  }

  public Integer getAverageNumberOfItems() {
    return averageNumberOfItems;
  }

  public void setAverageNumberOfItems(Integer averageNumberOfItems) {
    this.averageNumberOfItems = averageNumberOfItems;
  }

}
