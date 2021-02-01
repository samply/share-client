package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.model.common.Attribute;
import java.io.Serializable;
import java.util.Set;

public class ProjectIdConverter extends ProjectParticipationConverter {


  public ProjectIdConverter(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils) {
    super(idManagementConnector, projectDirectoryUtils);
  }

  @Override
  protected Serializable createInSerializableTerm(Attribute attribute)
      throws SerializableConverterException {

    String projectIdType = attribute.getValue().getValue();
    Set<String> patientLocalIds =
        (projectIdType != null) ? fetchAllPatientLocalIdsFromIdManagement(projectIdType) : null;
    return (patientLocalIds != null && patientLocalIds.size() > 0) ? createInTerm(patientLocalIds)
        : null;

  }

}
