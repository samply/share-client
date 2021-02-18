package de.samply.share.client.quality.report.results.operations;

import de.dth.mdr.validator.MdrValidator;
import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.QueryValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class QualityResultsValidator {


  private static final Logger logger = LogManager.getLogger(QualityResultsValidator.class);

  private final QueryValidator queryValidator;
  private final MdrValidator mdrValidator;


  public QualityResultsValidator(MdrValidator dthValidator, QueryValidator queryValidator) {
    this.mdrValidator = dthValidator;
    this.queryValidator = queryValidator;
  }

  /**
   * Validate Quality Results.
   *
   * @param qualityResults Quality results.
   * @return Quality Results validated.
   * @throws QualityResultsValidatorException Encapsulates exceltions in the class.
   */
  public QualityResults validate(QualityResults qualityResults)
      throws QualityResultsValidatorException {
    if (qualityResults != null) {
      int numberOfQualityResults = getNumberOfQualityResults(qualityResults);
      PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfQualityResults,
          "validating quality results...");
      for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
        logger.debug("validating element  " + mdrId);
        for (String value : qualityResults.getValues(mdrId)) {
          qualityResults = validate(qualityResults, mdrId, value);
          percentageLogger.incrementCounter();
        }
      }
    }
    return qualityResults;
  }

  private QualityResults validate(QualityResults qualityResults, MdrIdDatatype mdrId, String value)
      throws QualityResultsValidatorException {
    value = correctDate(qualityResults, mdrId, value);
    if (isValid(mdrId, value)) {
      qualityResults.setAsValid(mdrId, value);
    }
    return qualityResults;
  }

  private int getNumberOfQualityResults(QualityResults qualityResults) {

    int counter = 0;

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
      counter += qualityResults.getValues(mdrId).size();
    }

    return counter;

  }


  private String correctDate(QualityResults qualityResults, MdrIdDatatype mdrId, String value) {

    String newValue = getDateValueInSlotFormat(mdrId, value);

    if (!value.equals(newValue)) {
      qualityResults.updateValue(mdrId, value, newValue);
      value = newValue;
    }

    return value;

  }

  private String getDateValueInSlotFormat(MdrIdDatatype mdrId, String value) {

    try {

      return queryValidator
          .getDateValueInElementValidationFormatWithSourceFormatAutodiscovering(mdrId.toString(),
              value);

    } catch (Exception e) {
      return value;
    }

  }

  private boolean isValid(MdrIdDatatype mdrId, String value)
      throws QualityResultsValidatorException {

    try {

      return (value != null) && mdrValidator.validate(mdrId.toString(), value);

    } catch (ValidatorException e) {
      //throw new QualityResultsValidatorException(e);
      logger.debug(mdrId + ":" + value);
      logger.debug(e);

      return false;
      //return isValid_LastMdrId(mdrId, value);
    }

  }

  private boolean isValid_LastMdrId(MdrIdDatatype mdrId, String value)
      throws QualityResultsValidatorException {

    try {
      return (value != null) && mdrValidator.validate(mdrId.getLatestMdr(), value);

    } catch (ValidatorException e) {
      throw new QualityResultsValidatorException(e);
    }
  }


}
