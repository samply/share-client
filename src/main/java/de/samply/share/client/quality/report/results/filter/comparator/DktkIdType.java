package de.samply.share.client.quality.report.results.filter.comparator;

public class DktkIdType implements Comparable {

  private String source;
  private Integer number;

  /**
   * Constructs class to compare mdr slot DKTK-ID.
   *
   * @param dktkId mdr slot DKTK-ID.
   */
  public DktkIdType(String dktkId) {

    if (dktkId != null) {
      initialize(dktkId);
    }

  }

  private void initialize(String dktkId) {

    if (dktkId.contains("-")) {
      String[] split = dktkId.split("-");

      if (split.length >= 2) {
        source = split[0];
        number = convertToInteger(split[1]);
      }

    } else {

      source = dktkId;
      number = 0;

    }

  }

  private Integer convertToInteger(String number) {

    try {
      return Integer.valueOf(number);
    } catch (Exception e) {
      return null;
    }

  }

  @Override
  public int compareTo(Object o) {

    return (o == null) ? Integer.MAX_VALUE : compareDktkTypeTo((DktkIdType) o);

  }

  private int compareDktkTypeTo(DktkIdType dktkId) {

    int comparison1 = compare(source, dktkId.source);

    return (comparison1 == 0) ? compare(number, dktkId.number) : comparison1;

  }

  private int compare(Object o1, Object o2) {
    return o1 == null
        ? (o2 == null ? 0 : Integer.MIN_VALUE) :
        (o2 == null ? Integer.MAX_VALUE : ((Comparable) o1).compareTo(o2));
  }

}
