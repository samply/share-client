package de.samply.share.client.quality.report.chainlinks.instances.ignoredelements;

import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.List;

public class IgnoredElementsChainLink<I extends ChainLinkItem & QualityResultsContext> extends
    ChainLink<I> {

  private static final String EMPTY_VALUE = "";
  private final MdrIgnoredElements ignoredElements;
  private final ModelSearcher modelSearcher;

  /**
   * Todo.
   *
   * @param ignoredElements Todo.
   * @param modelSearcher   Todo.
   */
  public IgnoredElementsChainLink(MdrIgnoredElements ignoredElements,
      ModelSearcher modelSearcher) {

    this.ignoredElements = ignoredElements;
    this.modelSearcher = modelSearcher;

  }

  @Override
  protected String getChainLinkId() {
    return "Ignored Elements Setter";
  }

  @Override
  protected I process(I item) throws ChainLinkException {

    QualityResults qualityResults = item.getQualityResults();

    addIgnoredElementsToQualityResults(qualityResults);

    return item;
  }

  private QualityResults addIgnoredElementsToQualityResults(QualityResults qualityResults) {

    for (MdrIdDatatype mdrId : ignoredElements) {

      for (String value : getValues(mdrId)) {

        qualityResults.put(mdrId, value, new QualityResult());

      }

    }

    return qualityResults;
  }

  private List<String> getValues(MdrIdDatatype mdrIdDatatype) {

    List<String> values = new ArrayList<>();
    Validations validations = modelSearcher.getValidations(mdrIdDatatype);

    List<PermissibleValue> permissibleValues = validations.getPermissibleValues();
    if (permissibleValues != null && permissibleValues.size() > 0) {

      for (PermissibleValue permissibleValue : permissibleValues) {
        values.add(permissibleValue.getValue());
      }

    } else {

      values.add(EMPTY_VALUE);

    }

    return values;

  }


}
