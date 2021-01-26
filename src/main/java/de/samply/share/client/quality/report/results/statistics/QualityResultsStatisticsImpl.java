package de.samply.share.client.quality.report.results.statistics;

import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashSet;
import java.util.Set;

public class QualityResultsStatisticsImpl implements QualityResultsStatistics,
    GeneralRehearsalStatistics {

  private final QualityResults qualityResults;
  private final MdrMappedElements mdrMappedElements;

  private final AndConditionsEvaluator andConditionsEvaluator = new AndConditionsEvaluator();
  private final OrConditionsEvaluator orConditionsEvaluator = new OrConditionsEvaluator();

  private Integer totalNumberOfPatients;


  public QualityResultsStatisticsImpl(QualityResults qualityResults,
      MdrMappedElements mdrMappedElements) {
    this.qualityResults = qualityResults;
    this.mdrMappedElements = mdrMappedElements;
  }


  private double getPercentage(int part, int total) {
    return (total > 0) ? 100.0d * ((double) part) / ((double) total) : 0;
  }

  @Override
  public double getPercentageOfPatientsWithValueOutOfPatientsWithMdrId(MdrIdDatatype mdrId,
      String value) {

    int patientsWithValue = getPatientsWithValue(mdrId, value);
    int patientsWithMdrId = getPatientsWithMdrId(mdrId);

    return getPercentage(patientsWithValue, patientsWithMdrId);

  }

  @Override
  public double getPercentageOfPatientsWithValueOutOfTotalPatients(MdrIdDatatype mdrId,
      String value) {

    int numberOfPatientsWithValue = getPatientsWithValue(mdrId, value);
    int totalNumberOfPatients = getTotalNumberOfPatients();

    return getPercentage(numberOfPatientsWithValue, totalNumberOfPatients);
  }

  private int getPatientsWithValue(MdrIdDatatype mdrID, String value) {

    QualityResult result = qualityResults.getResult(mdrID, value);
    return (result == null) ? 0 : result.getNumberOfPatients();

  }

  private int countPatients(MdrIdDatatype mdrId, QualityResultPatientIdsGetter processor) {

    Set<String> patientIds = new HashSet<>();

    Set<String> values = qualityResults.getValues(mdrId);

    if (values != null) {
      for (String value : values) {

        QualityResult qualityResult = qualityResults.getResult(mdrId, value);
        Set<String> tempPatientIds = processor.getPatientIds(qualityResult);
        if (tempPatientIds != null) {
          patientIds.addAll(tempPatientIds);
        }

      }
    }

    return patientIds.size();

  }

  private int getPatientsWithMdrId(MdrIdDatatype mdrId) {

    return countPatients(mdrId, qualityResult -> qualityResult.getPatientLocalIds());

  }

  @Override
  public double getPercentageOf_MismatchingPatientsWithValue_outOf_MismatchingPatientsWithMdrId(
      MdrIdDatatype mdrId, String value) {

    int mismatchingPatientsWithValue = getMismatchingPatientsWithValue(mdrId, value);
    int mismatchingPatientsWithMdrId = getMismatchingPatientsWithMdrId(mdrId);

    return getPercentage(mismatchingPatientsWithValue, mismatchingPatientsWithMdrId);

  }

  private int getMismatchingPatientsWithValue(MdrIdDatatype mdrId, String value) {

    QualityResult qualityResult = qualityResults.getResult(mdrId, value);
    return (qualityResult != null && !qualityResult.isValid()) ? qualityResult.getNumberOfPatients()
        : 0;

  }

  private int getMismatchingPatientsWithMdrId(MdrIdDatatype mdrId) {

    return countPatients(mdrId,
        qualityResult -> (qualityResult.isValid()) ? null : qualityResult.getPatientLocalIds());

  }

  @Override
  public double getPercentageOfMismatchingPatientsWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId) {

    int mismatchingPatientsWithMdrId = getMismatchingPatientsWithMdrId(mdrId);
    int patientsWithMdrId = getPatientsWithMdrId(mdrId);

    return getPercentage(mismatchingPatientsWithMdrId, patientsWithMdrId);

  }

  @Override
  public double getPercentageOfMatchingPatientsWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId) {

    int matchingPatientsWithMdrId = getMatchingPatientsWithMdrId(mdrId);
    int patientsWithMdrId = getPatientsWithMdrId(mdrId);

    return getPercentage(matchingPatientsWithMdrId, patientsWithMdrId);

  }

  private int getMatchingPatientsWithMdrId(MdrIdDatatype mdrId) {

    return countPatients(mdrId,
        qualityResult -> (qualityResult.isValid()) ? qualityResult.getPatientLocalIds() : null);

  }

  @Override
  public int getNumberOf_MismatchingPatientsWithMdrId(MdrIdDatatype mdrId) {

    return getMismatchingPatientsWithMdrId(mdrId);

  }

  @Override
  public int getNumberOfMatchingPatientsWithMdrId(MdrIdDatatype mdrId) {

    return getMatchingPatientsWithMdrId(mdrId);

  }

  @Override
  public int getNumberOfPatientsForValidation(MdrIdDatatype mdrId) {

    return getMismatchingPatientsWithMdrId(mdrId);

  }

  @Override
  public int getNumberOfPatientsWithMdrId(MdrIdDatatype mdrId) {

    return getPatientsWithMdrId(mdrId);

  }

  @Override
  public double getPercentageOfPatientsWithMdrIdOutOfTotalPatients(MdrIdDatatype mdrId) {

    int numberOfPatientsWithMdrId = getNumberOfPatientsWithMdrId(mdrId);
    int totalNumberOfPatients = getTotalNumberOfPatients();

    return getPercentage(numberOfPatientsWithMdrId, totalNumberOfPatients);
  }

  @Override
  public int getNumberOfPatientsWithMatchOnlyWithMdrId(MdrIdDatatype mdrId) {

    int numberOfPatientsWithAnyMismatchWithMdrId = getNumberOfPatientsWithAnyMismatchWithMdrId(
        mdrId);
    int numberOfPatientsWithMdrId = getNumberOfPatientsWithMdrId(mdrId);

    return numberOfPatientsWithMdrId - numberOfPatientsWithAnyMismatchWithMdrId;

  }

  @Override
  public double getPercentageOfPatientsWithMatchOnlyWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId) {

    int numberOfPatientsWithMatchOnlyWithMdrId = getNumberOfPatientsWithMatchOnlyWithMdrId(mdrId);
    int numberOfPatientsWithMdrId = getNumberOfPatientsWithMdrId(mdrId);

    return getPercentage(numberOfPatientsWithMatchOnlyWithMdrId, numberOfPatientsWithMdrId);

  }

  @Override
  public double getPercentageOfPatientsWithMatchOnlyWithMdrIdoutOfTotalPatients(
      MdrIdDatatype mdrId) {

    int numberOfPatientsWithMatchOnlyWithMdrId = getNumberOfPatientsWithMatchOnlyWithMdrId(mdrId);
    int totalNumberOfPatients = getTotalNumberOfPatients();

    return getPercentage(numberOfPatientsWithMatchOnlyWithMdrId, totalNumberOfPatients);

  }

  @Override
  public int getNumberOfPatientsWithAnyMismatchWithMdrId(MdrIdDatatype mdrId) {
    return countPatients(mdrId,
        qualityResult -> qualityResult.isValid() ? null : qualityResult.getPatientLocalIds());
  }

  @Override
  public double getPercentageOfPatientsWithAnyMismatchWithMdrIdoutOfPatientsWithMdrId(
      MdrIdDatatype mdrId) {

    int numberOfPatientsWithAnyMismatchWithMdrId = getNumberOfPatientsWithAnyMismatchWithMdrId(
        mdrId);
    int numberOfPatientsWithMdrId = getNumberOfPatientsWithMdrId(mdrId);

    return getPercentage(numberOfPatientsWithAnyMismatchWithMdrId, numberOfPatientsWithMdrId);

  }

  @Override
  public double getPercentageOfPatientsWithAnyMismatchWithMdrIdOutOfTotalPatients(
      MdrIdDatatype mdrId) {

    int numberOfPatientsWithAnyMismatchWithMdrId = getNumberOfPatientsWithAnyMismatchWithMdrId(
        mdrId);
    int totalNumberOfPatients = getTotalNumberOfPatients();

    return getPercentage(numberOfPatientsWithAnyMismatchWithMdrId, totalNumberOfPatients);

  }

  @Override
  public double getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements() {

    int matchingDataelements = getCompletelyMatchingDataelements();
    int allDataelements = getAllDataElements();

    return getPercentage(matchingDataelements, allDataelements);

  }

  private int countAllDateElements(QualityResultConditionEvaluator conditionEvaluator,
      ConditionsEvaluator conditionsEvaluator) {

    int result = 0;

    Set<MdrIdDatatype> mdrIds = qualityResults.getMdrIds();
    for (MdrIdDatatype mdrId : mdrIds) {

      Set<String> values = qualityResults.getValues(mdrId);

      boolean[] conditions = new boolean[values.size()];
      int i = 0;
      for (String value : values) {

        QualityResult qualityResult = qualityResults.getResult(mdrId, value);
        conditions[i] = conditionEvaluator.fullfillsCondition(qualityResult);
        i++;

      }

      if (conditionsEvaluator.evaluate(conditions)) {
        result++;
      }

    }

    return result;

  }

  private int getMatchingDataElements() {

    return countAllDateElements(qualityResult -> qualityResult.isValid(), andConditionsEvaluator);

  }

  private int getAllDataElements() {

    return countAllDateElements(qualityResult -> true, andConditionsEvaluator);

  }

  @Override
  public double getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements() {

    int notCompletelyMismatchingDataelements = getNotCompletelyMismatchingDataelements();
    int allDataelements = getAllDataElements();

    return getPercentage(notCompletelyMismatchingDataelements, allDataelements);

  }

  private int getNotCompletelyMismatchingDataelements() {

    return countAllDateElements(qualityResult -> !qualityResult.isValid(), orConditionsEvaluator);

  }

  @Override
  public double getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements() {

    int completelyMismatchingDataelements = getCompletelyMismatchingDataelements();
    int allDataelements = getAllDataElements();

    return getPercentage(completelyMismatchingDataelements, allDataelements);

  }

  private int getCompletelyMismatchingDataelements() {

    return countAllDateElements(qualityResult -> !qualityResult.isValid(), andConditionsEvaluator);

  }

  private int getCompletelyMatchingDataelements() {

    return countAllDateElements(qualityResult -> qualityResult.isValid(), andConditionsEvaluator);

  }

  @Override
  public double getPercentageOf_NotMappedDataelements_outOf_AllDataelements() {

    int notMappedDataelements = getNotMappedDataelements();
    int allDataelements = getAllDataElements();

    return getPercentage(notMappedDataelements, allDataelements);

  }

  private int getNotMappedDataelements() {

    int result = 0;

    Set<MdrIdDatatype> mdrIds = qualityResults.getMdrIds();
    for (MdrIdDatatype mdrId : mdrIds) {

      if (mdrMappedElements.isMapped(mdrId)) {
        result++;
      }

    }

    return result;

  }

  @Override
  public int getTotalNumberOfPatients() {

    if (totalNumberOfPatients == null) {

      Set<String> patientIds = new HashSet<>();

      for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
        for (String value : qualityResults.getValues(mdrId)) {

          QualityResult result = qualityResults.getResult(mdrId, value);
          patientIds.addAll(result.getPatientLocalIds());

        }
      }

      totalNumberOfPatients = patientIds.size();

    }

    return totalNumberOfPatients;

  }

  @Override
  public double getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement(
      MdrIdDatatype mdrId) {

    int numberOfPatientsForId = getNumberOfPatientsWithMdrId(mdrId);
    int totalNumberOfPatients = getTotalNumberOfPatients();

    return getPercentage(numberOfPatientsForId, totalNumberOfPatients);

  }

  @Override
  public boolean getGeneralRehearsalAContainedInQR(MdrIdDatatype mdrId) {
    return getNumberOfPatientsWithMdrId(mdrId) > 0;
  }

  @Override
  public boolean getGeneralRehearsalBLowMismatch(MdrIdDatatype mdrId) {
    return getPercentageOfPatientsWithAnyMismatchWithMdrIdoutOfPatientsWithMdrId(mdrId) < 10.0d;
  }

  @Override
  public boolean getGeneralRehearsalAAndB(MdrIdDatatype mdrId) {
    return getGeneralRehearsalAContainedInQR(mdrId) && getGeneralRehearsalBLowMismatch(mdrId);
  }

  private interface QualityResultPatientIdsGetter {

    Set<String> getPatientIds(QualityResult qualityResult);
  }


  private interface QualityResultConditionEvaluator {

    boolean fullfillsCondition(QualityResult qualityResult);
  }

  private interface ConditionsEvaluator {

    boolean evaluate(boolean[] conditions);
  }

  private class AndConditionsEvaluator implements ConditionsEvaluator {

    @Override
    public boolean evaluate(boolean[] conditions) {

      boolean result = conditions[0];

      for (boolean condition : conditions) {
        result &= condition;
      }

      return result;
    }

  }

  private class OrConditionsEvaluator implements ConditionsEvaluator {

    @Override
    public boolean evaluate(boolean[] conditions) {

      boolean result = conditions[0];

      for (boolean condition : conditions) {
        result |= condition;
      }

      return result;

    }

  }


}
