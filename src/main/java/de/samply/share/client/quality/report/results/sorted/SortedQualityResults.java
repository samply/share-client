package de.samply.share.client.quality.report.results.sorted;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

public interface SortedQualityResults extends QualityResults, Iterable<QualityResult> {

  public int getOrdinal(MdrIdDatatype mdrId, String value);

  public MdrIdDatatype getMdrId(int ordinal);

  public String getValue(int ordinal);

}
