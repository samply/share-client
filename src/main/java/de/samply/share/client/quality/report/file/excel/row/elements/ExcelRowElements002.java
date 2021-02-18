package de.samply.share.client.quality.report.file.excel.row.elements;

import de.samply.share.client.quality.report.file.excel.cell.element.DoubleExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.IntegerExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.LinkExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.MatchElement;
import de.samply.share.client.quality.report.file.excel.cell.element.MatchExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.reference.CellReference;
import de.samply.share.client.quality.report.file.excel.cell.reference.CellReferenceExcelCellElement;
import de.samply.share.common.utils.MdrIdDatatype;

public class ExcelRowElements002 extends ExcelRowElements {

  public ExcelRowElements002() {
    super(ElementOrder.values().length);
  }

  @Override
  public ExcelCellElement getElementTitle(int order) {

    String title =
        (order >= 0 && order < ElementOrder.values().length) ? ElementOrder.values()[order]
            .getTitle() : "";
    return new StringExcelCellElement(title);

  }

  private void addElement(ElementOrder elementOrder, ExcelCellElement element) {
    addElement(elementOrder.ordinal(), element);
  }

  private ExcelCellElement getElement(ElementOrder elementOrder) {
    return getElement(elementOrder.ordinal());
  }

  /**
   * Set mdr data element.
   *
   * @param mdrDatenElement mdr data element designation.
   */
  public void setMdrDatenElement(String mdrDatenElement) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrDatenElement);
    addElement(ElementOrder.MDR_DATEN_ELEMENT, cellElement);

  }

  /**
   * Set centraxx data element.
   *
   * @param cxxDatenElement centraxx data element.
   */
  public void setCxxDatenElement(String cxxDatenElement) {

    StringExcelCellElement cellElement = new StringExcelCellElement(cxxDatenElement);
    addElement(ElementOrder.CXX_DATEN_ELEMENT, cellElement);

  }

  /**
   * Set mdr slot DKTK-ID.
   *
   * @param dktkId mdr slot DKTK-ID.
   */
  public void setDktkId(String dktkId) {

    StringExcelCellElement cellElement = new StringExcelCellElement(dktkId);
    addElement(ElementOrder.DKTK_ID, cellElement);

  }

  /**
   * Set link to mdr web interface.
   *
   * @param link  link to mdr web interface, with description of mdr data element.
   * @param mdrId mdr data element id.
   */
  public void setMdrLink(String link, MdrIdDatatype mdrId) {

    String title = getLinkTitle(mdrId);
    LinkExcelCellElement cellElement = new LinkExcelCellElement<String>(link, title);
    addElement(ElementOrder.MDR_LINK, cellElement);

  }

  private String getLinkTitle(MdrIdDatatype mdrId) {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(mdrId.getNamespace());
    stringBuilder.append(':');
    stringBuilder.append(mdrId.getId());
    stringBuilder.append(':');
    stringBuilder.append(mdrId.getVersion());

    return stringBuilder.toString();

  }

  /**
   * Set value of mdr attribute.
   *
   * @param mdrAttributeValue value of mdr attribute.
   */
  public void setMdrAttributeValue(String mdrAttributeValue) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrAttributeValue);
    addElement(ElementOrder.MDR_ATTRIBUTE_VALUE, cellElement);

  }

  /**
   * Set value of centraxx attribute.
   *
   * @param cxxAttributeValue value of centraxx attribute.
   */
  public void setCxxAttributeValue(String cxxAttributeValue) {

    StringExcelCellElement cellElement = new StringExcelCellElement(cxxAttributeValue);
    addElement(ElementOrder.CXX_ATTRIBUTE_VALUE, cellElement);

  }

  /**
   * Set type of mdr data element.
   *
   * @param mdrType type of mdr dataelement.
   */
  public void setMdrType(String mdrType) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrType);
    addElement(ElementOrder.MDR_TYPE, cellElement);

  }

  /**
   * Set if mdr data element is valid.
   *
   * @param isValid          check if is valid.
   * @param numberOfPatients number of patients that are valid/invalid.
   */
  public void setValid(boolean isValid, int numberOfPatients) {
    setValid(isValid, numberOfPatients, null);
  }
  //    public void setValid (boolean isValid, int numberOfPatients){
  //
  //       MatchExcelCellElement cellElement = new MatchExcelCellElement(isValid, numberOfPatients);
  //       addElement(ElementOrder.IS_VALID, cellElement);
  //
  //    }

  /**
   * Set of mdr data element is valid.
   *
   * @param isValid          check if is valid.
   * @param numberOfPatients number of patients that are valid/invalid.
   * @param cellReference    reference (link) to another cell .
   */
  public void setValid(boolean isValid, int numberOfPatients, CellReference cellReference) {

    MatchElement matchElement = new MatchElement(numberOfPatients, isValid);
    ExcelCellElement cellElement =
        (cellReference != null) ? new CellReferenceExcelCellElement<MatchElement>(cellReference,
            matchElement) : new MatchExcelCellElement(matchElement);
    addElement(ElementOrder.IS_VALID, cellElement);

  }

  /**
   * Set of the mdr element is not mapped.
   */
  public void setNotMapped() {

    MatchElement matchElement = new MatchElement(0, true);
    matchElement.setNotMapped();
    MatchExcelCellElement matchExcelCellElement = new MatchExcelCellElement(matchElement);

    addElement(ElementOrder.IS_VALID, matchExcelCellElement);

  }

  /**
   * Set number of patients.
   *
   * @param numberOfPatients number of patients.
   */
  public void setNumberOfPatients(int numberOfPatients) {
    IntegerExcelCellElement cellElement = new IntegerExcelCellElement(numberOfPatients);
    addElement(ElementOrder.NUMBER_OF_PATIENTS, cellElement);
  }

  /**
   * Set percentage out of patient with data element.
   *
   * @param percentageOfPatientsWithDataElement percentage out of patient with data element.
   */
  public void setPercentageOutOfPatientsWithDataElement(
      Double percentageOfPatientsWithDataElement) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithDataElement);
    addElement(ElementOrder.PERCENTAGE_OUT_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

  }

  //    public void setNumberOfPatients (CellReference cellReference, int numberOfPatients){
  //
  //        CellReferenceExcelCellElement cellElement =
  //        new CellReferenceExcelCellElement(cellReference, "" + numberOfPatients);
  //        addElement(ElementOrder.NUMBER_OF_PATIENTS, cellElement);
  //
  //    }

  /**
   * Set percentage out of total patients.
   *
   * @param percentageOfTotalPatients percentage out of total patients  .
   */
  public void setPercentageOutOfTotalPatients(Double percentageOfTotalPatients) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfTotalPatients);
    addElement(ElementOrder.PERCENTAGE_OUT_OF_TOTAL_PATIENTS, excelCellElement);

  }

  public enum ElementOrder {

    MDR_LINK("id"),
    DKTK_ID("DKTK-id"),
    MDR_DATEN_ELEMENT("dataelement MDR"),
    CXX_DATEN_ELEMENT("dataelement CXX"),
    MDR_TYPE("datatype MDR"),
    MDR_ATTRIBUTE_VALUE("value MDR"),
    CXX_ATTRIBUTE_VALUE("value CXX"),
    IS_VALID("validation"),
    NUMBER_OF_PATIENTS("number of patients CXX"),
    PERCENTAGE_OUT_OF_PATIENTS_WITH_DATA_ELEMENT("% of patients with entry for this element"),
    PERCENTAGE_OUT_OF_TOTAL_PATIENTS("% of patients - total");

    private final String title;

    ElementOrder(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
  }


}
