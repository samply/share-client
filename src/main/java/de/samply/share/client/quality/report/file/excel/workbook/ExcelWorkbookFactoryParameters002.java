package de.samply.share.client.quality.report.file.excel.workbook;

import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientDktkIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientLocalIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats.DataElementStatsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextFactory002;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExplanatoryExcelSheetFactory;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;

public class ExcelWorkbookFactoryParameters002 {

  private ExcelSheetFactory excelSheetFactory;
  private ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory;
  private ModelSearcher modelSearcher;
  private DktkIdMdrIdConverter dktkIdManager;
  private PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory;
  private PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory;
  private MdrMappedElements mdrMappedElements;
  private ExcelRowContextFactory002 excelRowContextFactory;
  private DataElementStatsExcelRowContextFactory dataElementStatsExcelRowContextFactory;


  public ExcelSheetFactory getExcelSheetFactory() {
    return excelSheetFactory;
  }

  public void setExcelSheetFactory(ExcelSheetFactory excelSheetFactory) {
    this.excelSheetFactory = excelSheetFactory;
  }

  public ExplanatoryExcelSheetFactory getExplanatoryExcelSheetFactory() {
    return explanatoryExcelSheetFactory;
  }

  public void setExplanatoryExcelSheetFactory(
      ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory) {
    this.explanatoryExcelSheetFactory = explanatoryExcelSheetFactory;
  }

  public ModelSearcher getModelSearcher() {
    return modelSearcher;
  }

  public void setModelSearcher(ModelSearcher modelSearcher) {
    this.modelSearcher = modelSearcher;
  }

  public DktkIdMdrIdConverter getDktkIdManager() {
    return dktkIdManager;
  }

  public void setDktkIdManager(DktkIdMdrIdConverter dktkIdManager) {
    this.dktkIdManager = dktkIdManager;
  }

  public PatientLocalIdsExcelRowContextFactory getPatientLocalIdsExcelRowContextFactory() {
    return patientLocalIdsExcelRowContextFactory;
  }

  public void setPatientLocalIdsExcelRowContextFactory(
      PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory) {
    this.patientLocalIdsExcelRowContextFactory = patientLocalIdsExcelRowContextFactory;
  }

  public PatientDktkIdsExcelRowContextFactory getPatientDktkIdsExcelRowContextFactory() {
    return patientDktkIdsExcelRowContextFactory;
  }

  public void setPatientDktkIdsExcelRowContextFactory(
      PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory) {
    this.patientDktkIdsExcelRowContextFactory = patientDktkIdsExcelRowContextFactory;
  }

  public ExcelRowContextFactory002 getExcelRowContextFactory() {
    return excelRowContextFactory;
  }

  public void setExcelRowContextFactory(ExcelRowContextFactory002 excelRowContextFactory) {
    this.excelRowContextFactory = excelRowContextFactory;
  }

  public DataElementStatsExcelRowContextFactory getDataElementStats_excelRowContextFactory() {
    return dataElementStatsExcelRowContextFactory;
  }

  public void setDataElementStats_excelRowContextFactory(
      DataElementStatsExcelRowContextFactory dataElementStatsExcelRowContextFactory) {
    this.dataElementStatsExcelRowContextFactory = dataElementStatsExcelRowContextFactory;
  }

  public MdrMappedElements getMdrMappedElements() {
    return mdrMappedElements;
  }

  public void setMdrMappedElements(MdrMappedElements mdrMappedElements) {
    this.mdrMappedElements = mdrMappedElements;
  }

}
