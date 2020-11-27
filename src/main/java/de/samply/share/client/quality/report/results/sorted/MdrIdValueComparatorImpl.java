package de.samply.share.client.quality.report.results.sorted;

public class MdrIdValueComparatorImpl implements MdrIdValueComparator {

  @Override
  public int compare(MdrIdValue mdrIdValue1, MdrIdValue mdrIdValue2) {

    int result = mdrIdValue1.getMdrId().toString().compareTo(mdrIdValue2.getMdrId().toString());

    return (result != 0) ? result : mdrIdValue1.getValue().compareTo(mdrIdValue2.getValue());

  }


}
