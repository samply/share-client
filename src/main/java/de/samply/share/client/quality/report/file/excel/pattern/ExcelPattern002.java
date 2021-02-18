package de.samply.share.client.quality.report.file.excel.pattern;

import de.samply.common.mdrclient.MdrClient;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.cell.reference.FirstRowCellReferenceFactoryForOneSheet;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientDktkIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.patientids.PatientLocalIdsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats.DataElementStatsExcelRowContextFactory;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContextFactory002;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactory;
import de.samply.share.client.quality.report.file.excel.row.factory.ExcelRowFactoryImpl;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapper002;
import de.samply.share.client.quality.report.file.excel.row.mapper.ExcelRowMapperUtils;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.ExcelSheetFactoryImpl;
import de.samply.share.client.quality.report.file.excel.sheet.ExplanatoryExcelSheetFactory;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.ExcelSheetFreezeFirstRowFactory;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.ExcelSheetWithAutoFilterFactory;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.ExcelSheetWithAutoSizeColumnFactory;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.HighlightMismatchInRedExcelSheetFactory002;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.HighlightNotFoundInBlueExcelSheetFactory002;
import de.samply.share.client.quality.report.file.excel.sheet.wrapper.HighlightNotMappedInOrangeExcelSheetFactory002;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactory;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryImpl002;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryParameters002;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;

public class ExcelPattern002 implements ExcelPattern {

  private final Model model;
  private final MdrClient mdrClient;
  private final CentraxxMapper centraXxMapper;
  private final DktkIdMdrIdConverter dktkIdManager;
  private final MdrMappedElements mdrMappedElements;
  private final ExcelRowMapperUtils excelRowMapperUtils;


  /**
   * Creates Excel Pattern. It contains relevant information for building a quality report.
   *
   * @param model             mdr data element ids and validations.
   * @param mdrClient         mdr client.
   * @param centraXxMapper    map mdr data element - centraxx data-element.
   * @param dktkIdManager     map mdr data element - mdr slot DKTK-ID.
   * @param mdrMappedElements additional information about mdr data elements.
   */
  public ExcelPattern002(Model model, MdrClient mdrClient, CentraxxMapper centraXxMapper,
      DktkIdMdrIdConverter dktkIdManager, MdrMappedElements mdrMappedElements) {

    this.model = model;
    this.mdrClient = mdrClient;
    this.excelRowMapperUtils = new ExcelRowMapperUtils(model, mdrClient);

    this.centraXxMapper = centraXxMapper;
    this.dktkIdManager = dktkIdManager;
    this.mdrMappedElements = mdrMappedElements;


  }

  @Override
  public ExcelWorkbookFactory createExcelWorkbookFactory() {

    ExcelWorkbookFactoryParameters002 excelWorkbookFactoryParameters =
        createExcelWorkbookFactoryParameters();
    return new ExcelWorkbookFactoryImpl002(excelWorkbookFactoryParameters);

  }

  private ExcelWorkbookFactoryParameters002 createExcelWorkbookFactoryParameters() {

    ExcelWorkbookFactoryParameters002 excelWorkbookFactoryParameters =
        new ExcelWorkbookFactoryParameters002();

    ExcelRowFactory excelRowFactory = new ExcelRowFactoryImpl();
    ExcelSheetFactory excelSheetFactory = createExcelSheetFactory(excelRowFactory);

    ExcelRowContextFactory002 excelRowContextFactory = createExcelRowContextFactory();
    PatientDktkIdsExcelRowContextFactory patientDktkIdsExcelRowContextFactory =
        new PatientDktkIdsExcelRowContextFactory();
    PatientLocalIdsExcelRowContextFactory patientLocalIdsExcelRowContextFactory =
        new PatientLocalIdsExcelRowContextFactory();
    ExplanatoryExcelSheetFactory explanatoryExcelSheetFactory =
        createExplanatoryExcelSheetFactory();
    DataElementStatsExcelRowContextFactory dataElementStatsExcelRowContextFactory =
        new DataElementStatsExcelRowContextFactory(
            excelRowMapperUtils, dktkIdManager, centraXxMapper);

    excelWorkbookFactoryParameters.setExcelSheetFactory(excelSheetFactory);
    excelWorkbookFactoryParameters.setExplanatoryExcelSheetFactory(explanatoryExcelSheetFactory);
    excelWorkbookFactoryParameters.setModelSearcher(new ModelSearcher(model));
    excelWorkbookFactoryParameters.setDktkIdManager(dktkIdManager);
    excelWorkbookFactoryParameters
        .setPatientLocalIdsExcelRowContextFactory(patientLocalIdsExcelRowContextFactory);
    excelWorkbookFactoryParameters
        .setPatientDktkIdsExcelRowContextFactory(patientDktkIdsExcelRowContextFactory);
    excelWorkbookFactoryParameters.setMdrMappedElements(mdrMappedElements);
    excelWorkbookFactoryParameters.setExcelRowContextFactory(excelRowContextFactory);
    excelWorkbookFactoryParameters
        .setDataElementStats_excelRowContextFactory(dataElementStatsExcelRowContextFactory);

    return excelWorkbookFactoryParameters;

  }

  private ExcelSheetFactory createExcelSheetFactory(ExcelRowFactory excelRowFactory) {

    ExcelSheetFactory excelSheetFactory = new ExcelSheetFactoryImpl(excelRowFactory);

    excelSheetFactory = new ExcelSheetWithAutoFilterFactory(excelSheetFactory);
    excelSheetFactory = new HighlightMismatchInRedExcelSheetFactory002(excelSheetFactory);
    excelSheetFactory = new HighlightNotMappedInOrangeExcelSheetFactory002(excelSheetFactory);
    excelSheetFactory = new HighlightNotFoundInBlueExcelSheetFactory002(excelSheetFactory);
    excelSheetFactory = new ExcelSheetWithAutoSizeColumnFactory(excelSheetFactory);
    excelSheetFactory = new ExcelSheetFreezeFirstRowFactory(excelSheetFactory);

    return excelSheetFactory;

  }

  private ExcelRowContextFactory002 createExcelRowContextFactory() {

    ExcelRowMapper002 excelRowMapper = createExcelRowMapper();
    return new ExcelRowContextFactory002(excelRowMapper);

  }


  private ExcelRowMapper002 createExcelRowMapper() {

    FirstRowCellReferenceFactoryForOneSheet firstRowCellReferenceFactoryForOneSheet =
        new FirstRowCellReferenceFactoryForOneSheet(
            ExcelWorkbookFactoryImpl002.PATIENT_LOCAL_IDS_SHEET_TITLE);
    return new ExcelRowMapper002(centraXxMapper, dktkIdManager,
        firstRowCellReferenceFactoryForOneSheet, mdrMappedElements, excelRowMapperUtils);

  }

  private ExplanatoryExcelSheetFactory createExplanatoryExcelSheetFactory() {

    return new ExplanatoryExcelSheetFactory();

  }

}
