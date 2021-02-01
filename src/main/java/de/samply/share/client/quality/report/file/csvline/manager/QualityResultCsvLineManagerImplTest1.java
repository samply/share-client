package de.samply.share.client.quality.report.file.csvline.manager;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Definition;
import de.samply.common.mdrclient.domain.Record;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.file.csvline.PatientDataCsvLinePatternTest1;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class QualityResultCsvLineManagerImplTest1 implements QualityResultCsvLineManager {

  private static final String MDR_LINK_PREFIX = "https://mdr.ccp-it.dktk.dkfz.de/detail.xhtml?urn=";
  private static final String languageCode = "de";
  private static final String EXCEL_HYPERLINK_PREFIX = "=HYPERLINK(\"";
  private static final String EXCEL_HYPERLINK_SUFFIX = "\")";

  private final ModelSearcher modelSearcher;
  private final MdrClient mdrClient;
  private final Map<MdrIdDatatype, String> mdrNames = new HashMap<>();

  public QualityResultCsvLineManagerImplTest1(Model model, MdrClient mdrClient) {
    this.modelSearcher = new ModelSearcher(model);
    this.mdrClient = mdrClient;
  }

  @Override
  public String createLine(MdrIdDatatype mdrId, String value, QualityResult qualityResult)
      throws QualityResultCsvLineManagerException {

    PatientDataCsvLinePatternTest1 csvLine = new PatientDataCsvLinePatternTest1();

    csvLine.setMdrId(mdrId);
    csvLine.setAttributeValue(value);
    csvLine.setValid(qualityResult.isValid());
    csvLine.setNumberOfPatients(qualityResult.getNumberOfPatients());
    csvLine.setMdrLink(getExcelHyperlink(getMdrLink(mdrId)));
    csvLine.setMdrName(getMdrName(mdrId));
    csvLine.setMdrType(getMdrType(mdrId));

    return csvLine.createLine();

  }

  private String getMdrName(MdrIdDatatype mdrId) throws QualityResultCsvLineManagerException {

    String mdrName = mdrNames.get(mdrId);
    if (mdrName == null) {
      mdrName = createMdrName(mdrId);
      mdrNames.put(mdrId, mdrName);
    }
    return mdrName;
  }

  private String getMdrName(Record record) {
    return (record != null) ? record.getDesignation() : null;
  }

  private String createMdrName(MdrIdDatatype mdrId) throws QualityResultCsvLineManagerException {

    try {

      return createMdrNameWithoutExceptions(mdrId);

    } catch (MdrConnectionException | ExecutionException | MdrInvalidResponseException e) {
      throw new QualityResultCsvLineManagerException(e);
    }

  }

  private String createMdrNameWithoutExceptions(MdrIdDatatype mdrId)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    Definition definition = mdrClient.getDataElementDefinition(mdrId.toString(), languageCode);
    ArrayList<Record> designations = definition.getDesignations();
    if (designations != null && designations.size() > 0) {
      Record record = designations.get(0);
      return getMdrName(record);
    }

    return null;

  }

  private String getMdrType(MdrIdDatatype mdrId) {

    Validations validations = modelSearcher.getValidations(mdrId);
    return (validations != null) ? validations.getDatatype() : null;

  }

  private String getExcelHyperlink(String link) {
    return EXCEL_HYPERLINK_PREFIX + link + EXCEL_HYPERLINK_SUFFIX;
  }

  private String getMdrLink(MdrIdDatatype mdrId) {
    return MDR_LINK_PREFIX + mdrId;
  }

  @Override
  public QualityResults parseLineAndAddToQualityResults(String line,
      QualityResults qualityResults) {

    PatientDataCsvLinePatternTest1 csvLine = new PatientDataCsvLinePatternTest1();

    csvLine.parseValuesOfLine(line);

    MdrIdDatatype mdrId = csvLine.getMdrId();
    String attributeValue = csvLine.getAttributeValue();
    Integer numberOfPatients = csvLine.getNumberOfPatients();
    Boolean valid = csvLine.isValid();

    if (mdrId != null && attributeValue != null && valid != null && numberOfPatients != null) {

      QualityResult qualityResult = new QualityResult();

      qualityResult.setValid(valid);
      qualityResult.setNumberOfPatients(numberOfPatients);

      qualityResults.put(mdrId, attributeValue, qualityResult);

    }

    return qualityResults;

  }

}
