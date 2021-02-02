package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.file.excel.cell.element.DoubleExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.ExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.IntegerExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.LinkExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.NaturalLanguageBooleanExcelCellElement;
import de.samply.share.client.quality.report.file.excel.cell.element.StringExcelCellElement;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.common.utils.MdrIdDatatype;

/**
 * Statistics of quality report. More information on: Methods Inf Med 2019; 58(02/03): 086-093 DOI:
 * 10.1055/s-0039-1693685
 */
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
   * Set link for a mdr data element.
   *
   * @param link  link of an mdr data element.
   * @param mdrId id of the mdr data element.
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
   * Set the mdr data element slot DKTK-ID.
   *
   * @param dktkId DKTK-ID.
   */
  public void setDktkId(String dktkId) {

    StringExcelCellElement cellElement = new StringExcelCellElement(dktkId);
    addElement(ElementOrder.DKTK_ID, cellElement);

  }

  /**
   * Set mdr designation of mdr data element.
   *
   * @param mdrDatenElement mdr data element designation.
   */
  public void setMdrDatenElement(String mdrDatenElement) {

    StringExcelCellElement cellElement = new StringExcelCellElement(mdrDatenElement);
    addElement(ElementOrder.MDR_DATEN_ELEMENT, cellElement);

  }

  /**
   * set centraxx data element.
   *
   * @param cxxDatenElement centraxx data element.
   */
  public void setCxxDatenElement(String cxxDatenElement) {

    StringExcelCellElement cellElement = new StringExcelCellElement(cxxDatenElement);
    addElement(ElementOrder.CXX_DATEN_ELEMENT, cellElement);

  }

  /**
   * Set number of patients with mdr data element.
   *
   * @param numberOfPatientsWithDataElement number of patients with mdr data element.
   */
  public void setNumberOfPatientsWithDataElement(Integer numberOfPatientsWithDataElement) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithDataElement);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_DATA_ELEMENT, excelCellElement);

  }

  /**
   * Set percentage of patients with data element out of total patients.
   *
   * @param percentageOfPatientsWithDataElementOutOfTotalPatients percentage of patients with data
   *                                                              element out of total patients.
   */
  public void setPercentageOfPatientsWithDataElementOutOfTotalPatients(
      Double percentageOfPatientsWithDataElementOutOfTotalPatients) {

    DoubleExcelCellElement excelCellElement = new DoubleExcelCellElement(
        percentageOfPatientsWithDataElementOutOfTotalPatients);
    addElement(ElementOrder.PERCENTAGE_OF_PATIENTS_WITH_DATA_ELEMENT_OUT_OF_TOTAL_PATIENTS,
        excelCellElement);

  }

  /**
   * SEt number of patients with match only for data element.
   *
   * @param numberOfPatientsWithMatchOnlyForDataElement number of patients with match only for data
   *                                                    element.
   */
  public void setNumberOfPatientsWithMatchOnlyForDataElement(
      Integer numberOfPatientsWithMatchOnlyForDataElement) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithMatchOnlyForDataElement);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_MATCH_ONLY_FOR_DATA_ELEMENT, excelCellElement);


  }

  /**
   * Set percentage of patients with match only for data element out of patients with data element.
   *
   * @param percentageOfPatientsWithMatchOnlyForDataElementOutOfPatientsWithDataElement Set
   *                                                                                    percentage
   *                                                                                    of patients
   *                                                                                    with match
   *                                                                                    only for
   *                                                                                    data
   *                                                                                    element out
   *                                                                                    of patients
   *                                                                                    with data
   *                                                                                    element.
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
   * Set percentage of patients with match only for data element out of total patients.
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
   * Set number of patients with any mismatch for data element.
   *
   * @param numberOfPatientsWithAnyMismatchForDataElement number of patients with any mismatch for
   *                                                      data element.
   */
  public void setNumberOfPatientsWithAnyMismatchForDataElement(
      Integer numberOfPatientsWithAnyMismatchForDataElement) {

    IntegerExcelCellElement excelCellElement = new IntegerExcelCellElement(
        numberOfPatientsWithAnyMismatchForDataElement);
    addElement(ElementOrder.NUMBER_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT,
        excelCellElement);

  }

  /**
   * Set percentage of patients with any mismatch for data element out of patients with data
   * element.
   *
   * @param percentageOfPatientsWithAnyMismatchForDataElementOutOfPatientsWithDataElement Set
   *                                                                                      percentage
   *                                                                                      of
   *                                                                                      patients
   *                                                                                      with any
   *                                                                                      mismatch
   *                                                                                      for data
   *                                                                                      element
   *                                                                                      out of
   *                                                                                      patients
   *                                                                                      with data
   *                                                                                      element.
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
   * Set percentage of patients with any mismatch for data elemento out of total patients.
   *
   * @param percentageOfPatientsWithAnyMismatchForDataElementOutOfTotalPatients percentage of
   *                                                                            patients
   *                                                                            with any mismatch
   *                                                                            for data elemento
   *                                                                            out of
   *                                                                            total patients.
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
   * Set general rehearsal priorization.
   *
   * @param priorization general rehearsal priorization.
   */
  public void setGeneralRehearsalPriorization(String priorization) {

    StringExcelCellElement cellElement = new StringExcelCellElement(priorization);
    addElement(ElementOrder.GENERAL_REHEARSAL_PRIORIZATION, cellElement);

  }

  /**
   * Set general rehearsal a contained in quality report.
   *
   * @param value general rehearsal a contained in quality report.
   */
  public void setGeneralRehearsalAContainedInQR(boolean value) {
    NaturalLanguageBooleanExcelCellElement excelCellElement =
        new NaturalLanguageBooleanExcelCellElement(value);
    addElement(ElementOrder.GENERAL_REHEARSAL_A_CONTAINED_IN_QR, excelCellElement);

  }

  /**
   * Set general rehearsal b low mismatch.
   *
   * @param value general rehearsal b low mismatch.
   */
  public void setGeneralRehearsalBLowMismatch(boolean value) {

    NaturalLanguageBooleanExcelCellElement excelCellElement =
        new NaturalLanguageBooleanExcelCellElement(value);
    addElement(ElementOrder.GENERAL_REHEARSAL_B_LOW_MISMATCH, excelCellElement);

  }

  /**
   * Set general rehearsal A and B.
   *
   * @param value general rehearsal A and B.
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
    NUMBER_OF_PATIENTS_WITH_ANY_MISMATCH_FOR_DATA_ELEMENT(
        "Number of patients with any mismatch"),
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
