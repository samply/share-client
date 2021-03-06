package de.samply.share.client.quality.report.file.csvline;

import de.samply.share.common.utils.MdrIdDatatype;

public final class CsvLine002 extends CsvLineImpl {


  public CsvLine002() {
    super(ElementOrder.values().length);
  }

  private String getElement(ElementOrder order) {
    return getElement(order.ordinal());
  }

  private void addElement(ElementOrder order, String element) {
    addElement(order.ordinal(), element);
  }

  /**
   * Get mdr id of csv line.
   *
   * @return mdr id.
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

  /**
   * Set attribute value of an mdr data element in csv line.
   *
   * @param attributeValue attribute value.
   */
  public void setAttributeValue(String attributeValue) {
    addElement(ElementOrder.ATTRIBUTE_VALUE, attributeValue);
  }

  /**
   * Checks if mdr id is valid according to the mdr validations.
   *
   * @return Todo.
   */
  public Boolean isValid() {

    String element = getElement(ElementOrder.IS_VALID);
    return (element != null) ? Boolean.valueOf(element) : null;

  }

  public void setValid(Boolean valid) {
    addElement(ElementOrder.IS_VALID, valid.toString());
  }

  /**
   * Get number of patients with a pair data element - attribute.
   *
   * @return Number of patients.
   */
  public Integer getNumberOfPatients() {

    String element = getElement(ElementOrder.NUMBER_OF_PATIENTS);
    return (element != null) ? Integer.valueOf(element) : null;

  }

  public void setNumberOfPatients(Integer numberOfPatients) {
    addElement(ElementOrder.NUMBER_OF_PATIENTS, numberOfPatients.toString());
  }

  private enum ElementOrder {
    MDR_ID, ATTRIBUTE_VALUE, IS_VALID, NUMBER_OF_PATIENTS
  }


}
