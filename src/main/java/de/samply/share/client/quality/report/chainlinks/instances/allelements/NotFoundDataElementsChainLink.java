package de.samply.share.client.quality.report.chainlinks.instances.allelements;

import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.instances.ignoredelements.QualityResultsContext;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.Set;

public class NotFoundDataElementsChainLink<I extends ChainLinkItem & QualityResultsContext> extends
    ChainLink<I> {


  private static final String EMPTY_VALUE = "";
  private final Model model;

  public NotFoundDataElementsChainLink(Model model) {
    this.model = model;
  }

  @Override
  protected String getChainLinkId() {
    return "Not found data elements";
  }

  @Override
  protected I process(I item) throws ChainLinkException {

    QualityResults qualityResults = item.getQualityResults();

    for (MdrIdAndValidations mdrIdAndValidations : model.getMdrIdAndValidations()) {

      MdrIdDatatype mdrId = mdrIdAndValidations.getMdrId();
      Set<String> values = qualityResults.getValues(mdrId);
      if (values == null || values.size() == 0) {
        qualityResults.put(mdrId, EMPTY_VALUE, new QualityResult());
      }

    }

    return item;

  }

}
