package de.samply.share.client.quality.report.results.statistics;

import de.samply.share.common.utils.MdrIdDatatype;

public interface QualityResultsStatistics {


  //1
  public double getPercentageOfPatientsWithValueOutOfPatientsWithMdrId(MdrIdDatatype mdrId,
      String value);

  //2
  public double getPercentageOfPatientsWithValueOutOfTotalPatients(MdrIdDatatype mdrId,
      String value);

  public double getPercentageOf_MismatchingPatientsWithValue_outOf_MismatchingPatientsWithMdrId(
      MdrIdDatatype mdrId, String value);


  public double getPercentageOfMismatchingPatientsWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  public double getPercentageOfMatchingPatientsWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  public int getNumberOf_MismatchingPatientsWithMdrId(MdrIdDatatype mdrId);

  public int getNumberOfMatchingPatientsWithMdrId(MdrIdDatatype mdrId);

  public int getNumberOfPatientsForValidation(MdrIdDatatype mdrId);

  // 3
  public int getNumberOfPatientsWithMdrId(MdrIdDatatype mdrId);

  //4
  public double getPercentageOfPatientsWithMdrIdOutOfTotalPatients(MdrIdDatatype mdrId);

  //5
  public int getNumberOfPatientsWithMatchOnlyWithMdrId(MdrIdDatatype mdrId);

  //6
  public double getPercentageOfPatientsWithMatchOnlyWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  //7
  public double getPercentageOfPatientsWithMatchOnlyWithMdrIdoutOfTotalPatients(
      MdrIdDatatype mdrId);

  //8
  public int getNumberOfPatientsWithAnyMismatchWithMdrId(MdrIdDatatype mdrId);

  //9
  public double getPercentageOfPatientsWithAnyMismatchWithMdrIdoutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  //10
  public double getPercentageOfPatientsWithAnyMismatchWithMdrIdOutOfTotalPatients(
      MdrIdDatatype mdrId);

  public double getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement(
      MdrIdDatatype mdrId);


  public double getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements();

  public double getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements();

  public double getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements();

  public double getPercentageOf_NotMappedDataelements_outOf_AllDataelements();

  public int getTotalNumberOfPatients();


}
