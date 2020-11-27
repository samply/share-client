package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;

public class QualityResultsValidDateFilter extends QualityResultsValidValueByTypeFilter {

  private static final String DATE_TYPE = "DATE";


  public QualityResultsValidDateFilter(QualityResults qualityResults, ModelSearcher modelSearcher) {
    super(qualityResults, modelSearcher);
  }

  @Override
  protected String getDataType() {
    return DATE_TYPE;
  }


}
