package de.samply.share.client.quality.report.results.filter;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class QualityResultsValidValueFilter extends QualityResultsFilter {

  private final Map<MdrIdDatatype, ValidValueFilterQualityResults> filteredQualityResults =
      new HashMap<>();

  public QualityResultsValidValueFilter(QualityResults qualityResults) {
    super(qualityResults);
  }

  protected abstract boolean isFilterCondition(MdrIdDatatype mdrId);

  @Override
  public Set<String> getValues(MdrIdDatatype mdrId) {

    if (isFilterCondition(mdrId)) {

      filterValidValues(mdrId);
      return filteredQualityResults.get(mdrId).getValues();

    } else {

      return super.getValues(mdrId);

    }

  }

  @Override
  public QualityResult getResult(MdrIdDatatype mdrId, String value) {

    if (isFilterCondition(mdrId)) {

      filterValidValues(mdrId);
      return filteredQualityResults.get(mdrId).getResult(value);

    } else {

      return super.getResult(mdrId, value);

    }

  }

  private void filterValidValues(MdrIdDatatype mdrId) {

    if (!filteredQualityResults.containsKey(mdrId)) {

      ValidValueFilterQualityResults validFilterQualityResults =
          new ValidValueFilterQualityResults();

      for (String value : super.getValues(mdrId)) {

        QualityResult qualityResult = super.getResult(mdrId, value);
        validFilterQualityResults.addValueAndQualityResult(value, qualityResult);

      }

      filteredQualityResults.put(mdrId, validFilterQualityResults);

    }

  }

}
