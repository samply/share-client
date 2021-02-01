package de.samply.share.client.quality.report.file.excel.instances.statistics.table2;

import de.samply.share.client.quality.report.file.excel.cell.element.DoubleExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.IntegerExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.LinkExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.common.utils.MdrIdDatatype;

public class Table2ExcelRowElements extends ExcelRowElements {

  public Table2ExcelRowElements() {
    super(ElementOrder.values().length);
  }

  private void addElement(ElementOrder elementOrder, ExcelCellElement element) {
    addElement(elementOrder.ordinal(), element);
  }

  @Override
  public ExcelCellElement getElementTitle(int order) {

    String title =
        (order >= 0 && order < ElementOrder.values().length) ? ElementOrder.values()[order]
            .getTitle() : "";
    return new StringExcelCellElement(title);

  }

  /**
   * Set link to meta data repository for mdr data element mdr id.
   *
   * @param link  link to meta data repository for mdr data element mdr id.
   * @param mdrId mdr data element id.
   */
  public void setMdrLink(String link, MdrIdDatatype mdrId) {

    String title = getLinkTitle(mdrId);
    LinkExcelCellElement cellElement = new LinkExcelCellElement(link, title);
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
   * Set mdr slot DKTK-ID.
   *
   * @param dktkId mdr slot DKTK-ID.
   */
  public void setDktkId(String dktkId) {

    StringExcelCellElement cellElement = new StringExcelCellElement(dktkId);
    addElement(ElementOrder.DKTK_ID, cellElement);

  }

  /**
   * Set mdr data element designation.
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
   * Set patient for validation.
   *
   * @param patientsForValidation patients for validation.
   */
  public void setPatientsForValidation(Integer patientsForValidation) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(patientsForValidation);
    addElement(ElementOrder.PATIENTS_FOR_VALIDATION, excelCellElement);

  }

  /**
   * Se patients for id.
   *
   * @param patientsForId patients for id.
   */
  public void setPatientsForId(Integer patientsForId) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(patientsForId);
    addElement(ElementOrder.PATIENTS_FOR_ID, excelCellElement);

  }

  /**
   * Set ratio.
   *
   * @param ratio ratio.
   */
  public void setRatio(Double ratio) {

    DoubleExcelCellElement doubleExcelCellElement = new DoubleExcelCellElement(ratio);
    addElement(ElementOrder.RATIO, doubleExcelCellElement);

  }

  /**
   * Set number of patients with mismatch.
   *
   * @param numberOfPatientsWithMismatch Number of patients with mismatch.
   */
  public void setNumberOfPatientsWithMismatch(Integer numberOfPatientsWithMismatch) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithMismatch);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_MISMATCH, excelCellElement);

  }

  /**
   * Set number of patients with match.
   *
   * @param numberOfPatientsWithMatch Number of patients with match.
   */
  public void setNumberOfPatientsWithMatch(Integer numberOfPatientsWithMatch) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithMatch);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_MATCH, excelCellElement);

  }

  /**
   * Set percentage of patients with mismatch.
   *
   * @param percentageOfPatientsWithMismatch percentage of patients with mismatch.
   */
  public void setPercentageOfPatientsWithMismatch(Double percentageOfPatientsWithMismatch) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithMismatch);
    addElement(ElementOrder.PERCENTAGE_OF_PATIENTS_WITH_MISMATCH, excelCellElement);

  }

  /**
   * Set percentage of patients with match.
   *
   * @param percentageOfPatientsWithMatch percentage of patients with match.
   */
  public void setPercentageOfPatientsWithMatch(Double percentageOfPatientsWithMatch) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithMatch);
    addElement(ElementOrder.PERCENTAGE_OF_PATIENTS_WITH_MATCH, excelCellElement);

  }

  /**
   * Set percentage of total patients.
   *
   * @param percentageOfTotalPatients percentage of total patients.
   */
  public void setPercentageOfTotalPatients(Double percentageOfTotalPatients) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(percentageOfTotalPatients);
    addElement(ElementOrder.PERCENTAGE_OF_TOTAL_PATIENTS, excelCellElement);

  }

  public enum ElementOrder {

    MDR_LINK("id"),
    DKTK_ID("DKTK-id"),
    MDR_DATEN_ELEMENT("dataelement MDR"),
    CXX_DATEN_ELEMENT("dataelement CXX"),
    PATIENTS_FOR_VALIDATION("Patienten zu Validierung"),
    PATIENTS_FOR_ID("Patienten zu ID"),
    RATIO("Anteil"),
    PERCENTAGE_OF_TOTAL_PATIENTS("Percentage of total patients"),
    NUMBER_OF_PATIENTS_WITH_MATCH("Number of patients with match"),
    NUMBER_OF_PATIENTS_WITH_MISMATCH("Number of patients with mismatch"),
    PERCENTAGE_OF_PATIENTS_WITH_MATCH("Percentage of patients with match"),
    PERCENTAGE_OF_PATIENTS_WITH_MISMATCH("Percentage of patients with mismatch");


    private final String title;

    ElementOrder(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
  }


}
