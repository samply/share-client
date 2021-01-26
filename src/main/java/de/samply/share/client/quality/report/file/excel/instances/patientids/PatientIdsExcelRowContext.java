package de.samply.share.client.quality.report.file.excel.instances.patientids;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.excel.instances.basic.BasicExcelColumnMetaInfo;
import de.samply.share.client.quality.report.file.excel.instances.basic.BasicExcelRowElements;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.sorted.AlphabeticallySortedMismatchedQualityResults;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PatientIdsExcelRowContext implements ExcelRowContext {

  protected static final Logger logger = LogManager.getLogger(PatientIdsExcelRowContext.class);
  private final String mdrLinkPrefix;
  private final Integer maxNumberOfPatientIdsToBeShown;
  private final PatientIdsList patientIdsList = new PatientIdsList();
  private final List<BasicExcelColumnMetaInfo> metaInfos = new ArrayList<>();

  /**
   * Todo.
   *
   * @param qualityResults Todo.
   */
  public PatientIdsExcelRowContext(AlphabeticallySortedMismatchedQualityResults qualityResults) {

    this.mdrLinkPrefix = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MDR_LINK_PREFIX);
    String maxNumberOfPatientIdsToBeShownS = ConfigurationUtil.getConfigurationElementValue(
        EnumConfiguration.QUALITY_REPORT_MAX_NUMBER_OF_PATIENT_IDS_TO_BE_SHOWN);
    maxNumberOfPatientIdsToBeShown = convert(maxNumberOfPatientIdsToBeShownS);

    createQualityResultList(qualityResults);

  }

  protected abstract Collection<String> getPatientIds(QualityResult qualityResult);

  public Integer getNumberOfRows() {
    return patientIdsList.getMaxNumberOfPatientsOfAllPatientLists();
  }

  private Integer convert(String number) {

    try {
      return Integer.valueOf(number);
    } catch (Exception e) {
      return null;
    }

  }

  private void createQualityResultList(
      AlphabeticallySortedMismatchedQualityResults qualityResults) {

    int counter = 0;

    int numberOfQualityResults = getNumberOfQualityResults(qualityResults);
    PercentageLogger percentageLogger = new PercentageLogger(logger, numberOfQualityResults,
        "analyzing quality results");

    for (QualityResult qualityResult : qualityResults) {

      percentageLogger.incrementCounter();

      MdrIdDatatype mdrId = qualityResults.getMdrId(counter);
      String value = qualityResults.getValue(counter);

      addMetaInfo(mdrId, value);
      addPatientIdsToList(qualityResult);

      counter++;

    }

  }

  private int getNumberOfQualityResults(QualityResults qualityResults) {

    int counter = 0;

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {
      counter += qualityResults.getValues(mdrId).size();
    }

    return counter;

  }

  private void addPatientIdsToList(QualityResult qualityResult) {

    Collection<String> patientIds = getPatientIds(qualityResult);

    if (maxNumberOfPatientIdsToBeShown != null && maxNumberOfPatientIdsToBeShown > 0) {

      List<String> reducedPatientIds = new ArrayList<>();

      int counter = 0;
      for (String patientId : patientIds) {
        reducedPatientIds.add(patientId);
        counter++;
        if (counter == maxNumberOfPatientIdsToBeShown) {
          break;
        }
      }

      patientIds = reducedPatientIds;

    }

    patientIdsList.addList(patientIds);

  }

  private void addMetaInfo(MdrIdDatatype mdrId, String value) {

    BasicExcelColumnMetaInfo metaInfo = new BasicExcelColumnMetaInfo();
    String title = getTitle(mdrId, value);
    String link = getMdrLink(mdrId);

    metaInfo.setTitle(title);
    metaInfo.setLink(link);

    metaInfos.add(metaInfo);

  }

  private String getTitle(MdrIdDatatype mdrId, String value) {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(mdrId.getNamespace());
    stringBuilder.append(':');
    stringBuilder.append(mdrId.getId());
    stringBuilder.append(':');
    stringBuilder.append(mdrId.getVersion());
    stringBuilder.append(':');
    stringBuilder.append(value);

    return stringBuilder.toString();

  }

  private String getMdrLink(MdrIdDatatype mdrId) {
    return mdrLinkPrefix + mdrId;
  }


  @Override
  public ExcelRowElements createEmptyExcelRowElements() {
    return new BasicExcelRowElements(metaInfos);
  }

  @Override
  public Iterator<ExcelRowElements> iterator() {
    return new PatientIdsExcelContextIterator();
  }

  private class PatientIdsExcelContextIterator implements Iterator<ExcelRowElements> {

    private final Iterator<List<String>> iterator;

    public PatientIdsExcelContextIterator() {
      iterator = patientIdsList.iterator();
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public ExcelRowElements next() {

      List<String> next = iterator.next();

      return createExcelRowElements(next);

    }

    private ExcelRowElements createExcelRowElements(List<String> myList) {

      BasicExcelRowElements excelRowElements = new BasicExcelRowElements(metaInfos);

      for (String element : myList) {
        excelRowElements.addElement(element);
      }

      return excelRowElements;

    }

    @Override
    public void remove() {
      iterator.remove();
    }
  }


}
