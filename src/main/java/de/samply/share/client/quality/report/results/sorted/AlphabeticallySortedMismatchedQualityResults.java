package de.samply.share.client.quality.report.results.sorted;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.Collections;

public class AlphabeticallySortedMismatchedQualityResults extends SortedQualityResultsImpl {

  public AlphabeticallySortedMismatchedQualityResults(QualityResults qualityResults) {
    super(qualityResults);
  }

  @Override
  protected ArrayList<MdrIdValue> sortQualityResults(QualityResults qualityResults) {

    ArrayList<MdrIdValue> mdrIdValueArrayList = new ArrayList<>();

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
      for (String value : qualityResults.getValues(mdrId)) {

        QualityResult result = qualityResults.getResult(mdrId, value);
        if (!result.isValid()) {

          MdrIdValue mdrIdValue = new MdrIdValue(mdrId, value);
          mdrIdValueArrayList.add(mdrIdValue);

        }

      }

    }

    Collections.sort(mdrIdValueArrayList, new MdrIdValueComparatorImpl());

    return mdrIdValueArrayList;

  }

}
