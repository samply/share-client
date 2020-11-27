package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;

public class QualityResultsValidIntegerFilter extends QualityResultsValidValueByTypeFilter {

  private static final String INTEGER_TYPE = "INTEGER";


  public QualityResultsValidIntegerFilter(QualityResults qualityResults,
      ModelSearcher modelSearcher) {
    super(qualityResults, modelSearcher);
  }

  @Override
  protected String getDataType() {
    return INTEGER_TYPE;
  }


}
