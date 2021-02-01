package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.common.utils.MdrIdDatatype;

public class IdManagementLdmSerialzableConvertersFactory {

  private final IdManagementConnector idManagementConnector;
  private final ProjectDirectoryUtils projectDirectoryUtils;

  public IdManagementLdmSerialzableConvertersFactory(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils) {
    this.idManagementConnector = idManagementConnector;
    this.projectDirectoryUtils = projectDirectoryUtils;
  }

  /**
   * Todo David.
   * @return Todo David.
   */
  public LdmSerializableConverters createLdmSerializableConverters() {

    LdmSerializableConverters ldmSerializableConverters = new LdmSerializableConverters();

    MdrIdDatatype projectIdMdrId = new MdrIdDatatype(ProjectDirectoryUtils.PROJECT_ID_MDR_ID);
    MdrIdDatatype projectParticipationMdrId = new MdrIdDatatype(
        ProjectDirectoryUtils.PROJECT_PARTICIPATION_MDR_ID);
    MdrIdDatatype localPspIdMdrId = new MdrIdDatatype(ProjectDirectoryUtils.LOCAL_PSP_ID_MDR_ID);
    MdrIdDatatype globalPspIdMdrId = new MdrIdDatatype(ProjectDirectoryUtils.GLOBAL_PSP_ID_MDR_ID);
    MdrIdDatatype projectNameMdrId = new MdrIdDatatype(ProjectDirectoryUtils.PROJECT_NAME);
    MdrIdDatatype projectTypeMdrId = new MdrIdDatatype(ProjectDirectoryUtils.PROJECT_TYPE);
    MdrIdDatatype dataCategoryMdrId = new MdrIdDatatype(ProjectDirectoryUtils.DATA_CATEGORY);

    SerializableConverter projectIdConverter = createProjectIdConverter();
    SerializableConverter projectParticipationConverter = createProjectParticipationConverter();
    SerializableConverter localPspIdConverter = createPspIdConverter();
    SerializableConverter globalPspIdConverter = createPspIdConverter();
    SerializableConverter projectNameConverter = createProjectNameConverter();
    SerializableConverter projectTypeConverter = createProjectTypeConverter();
    SerializableConverter dataCategoryConverter = createDataCategoryConverter();

    ldmSerializableConverters.addSerializableConverter(projectIdMdrId, projectIdConverter);
    ldmSerializableConverters
        .addSerializableConverter(projectParticipationMdrId, projectParticipationConverter);
    ldmSerializableConverters.addSerializableConverter(localPspIdMdrId, localPspIdConverter);
    ldmSerializableConverters.addSerializableConverter(globalPspIdMdrId, globalPspIdConverter);
    ldmSerializableConverters.addSerializableConverter(projectNameMdrId, projectNameConverter);
    ldmSerializableConverters.addSerializableConverter(projectTypeMdrId, projectTypeConverter);
    ldmSerializableConverters.addSerializableConverter(dataCategoryMdrId, dataCategoryConverter);

    return ldmSerializableConverters;

  }


  private SerializableConverter createProjectIdConverter() {
    return new ProjectIdConverter(idManagementConnector, projectDirectoryUtils);
  }

  private SerializableConverter createPspIdConverter() {
    return new PspIdConverter(idManagementConnector, projectDirectoryUtils);
  }

  private SerializableConverter createProjectParticipationConverter() {
    return new ProjectParticipationConverter(idManagementConnector, projectDirectoryUtils);
  }

  private SerializableConverter createProjectNameConverter() {
    return new ProjectNameConverter(idManagementConnector, projectDirectoryUtils);
  }

  private SerializableConverter createProjectTypeConverter() {
    return new ProjectTypeConverter(idManagementConnector, projectDirectoryUtils);
  }

  private SerializableConverter createDataCategoryConverter() {
    return new DataCategoryConverter(idManagementConnector, projectDirectoryUtils);
  }

}
