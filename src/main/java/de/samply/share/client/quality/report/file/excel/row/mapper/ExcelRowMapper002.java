package de.samply.share.client.quality.report.file.excel.row.mapper;

import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.file.excel.cell.reference.CellReference;
import de.samply.share.client.quality.report.file.excel.cell.reference.FirstRowCellReferenceFactoryForOneSheet;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowParameters002;
import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements002;
import de.samply.share.client.quality.report.file.excel.row.elements.FormattedExcelRowElements002;
import de.samply.share.common.utils.MdrIdDatatype;

public class ExcelRowMapper002 {


  private final ExcelRowMapperUtils excelRowMapperUtils;
  private final CentraxxMapper centraXxMapper;
  private final DktkIdMdrIdConverter dktkIdManager;
  private final FirstRowCellReferenceFactoryForOneSheet cellReferenceFactory;
  private final MdrMappedElements mdrMappedElements;

  /**
   * Constructs Excel row mapper.
   *
   * @param centraXxMapper       map mdr data element id - centraxx information.
   * @param dktkIdManager        map mdr data element id - mdr slot DKTK-ID.
   * @param cellReferenceFactory factory that creates cell references.
   * @param mdrMappedElements    map mdr data element id - centraxx data elements.
   * @param excelRowMapperUtils  utils for excel row mapper.
   */
  public ExcelRowMapper002(CentraxxMapper centraXxMapper, DktkIdMdrIdConverter dktkIdManager,
      FirstRowCellReferenceFactoryForOneSheet cellReferenceFactory,
      MdrMappedElements mdrMappedElements, ExcelRowMapperUtils excelRowMapperUtils) {

    this.excelRowMapperUtils = excelRowMapperUtils;
    this.centraXxMapper = centraXxMapper;
    this.dktkIdManager = dktkIdManager;
    this.cellReferenceFactory = cellReferenceFactory;
    this.mdrMappedElements = mdrMappedElements;

  }

  /**
   * Constructs excel row element.
   *
   * @param excelRowParameters Excel row parameters.
   * @return Excel row elements.
   * @throws ExcelRowMapperException Encapsulates exceptions of the class.
   */
  public ExcelRowElements002 createExcelRowElements(ExcelRowParameters002 excelRowParameters)
      throws ExcelRowMapperException {
    CellReference cellReference = createCellReference(excelRowParameters);
    int numberOfPatients = excelRowParameters.getQualityResult().getNumberOfPatients();
    MdrIdDatatype mdrId = excelRowParameters.getMdrId();
    ExcelRowElements002 rowElements = new FormattedExcelRowElements002();
    boolean isValid = excelRowParameters.getQualityResult().isValid();
    if (mdrMappedElements.isMapped(mdrId)) {
      rowElements.setValid(isValid, numberOfPatients, cellReference);
    } else {
      rowElements.setNotMapped();
    }

    String mdrAttributeValue = isValid ? excelRowParameters.getValue() : null;
    rowElements.setMdrAttributeValue(mdrAttributeValue);
    String mdrDatenElement = excelRowMapperUtils.getMdrDatenElement(mdrId);
    rowElements.setMdrDatenElement(mdrDatenElement);
    String mdrType = excelRowMapperUtils.getMdrType(mdrId);
    rowElements.setMdrType(mdrType);
    String mdrLink = excelRowMapperUtils.getMdrLink(mdrId);
    rowElements.setMdrLink(mdrLink, mdrId);
    String cxxDatenElement = centraXxMapper.getCentraXxAttribute(mdrId);
    rowElements.setCxxDatenElement(cxxDatenElement);
    String cxxAttributeValue = isValid ? centraXxMapper.getCentraXxValue(mdrId, mdrAttributeValue)
        : excelRowParameters.getValue();
    rowElements.setCxxAttributeValue(cxxAttributeValue);
    String dktkId = dktkIdManager.getDktkId(mdrId);
    rowElements.setDktkId(dktkId);
    Double percentageOutOfPatientWithDataElement = excelRowParameters
        .getPercentageOutOfPatientWithDataElement();
    rowElements.setPercentageOutOfPatientsWithDataElement(percentageOutOfPatientWithDataElement);
    Double percentageOutOfTotalPatients = excelRowParameters.getPercentageOutOfTotalPatients();
    rowElements.setPercentageOutOfTotalPatients(percentageOutOfTotalPatients);
    rowElements.setNumberOfPatients(numberOfPatients);

    return rowElements;
  }

  private CellReference createCellReference(ExcelRowParameters002 excelRowParameters) {

    Integer mismatchOrdinal = excelRowParameters.getMismatchOrdinal();
    boolean isValid = excelRowParameters.getQualityResult().isValid();

    return (!isValid && mismatchOrdinal != null) ? cellReferenceFactory
        .createCellReference(mismatchOrdinal) : null;

  }


}
