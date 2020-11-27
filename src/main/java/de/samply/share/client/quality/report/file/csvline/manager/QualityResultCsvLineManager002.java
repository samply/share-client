package de.samply.share.client.quality.report.file.csvline.manager;

import de.samply.share.client.quality.report.file.csvline.CsvLine002;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

public class QualityResultCsvLineManager002 implements QualityResultCsvLineManager {


  @Override
  public String createLine(MdrIdDatatype mdrId, String value, QualityResult qualityResult) {

    CsvLine002 csvLine = new CsvLine002();

    csvLine.setMdrId(mdrId);
    csvLine.setAttributeValue(value);
    csvLine.setValid(qualityResult.isValid());
    csvLine.setNumberOfPatients(qualityResult.getNumberOfPatients());

    return csvLine.createLine();

  }

  @Override
  public QualityResults parseLineAndAddToQualityResults(String line,
      QualityResults qualityResults) {

    CsvLine002 csvLine = new CsvLine002();

    csvLine.parseValuesOfLine(line);

    MdrIdDatatype mdrId = csvLine.getMdrId();
    String attributeValue = csvLine.getAttributeValue();
    Integer numberOfPatients = csvLine.getNumberOfPatients();
    Boolean valid = csvLine.isValid();

    if (mdrId != null && attributeValue != null && valid != null && numberOfPatients != null) {

      QualityResult qualityResult = new QualityResult();

      qualityResult.setValid(valid);
      qualityResult.setNumberOfPatients(numberOfPatients);

      qualityResults.put(mdrId, attributeValue, qualityResult);

    }

    return qualityResults;

  }

}
