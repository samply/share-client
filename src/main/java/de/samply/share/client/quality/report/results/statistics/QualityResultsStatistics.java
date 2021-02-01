package de.samply.share.client.quality.report.results.statistics;

import de.samply.share.common.utils.MdrIdDatatype;

public interface QualityResultsStatistics {


  //1
  double getPercentageOfPatientsWithValueOutOfPatientsWithMdrId(MdrIdDatatype mdrId,
      String value);

  //2
  double getPercentageOfPatientsWithValueOutOfTotalPatients(MdrIdDatatype mdrId,
      String value);

  double getPercentageOf_MismatchingPatientsWithValue_outOf_MismatchingPatientsWithMdrId(
      MdrIdDatatype mdrId, String value);


  double getPercentageOfMismatchingPatientsWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  double getPercentageOfMatchingPatientsWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  int getNumberOf_MismatchingPatientsWithMdrId(MdrIdDatatype mdrId);

  int getNumberOfMatchingPatientsWithMdrId(MdrIdDatatype mdrId);

  int getNumberOfPatientsForValidation(MdrIdDatatype mdrId);

  // 3
  int getNumberOfPatientsWithMdrId(MdrIdDatatype mdrId);

  //4
  double getPercentageOfPatientsWithMdrIdOutOfTotalPatients(MdrIdDatatype mdrId);

  //5
  int getNumberOfPatientsWithMatchOnlyWithMdrId(MdrIdDatatype mdrId);

  //6
  double getPercentageOfPatientsWithMatchOnlyWithMdrIdOutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  //7
  double getPercentageOfPatientsWithMatchOnlyWithMdrIdoutOfTotalPatients(
      MdrIdDatatype mdrId);

  //8
  int getNumberOfPatientsWithAnyMismatchWithMdrId(MdrIdDatatype mdrId);

  //9
  double getPercentageOfPatientsWithAnyMismatchWithMdrIdoutOfPatientsWithMdrId(
      MdrIdDatatype mdrId);

  //10
  double getPercentageOfPatientsWithAnyMismatchWithMdrIdOutOfTotalPatients(
      MdrIdDatatype mdrId);

  double getPercentageOfPatientsOutOfTotalNumberOfPatientsForADataelement(
      MdrIdDatatype mdrId);


  double getPercentageOf_CompletelyMatchingDataelements_outOf_AllDataelements();

  double getPercentageOf_NotCompletelyMismatchingDataelements_outOf_AllDataelements();

  double getPercentageOf_CompletelyMismatchingDataelements_outOf_AllDataelements();

  double getPercentageOf_NotMappedDataelements_outOf_AllDataelements();

  int getTotalNumberOfPatients();


}
