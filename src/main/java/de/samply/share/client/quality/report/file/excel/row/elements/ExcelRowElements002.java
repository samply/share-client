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
   * Todo.
   *
   * @param mdrDatenElement Todo.
   */
  public void setMdrDatenElement(String mdrDatenElement) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrDatenElement);
    addElement(ElementOrder.MDR_DATEN_ELEMENT, cellElement);

  }

  /**
   * Todo.
   *
   * @param cxxDatenElement Todo.
   */
  public void setCxxDatenElement(String cxxDatenElement) {

    StringExcelCellElement cellElement = new StringExcelCellElement(cxxDatenElement);
    addElement(ElementOrder.CXX_DATEN_ELEMENT, cellElement);

  }

  /**
   * Todo.
   *
   * @param dktkId Todo.
   */
  public void setDktkId(String dktkId) {

    StringExcelCellElement cellElement = new StringExcelCellElement(dktkId);
    addElement(ElementOrder.DKTK_ID, cellElement);

  }

  /**
   * Todo.
   *
   * @param link  Todo.
   * @param mdrId Todo.
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
   * Todo.
   *
   * @param mdrAttributeValue Todo.
   */
  public void setMdrAttributeValue(String mdrAttributeValue) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrAttributeValue);
    addElement(ElementOrder.MDR_ATTRIBUTE_VALUE, cellElement);

  }

  /**
   * Todo.
   *
   * @param cxxAttributeValue Todo.
   */
  public void setCxxAttributeValue(String cxxAttributeValue) {

    StringExcelCellElement cellElement = new StringExcelCellElement(cxxAttributeValue);
    addElement(ElementOrder.CXX_ATTRIBUTE_VALUE, cellElement);

  }

  /**
   * Todo.
   *
   * @param mdrType Todo.
   */
  public void setMdrType(String mdrType) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrType);
    addElement(ElementOrder.MDR_TYPE, cellElement);

  }

  /**
   * Todo.
   *
   * @param isValid          Todo.
   * @param numberOfPatients Todo.
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
   * Todo.
   *
   * @param isValid          Todo.
   * @param numberOfPatients Todo.
   * @param cellReference    Todo.
   */
  public void setValid(boolean isValid, int numberOfPatients, CellReference cellReference) {

    MatchElement matchElement = new MatchElement(numberOfPatients, isValid);
    ExcelCellElement cellElement =
        (cellReference != null) ? new CellReferenceExcelCellElement<MatchElement>(cellReference,
            matchElement) : new MatchExcelCellElement(matchElement);
    addElement(ElementOrder.IS_VALID, cellElement);

  }

  /**
   * Todo.
   */
  public void setNotMapped() {

    MatchElement matchElement = new MatchElement(0, true);
    matchElement.setNotMapped();
    MatchExcelCellElement matchExcelCellElement = new MatchExcelCellElement(matchElement);

    addElement(ElementOrder.IS_VALID, matchExcelCellElement);

  }

  /**
   * Todo.
   *
   * @param numberOfPatients Todo.
   */
  public void setNumberOfPatients(int numberOfPatients) {
    IntegerExcelCellElement cellElement = new IntegerExcelCellElement(numberOfPatients);
    addElement(ElementOrder.NUMBER_OF_PATIENTS, cellElement);
  }

  /**
   * Todo.
   *
   * @param percentageOfPatientsWithDataElement Todo.
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
   * Todo.
   *
   * @param percentageOfTotalPatients Todo.
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
