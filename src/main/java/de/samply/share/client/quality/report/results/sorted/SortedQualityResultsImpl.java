package de.samply.share.client.quality.report.results.sorted;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.QualityResultsFilter;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.Iterator;


public abstract class SortedQualityResultsImpl extends QualityResultsFilter implements
    SortedQualityResults {


  private final ArrayList<MdrIdValue> sortedMdrIdValues;

  public SortedQualityResultsImpl(QualityResults qualityResults) {
    super(qualityResults);
    sortedMdrIdValues = sortQualityResults(qualityResults);
  }

  protected abstract ArrayList<MdrIdValue> sortQualityResults(QualityResults qualityResults);

  @Override
  public int getOrdinal(MdrIdDatatype mdrId, String value) {

    MdrIdValue mdrIdValue = new MdrIdValue(mdrId, value);
    return sortedMdrIdValues.indexOf(mdrIdValue);

  }

  @Override
  public MdrIdDatatype getMdrId(int ordinal) {

    MdrIdValue mdrIdValue = sortedMdrIdValues.get(ordinal);
    return (mdrIdValue == null) ? null : mdrIdValue.getMdrId();

  }

  @Override
  public String getValue(int ordinal) {

    MdrIdValue mdrIdValue = sortedMdrIdValues.get(ordinal);
    return (mdrIdValue == null) ? null : mdrIdValue.getValue();

  }

  @Override
  public Iterator<QualityResult> iterator() {
    return new SortedQualityResultsIterator();
  }

  private class SortedQualityResultsIterator implements Iterator<QualityResult> {

    private final Iterator<MdrIdValue> mdrIdValueIterator;

    public SortedQualityResultsIterator() {
      this.mdrIdValueIterator = sortedMdrIdValues.iterator();
    }

    @Override
    public boolean hasNext() {
      return mdrIdValueIterator.hasNext();
    }

    @Override
    public QualityResult next() {

      MdrIdValue next = mdrIdValueIterator.next();
      return qualityResults.getResult(next.getMdrId(), next.getValue());

    }

    @Override
    public void remove() {
      mdrIdValueIterator.remove();
    }
  }

}
