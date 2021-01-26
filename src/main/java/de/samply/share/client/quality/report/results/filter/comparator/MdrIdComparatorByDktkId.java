package de.samply.share.client.quality.report.results.filter.comparator;

import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.common.utils.MdrIdDatatype;


public class MdrIdComparatorByDktkId extends MdrIdComparator<DktkIdType> {


  private final DktkIdMdrIdConverter dktkIdManager;

  public MdrIdComparatorByDktkId(DktkIdMdrIdConverter dktkIdManager) {
    this.dktkIdManager = dktkIdManager;
  }

  @Override
  protected DktkIdType getValueToCompare(MdrIdDatatype mdrId) {

    String dktkId = dktkIdManager.getDktkId(mdrId);
    return (dktkId == null) ? null : new DktkIdType(dktkId);

  }


}
