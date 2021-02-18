package de.samply.share.client.util.connector.idmanagement.query;

import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.converter.IdManagementLdmSerialzableConvertersFactory;
import de.samply.share.client.util.connector.idmanagement.converter.LdmSerializableConverters;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;

public class LdmQueryConverterFactoryImpl implements LdmQueryConverterFactory {

  private IdManagementLdmSerialzableConvertersFactory idManagementLdmSerialzableConvertersFactory;


  public LdmQueryConverterFactoryImpl(
      LdmQueryConverterFactoryParameters ldmQueryConverterFactoryParameters) {
    initalize(ldmQueryConverterFactoryParameters);
  }

  private void initalize(LdmQueryConverterFactoryParameters ldmQueryConverterFactoryParameters) {

    IdManagementConnector idManagementConnector = ldmQueryConverterFactoryParameters
        .getIdManagementConnector();
    ProjectDirectoryUtils projectDirectoryUtils = ldmQueryConverterFactoryParameters
        .getProjectDirectoryUtils();

    idManagementLdmSerialzableConvertersFactory = new IdManagementLdmSerialzableConvertersFactory(
        idManagementConnector, projectDirectoryUtils);

  }

  @Override
  public LdmQueryConverter createLdmQueryConverter() {

    LdmQueryConverter ldmQueryConverter = new LdmQueryConverter();

    LdmSerializableConverters idManagementLdmSerializableConverters =
        idManagementLdmSerialzableConvertersFactory.createLdmSerializableConverters();
    ldmQueryConverter
        .addLdmSerializableConverters(LdmId.ID_MANAGEMENT, idManagementLdmSerializableConverters);

    return ldmQueryConverter;

  }

}
