package de.samply.share.client.quality.report.results.filter.comparator;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Comparator;

public abstract class MdrIdComparator<I extends Comparable> implements Comparator<MdrIdDatatype> {

  protected abstract I getValueToCompare(MdrIdDatatype mdrId);

  @Override
  public int compare(MdrIdDatatype o1, MdrIdDatatype o2) {

    I value1 = getValueToCompare(o1);
    I value2 = getValueToCompare(o2);

    return value1 == null
        ? (value2 == null ? 0 : Integer.MIN_VALUE) :
        (value2 == null ? Integer.MAX_VALUE : value1.compareTo(value2));

  }

}
