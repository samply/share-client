package de.samply.share.client.util.connector.idmanagement.utils;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import java.util.List;
import javax.xml.bind.JAXBElement;

public class IdManagementUtils {

  public static final String CENTRAL_MDS_DB_PUBKEY_FILENAME = "mds-db-key-public.der";
  private static final char LOCAL = 'L';
  private static final char GLOBAL = 'G';
  private static final String BK = "BK";
  private static final String MDS = "MDS";
  private static final String MDS_RANDOM = "MDS_RANDOM";
  private static String defaultPatientLocalIdType;
  private static String defaultPatientGlobalIdType;
  private static String defaultLocalExportIdType;
  private static final String defaultGlobalExportIdType = "MDS_*_G-ID";
  private static String defaultRandomLocalExportIdType;
  private static final MdrIdDatatype localPatientIdMdrId =
      new MdrIdDatatype("urn:dktk:dataelement:91");
  private static final MdrIdDatatype globalPatientIdMdrId =
      new MdrIdDatatype("urn:dktk:dataelement:54");

  private static String idManagementInstanceId;
  private static final ObjectFactory objectFactory = new ObjectFactory();


  public static boolean isLocalIdType(String idType) {
    return (idType != null) && idType.contains("_L-");
  }

  public static boolean isGlobalIdType(String idType) {
    return (idType != null) && idType.contains("_G-");
  }

  /**
   * Todo David.
   * @return Todo David
   */
  public static String getDefaultPatientLocalIdType() {

    if (defaultPatientLocalIdType == null) {
      defaultPatientLocalIdType = getIdType(BK, true);
    }

    return defaultPatientLocalIdType;

  }

  private static String getIdType(String prefix, boolean isLocal) {

    String idManagerInstanceId = getIdManagementInstanceId();

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(prefix);
    stringBuilder.append('_');
    stringBuilder.append(idManagerInstanceId);
    stringBuilder.append('_');
    stringBuilder.append(isLocal ? LOCAL : GLOBAL);
    stringBuilder.append("-ID");

    return stringBuilder.toString();

  }

  /**
   * Todo David.
   * @return Todo David
   */
  public static String getDefaultPatientGlobalIdType() {

    if (defaultPatientGlobalIdType == null) {
      defaultPatientGlobalIdType = getIdType(BK, false);
    }

    return defaultPatientGlobalIdType;

  }

  /**
   * Todo David.
   * @return Todo David
   */
  public static String getDefaultLocalExportIdType() {

    if (defaultLocalExportIdType == null) {
      defaultLocalExportIdType = getIdType(MDS, true);
    }

    return defaultLocalExportIdType;

  }

  /**
   * Todo David.
   * @return Todo David
   */
  public static String getDefaultRandomLocalExportIdType() {

    if (defaultRandomLocalExportIdType == null) {
      defaultRandomLocalExportIdType = getIdType(MDS_RANDOM, true);
    }

    return defaultRandomLocalExportIdType;

  }

  public static String getDefaultGlobalExportIdType() {
    return defaultGlobalExportIdType;
  }

  public static String getLocalIdManagementProjectId(String projectId) {
    return getIdType(projectId, true);
  }

  public static String getGlobalIdManagementProjectId(String projectId) {
    return getIdType(projectId, false);
  }

  private static String getIdManagementInstanceId() {

    if (idManagementInstanceId == null) {
      idManagementInstanceId = ConfigurationUtil
          .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID);
    }

    return idManagementInstanceId;

  }

  public static MdrIdDatatype getLocalPatientIdMdrId() {
    return localPatientIdMdrId;
  }

  /**
   * Todo David.
   * @param patient Todo David
   * @return Todo David
   */
  public static IdObject getLocalPatientId(Patient patient) {

    String idString = patient.getId();
    String idType = patient.getIdType();
    if (idType == null) {
      idType = getDefaultPatientLocalIdType();
    }

    return createIdObject(idType, idString);

  }

  private static IdObject createIdObject(String idType, String idString) {
    return new IdObject(idType, idString);
  }

  /**
   * Todo David.
   * @param patient Todo David
   * @param patientId Todo David
   */
  public static void setLocalPatientId(Patient patient, IdObject patientId) {

    if (patient != null && patientId != null) {

      String idString = patientId.getIdString();
      String idType = patientId.getIdType();

      patient.setIdType(idType);
      patient.setId(idString);

    }

  }

  /**
   * Todo David.
   * @param patient Todo David
   * @return Todo David
   */
  public static IdObject getGlobalPatientId(Patient patient) {

    IdObject idObject = null;

    Attribute globalPatientIdAttribute = getGlobalPatientIdAttribute(patient);

    if (globalPatientIdAttribute != null) {

      String idString = globalPatientIdAttribute.getValue().getValue();
      String idType = getDefaultPatientGlobalIdType();

      idObject = createIdObject(idType, idString);

    }

    return idObject;

  }

  private static Attribute getGlobalPatientIdAttribute(Patient patient) {

    Attribute globalPatientIdAttribute = null;

    for (Attribute attribute : patient.getAttribute()) {

      MdrIdDatatype attributeMdrKey = new MdrIdDatatype(attribute.getMdrKey());

      if (attributeMdrKey.equalsIgnoreVersion(globalPatientIdMdrId) && attribute.getValue() != null
          && !SamplyShareUtils.isNullOrEmpty(attribute.getValue().getValue())) {

        globalPatientIdAttribute = attribute;
        break;

      }

    }

    return globalPatientIdAttribute;

  }

  /**
   * Todo David.
   * @param patient Todo David
   * @param patientId Todo David
   */
  public static void setGlobalPatientId(Patient patient, IdObject patientId) {

    Attribute globalPatientIdAttribute = getGlobalPatientIdAttribute(patient);

    if (globalPatientIdAttribute != null) {

      String globalPatientId = patientId.getIdString();
      globalPatientIdAttribute.getValue().setValue(globalPatientId);

    } else {

      Attribute attribute = objectFactory.createAttribute();

      String mdrKey = globalPatientIdMdrId.getLatestMdr();
      JAXBElement<String> value = objectFactory.createValue(patientId.getIdString());

      attribute.setMdrKey(mdrKey);
      attribute.setValue(value);

      List<Attribute> patientAttributes = patient.getAttribute();
      patientAttributes.add(attribute);

    }


  }

  /**
   * Todo David.
   * @param idType Todo David
   * @return Todo David
   */
  public static String getProjectDirectoryId(String idType) {

    String projectDirectoryId = null;

    if (idType != null) {

      int index = idType.indexOf("_");
      if (index > 0) {

        projectDirectoryId = idType.substring(0, index);

      }

    }

    return projectDirectoryId;

  }

}
