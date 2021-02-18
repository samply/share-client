package de.samply.share.client.quality.report.chainlinks.instances.validator;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidatorException;

public class ValidatorChainLink<I extends ChainLinkItem & ValidatorContext> extends ChainLink<I> {

  private final QualityResultsValidator qualityResultsValidator;

  public ValidatorChainLink(QualityResultsValidator qualityResultsValidator) {
    this.qualityResultsValidator = qualityResultsValidator;
  }

  @Override
  protected String getChainLinkId() {
    return "Validator";
  }

  @Override
  protected I process(I item) throws ChainLinkException {

    validate(item);
    return item;

  }

  private void validate(I item) throws ChainLinkException {

    try {

      qualityResultsValidator.validate(item.getQualityResults());

    } catch (QualityResultsValidatorException e) {
      throw new ChainLinkException(e);
    }

  }


}
