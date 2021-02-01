package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.comparator.MdrIdComparatorByDktkId;

public class QualityResultsSortedMdrIdsByDktkIdFilter extends
    QualityResultsSortedMdrIdsFilter<MdrIdComparatorByDktkId> {

  private final MdrIdComparatorByDktkId mdrIdComparatorByDktkId;


  /**
   * Todo.
   *
   * @param qualityResults Todo.
   * @param dktkIdManager  Todo.
   */
  public QualityResultsSortedMdrIdsByDktkIdFilter(QualityResults qualityResults,
      DktkIdMdrIdConverter dktkIdManager) {

    super(qualityResults);
    mdrIdComparatorByDktkId = new MdrIdComparatorByDktkId(dktkIdManager);

  }

  @Override
  protected MdrIdComparatorByDktkId getComparator() {
    return mdrIdComparatorByDktkId;
  }

}
