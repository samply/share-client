package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;

public class QualityResultsValidStringFilter extends QualityResultsValidValueByTypeFilter {

  private static final String STRING_TYPE = "STRING";


  public QualityResultsValidStringFilter(QualityResults qualityResults,
      ModelSearcher modelSearcher) {
    super(qualityResults, modelSearcher);
  }

  @Override
  protected String getDataType() {
    return STRING_TYPE;
  }


}
