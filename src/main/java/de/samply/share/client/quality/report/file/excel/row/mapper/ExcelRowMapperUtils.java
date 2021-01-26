package de.samply.share.client.quality.report.file.excel.row.mapper;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Definition;
import de.samply.common.mdrclient.domain.Record;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ExcelRowMapperUtils {

  private final String languageCode;
  private final String mdrLinkPrefix; // = "https://mdr.ccp-it.dktk.dkfz.de/detail.xhtml?urn=";
  private final ModelSearcher modelSearcher;
  private final MdrClient mdrClient;
  private final Map<MdrIdDatatype, String> mdrDatenElements = new HashMap<>();

  /**
   * Todo.
   *
   * @param model     Todo.
   * @param mdrClient Todo.
   */
  public ExcelRowMapperUtils(Model model, MdrClient mdrClient) {

    this.mdrLinkPrefix = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_MDR_LINK_PREFIX);

    this.modelSearcher = new ModelSearcher(model);
    this.mdrClient = mdrClient;

    this.languageCode = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_LANGUAGE_CODE);

  }

  public String getMdrLink(MdrIdDatatype mdrId) {
    return mdrLinkPrefix + mdrId;
  }

  /**
   * Todo.
   *
   * @param mdrId Todo.
   * @return Todo.
   * @throws ExcelRowMapperException Todo.
   */
  public String getMdrDatenElement(MdrIdDatatype mdrId) throws ExcelRowMapperException {

    String mdrName = mdrDatenElements.get(mdrId);
    if (mdrName == null) {
      mdrName = createMdrDatenElement(mdrId);
      mdrDatenElements.put(mdrId, mdrName);
    }
    return mdrName;
  }

  private String getMdrDatenElement(Record record) {
    return (record != null) ? record.getDesignation() : null;
  }

  private String createMdrDatenElement(MdrIdDatatype mdrId) throws ExcelRowMapperException {

    try {

      return createMdrDatenElementWithoutExceptions(mdrId);

    } catch (MdrConnectionException | ExecutionException | MdrInvalidResponseException e) {
      throw new ExcelRowMapperException(e);
    }

  }

  private String createMdrDatenElementWithoutExceptions(MdrIdDatatype mdrId)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

    Definition definition = mdrClient.getDataElementDefinition(mdrId.toString(), languageCode);
    ArrayList<Record> designations = definition.getDesignations();
    if (designations != null && designations.size() > 0) {
      Record record = designations.get(0);
      return getMdrDatenElement(record);
    }

    return null;

  }

  /**
   * Todo.
   *
   * @param mdrId Todo.
   * @return Todo.
   */
  public String getMdrType(MdrIdDatatype mdrId) {

    Validations validations = modelSearcher.getValidations(mdrId);
    return (validations != null) ? validations.getDatatype() : null;

  }

}
