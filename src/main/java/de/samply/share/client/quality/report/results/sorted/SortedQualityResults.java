package de.samply.share.client.quality.report.results.sorted;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

public interface SortedQualityResults extends QualityResults, Iterable<QualityResult> {

  int getOrdinal(MdrIdDatatype mdrId, String value);

  MdrIdDatatype getMdrId(int ordinal);

  String getValue(int ordinal);

}
