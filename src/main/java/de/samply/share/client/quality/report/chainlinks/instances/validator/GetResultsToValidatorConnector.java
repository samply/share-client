package de.samply.share.client.quality.report.chainlinks.instances.validator;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.connector.ChainLinkConnectorFunnel;
import de.samply.share.client.quality.report.chainlinks.instances.result.ResultContext;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.operations.QualityResultsAnalyzer;
import de.samply.share.model.ccp.QueryResult;

public class GetResultsToValidatorConnector<I extends ChainLinkItem & ValidatorContext> extends
    ChainLinkConnectorFunnel {


  private I item;
  private final QualityResultsAnalyzer qualityResultsAnalyzer;

  public GetResultsToValidatorConnector(ChainLink nextChainLink,
      QualityResultsAnalyzer qualityResultsAnalyzer) {
    super(nextChainLink);
    this.qualityResultsAnalyzer = qualityResultsAnalyzer;
  }

  @Override
  protected void keepItemForLater(ChainLinkItem chainLinkItem) {
    item = analyze((I) chainLinkItem);
  }

  @Override
  protected ChainLinkItem getItemForNextChainLink() {
    return item;
  }

  private I analyze(I chainLinkItem) {

    QualityResults qualityResults = chainLinkItem.getQualityResults();
    QueryResult queryResult = ((ResultContext) chainLinkItem).getQueryResult();

    analyze(qualityResults, queryResult);

    return chainLinkItem;
  }

  private void analyze(QualityResults qualityResults, QueryResult queryResult) {
    qualityResultsAnalyzer.analyze(qualityResults, queryResult);
  }

}
