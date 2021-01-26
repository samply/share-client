package de.samply.share.client.quality.report.file.csvline;

import de.samply.share.common.utils.MdrIdDatatype;

public class PatientDataCsvLinePatternTest1 extends CsvLineImpl {

  public static final String MATCH = "match";
  public static final String MISMATCH = "basic";


  public PatientDataCsvLinePatternTest1() {
    super(ElementOrder.values().length);
  }

  private String getElement(ElementOrder order) {
    return getElement(order.ordinal());
  }

  private void addElement(ElementOrder order, String element) {
    addElement(order.ordinal(), element);
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public MdrIdDatatype getMdrId() {

    String element = getElement(ElementOrder.MDR_ID);
    return (element != null) ? new MdrIdDatatype(element) : null;

  }

  public void setMdrId(MdrIdDatatype mdrId) {
    addElement(ElementOrder.MDR_ID, mdrId.toString());
  }

  public String getAttributeValue() {
    return getElement(ElementOrder.ATTRIBUTE_VALUE);
  }

  public void setAttributeValue(String attributeValue) {
    addElement(ElementOrder.ATTRIBUTE_VALUE, attributeValue);
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public Boolean isValid() {

    String element = getElement(ElementOrder.IS_VALID);
    return (element != null) ? convertMatchToIsValid(element) : null;

  }

  public void setValid(Boolean valid) {
    addElement(ElementOrder.IS_VALID, convertIsValidToMatch(valid));
  }

  private String convertIsValidToMatch(boolean isValid) {
    return (isValid) ? MATCH : MISMATCH;
  }

  private boolean convertMatchToIsValid(String match) {
    return match.equals(MATCH);
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public Integer getNumberOfPatients() {

    String element = getElement(ElementOrder.NUMBER_OF_PATIENTS);
    return (element != null) ? Integer.valueOf(element) : null;

  }

  public void setNumberOfPatients(Integer numberOfPatients) {
    addElement(ElementOrder.NUMBER_OF_PATIENTS, numberOfPatients.toString());
  }

  public String getMdrName() {
    return getElement(ElementOrder.MDR_NAME);
  }

  public void setMdrName(String mdrName) {
    addElement(ElementOrder.MDR_NAME, mdrName);
  }

  public String getMdrLink() {
    return getElement(ElementOrder.MDR_LINK);
  }

  public void setMdrLink(String mdrLink) {
    addElement(ElementOrder.MDR_LINK, mdrLink);
  }

  public String getMdrType() {
    return getElement(ElementOrder.MDR_TYPE);
  }

  public void setMdrType(String mdrType) {
    addElement(ElementOrder.MDR_TYPE, mdrType);
  }

  private enum ElementOrder {
    MDR_ID, MDR_NAME, MDR_LINK, ATTRIBUTE_VALUE, MDR_TYPE, IS_VALID, NUMBER_OF_PATIENTS
  }


}
