package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.file.excel.cell.element.DoubleExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.IntegerExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.LinkExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.NaturalLanguageBooleanExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.common.utils.MdrIdDatatype;

public class DataElementStatsExcelRowElements extends ExcelRowElements {

  public DataElementStatsExcelRowElements() {
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
   * Todo.
   *
   * @param link  Todo.
   * @param mdrId Todo.
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
   * @param numberOfPatientsWithDataElement Todo.
   */
  public void setNumberOfPatientsWithDataElement(Integer numberOfPatientsWithDataElement) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithDataElement);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

  }

  /**
   * Todo.
   *
   * @param percentageOfPatientsWithDataElementOutOfTotalPatients Todo.
   */
  public void setPercentageOfPatientsWithDataElementOutOfTotalPatients(
      Double percentageOfPatientsWithDataElementOutOfTotalPatients) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithDataElementOutOfTotalPatients);
    addElement(ElementOrder.PERCENTAGE_OF_PATIENTS_WITH_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS,
        excelCellElement);

  }

  /**
   * Todo.
   *
   * @param numberOfPatientsWithMatchOnlyForDataElement Todo.
   */
  public void setNumberOfPatientsWithMatchOnlyForDataElement(
      Integer numberOfPatientsWithMatchOnlyForDataElement) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithMatchOnlyForDataElement);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT, excelCellElement);


  }

  /**
   * Todo.
   *
   * @param percentageOfPatientsWithMatchOnlyForDataElementOutOfPatientsWithDataElement Todo.
   */
  public void setPercentageOfPatientsWithMatchOnlyForDataElementOutOfPatientsWithDataElement(
      Double percentageOfPatientsWithMatchOnlyForDataElementOutOfPatientsWithDataElement) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithMatchOnlyForDataElementOutOfPatientsWithDataElement);
    addElement(ElementOrder
            .PERCENTAGE_OF_PATIENTS_WITH_MATCHONLY_FOR_DATAELEMENT_OUT_OF_PATIENTS_WITH_DATAELEMENT,
        excelCellElement);

  }

  /**
   * Todo.
   *
   * @param percentageOfPatientsWithMatchOnlyForDataElementOutOfTotalPatients Todo.
   */
  public void setPercentageOfPatientsWithMatchOnlyForDataElementOutOf_TotalPatients(
      Double percentageOfPatientsWithMatchOnlyForDataElementOutOfTotalPatients) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithMatchOnlyForDataElementOutOfTotalPatients);
    addElement(
        ElementOrder.PERCENTAGE_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS,
        excelCellElement);

  }

  /**
   * Todo.
   *
   * @param numberOfPatientsWithAnyMismatchForDataElement Todo.
   */
  public void setNumberOfPatientsWithAnyMismatchForDataElement(
      Integer numberOfPatientsWithAnyMismatchForDataElement) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithAnyMismatchForDataElement);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT,
        excelCellElement);

  }

  /**
   * Todo.
   *
   * @param percentageOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement Todo.
   */
  public void setPercentageOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement(
      Double percentageOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement);
    addElement(
        ElementOrder.PercentAgeOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement,
        excelCellElement);

  }

  /**
   * ToDo.
   *
   * @param percentageOfPatientsWithAnyMismatchForDataElementOutOfTotalPatients ToDo.
   */
  public void setPercentageOfPatientsWithAnyMismatchForDataElementOutOfTotalPatients(
      Double percentageOfPatientsWithAnyMismatchForDataElementOutOfTotalPatients) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithAnyMismatchForDataElementOutOfTotalPatients);
    addElement(
        ElementOrder.PERCENTAGE_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATAELEMENT_OUT_OF_TOTAL_PATIENTS,
        excelCellElement);

  }

  /**
   * ToDo.
   *
   * @param priorization ToDo.
   */
  public void setGeneralRehearsalPriorization(String priorization) {

    StringExcelCellElement cellElement = new StringExcelCellElement(priorization);
    addElement(ElementOrder.GENERAL_REHEARSAL_PRIORIZATION, cellElement);

  }

  /**
   * ToDo.
   *
   * @param value ToDo.
   */
  public void setGeneralRehearsalAContainedInQR(boolean value) {
    NaturalLanguageBooleanExcelCellElement excelCellElement =
        new NaturalLanguageBooleanExcelCellElement(value);
    addElement(ElementOrder.GENERAL_REHEARSAL_A_CONTAINED_IN_QR, excelCellElement);

  }

  /**
   * ToDo.
   *
   * @param value ToDo.
   */
  public void setGeneralRehearsalBLowMismatch(boolean value) {

    NaturalLanguageBooleanExcelCellElement excelCellElement =
        new NaturalLanguageBooleanExcelCellElement(value);
    addElement(ElementOrder.GENERAL_REHEARSAL_B_LOW_MISMATCH, excelCellElement);

  }

  /**
   * ToDo.
   *
   * @param value ToDo.
   */
  public void setGeneralRehearsalAAndB(boolean value) {

    NaturalLanguageBooleanExcelCellElement excelCellElement =
        new NaturalLanguageBooleanExcelCellElement(value);
    addElement(ElementOrder.GENERAL_REHEARSAL_A_AND_B, excelCellElement);

  }

  public enum ElementOrder {

    MDR_LINK("id"),
    DKTK_ID("DKTK-id"),
    MDR_DATEN_ELEMENT("dataelement MDR"),
    CXX_DATEN_ELEMENT("dataelement CXX"),
    NUMBER_OF_PATIENTS_WITH_DATA_ELEMENT("Number of patients with entry for this element"),
    PERCENTAGE_OF_PATIENTS_WITH_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS("% of patients - total"),
    NUMBER_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT("Number of patients with match only"),
    PERCENTAGE_OF_PATIENTS_WITH_MATCHONLY_FOR_DATAELEMENT_OUT_OF_PATIENTS_WITH_DATAELEMENT(
        "% of patients with entry for this element"),
    PERCENTAGE_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS(
        "% of patients - total"),
    NUMBER_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT("Number of patients with any mismatch"),
    PercentAgeOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement(
        "% of patients with entry for this element"),
    PERCENTAGE_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATAELEMENT_OUT_OF_TOTAL_PATIENTS(
        "% of patients - total"),
    GENERAL_REHEARSAL_PRIORIZATION("Priorisierung für GP"),
    GENERAL_REHEARSAL_A_CONTAINED_IN_QR("a) im QB enthalten [ja/nein]"),
    GENERAL_REHEARSAL_B_LOW_MISMATCH("b) im QB mismatch <10% zur MDR-Vorgabe [ja/nein]"),
    GENERAL_REHEARSAL_A_AND_B("a,b  ist \"ja\" [ja/nein]");


    private final String title;

    ElementOrder(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
  }


}
