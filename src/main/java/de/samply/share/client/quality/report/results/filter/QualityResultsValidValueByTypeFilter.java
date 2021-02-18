package de.samply.share.client.quality.report.results.filter;

import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

public abstract class QualityResultsValidValueByTypeFilter extends QualityResultsValidValueFilter {

  private final ModelSearcher modelSearcher;

  public QualityResultsValidValueByTypeFilter(QualityResults qualityResults,
      ModelSearcher modelSearcher) {
    super(qualityResults);
    this.modelSearcher = modelSearcher;
  }

  protected abstract String getDataType();

  @Override
  protected boolean isFilterCondition(MdrIdDatatype mdrId) {
    Validations validations = modelSearcher.getValidations(mdrId);
    return validations.getDatatype().equals(getDataType());

  }
}
