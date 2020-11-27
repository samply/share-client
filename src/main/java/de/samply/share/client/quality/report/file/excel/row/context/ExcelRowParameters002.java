package de.samply.share.client.quality.report.file.excel.row.context;

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.common.utils.MdrIdDatatype;

public class ExcelRowParameters002 {


  private MdrIdDatatype mdrId;
  private String value;
  private QualityResult qualityResult;
  private Integer mismatchOrdinal;
  private Double percentageOutOfPatientWithDataElement;
  private Double percentageOutOfTotalPatients;


  public MdrIdDatatype getMdrId() {
    return mdrId;
  }

  public void setMdrId(MdrIdDatatype mdrId) {
    this.mdrId = mdrId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public QualityResult getQualityResult() {
    return qualityResult;
  }

  public void setQualityResult(QualityResult qualityResult) {
    this.qualityResult = qualityResult;
  }

  public Integer getMismatchOrdinal() {
    return mismatchOrdinal;
  }

  public void setMismatchOrdinal(Integer mismatchOrdinal) {
    this.mismatchOrdinal = mismatchOrdinal;
  }

  public Double getPercentageOutOfPatientWithDataElement() {
    return percentageOutOfPatientWithDataElement;
  }

  public void setPercentageOutOfPatientWithDataElement(
      Double percentageOutOfPatientWithDataElement) {
    this.percentageOutOfPatientWithDataElement = percentageOutOfPatientWithDataElement;
  }

  public Double getPercentageOutOfTotalPatients() {
    return percentageOutOfTotalPatients;
  }

  public void setPercentageOutOfTotalPatients(Double percentageOutOfTotalPatients) {
    this.percentageOutOfTotalPatients = percentageOutOfTotalPatients;
  }
}
