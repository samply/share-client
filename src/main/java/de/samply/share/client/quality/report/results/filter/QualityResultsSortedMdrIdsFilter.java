package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.comparator.MdrIdComparator;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class QualityResultsSortedMdrIdsFilter<I extends MdrIdComparator> extends
    QualityResultsFilter {

  public QualityResultsSortedMdrIdsFilter(QualityResults qualityResults) {
    super(qualityResults);
  }

  protected abstract I getComparator();

  @Override
  public Set<MdrIdDatatype> getMdrIds() {

    Set<MdrIdDatatype> mdrIds = super.getMdrIds();

    SortedSet<MdrIdDatatype> sortedMdrIds = new TreeSet<MdrIdDatatype>(getComparator());

    sortedMdrIds.addAll(mdrIds);

    return sortedMdrIds;

  }

}
