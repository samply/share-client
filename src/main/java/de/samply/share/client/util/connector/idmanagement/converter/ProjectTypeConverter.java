package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.project.directory.client.ProjectDirectoryException;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.model.common.Attribute;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectTypeConverter extends ProjectParticipationConverter {

  public ProjectTypeConverter(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils) {
    super(idManagementConnector, projectDirectoryUtils);
  }

  @Override
  protected Serializable createInSerializableTerm(Attribute attribute)
      throws SerializableConverterException {

    String projectType = attribute.getValue().getValue();
    List<String> projectIdTypes =
        (projectType != null && !projectType.isEmpty()) ? getProjectIdTypesForProjectType(
            projectType) : new ArrayList<>();

    Set<String> patientLocalIds = fetchAllPatientLocalIdsFromIdManagement(projectIdTypes);

    return (patientLocalIds != null && patientLocalIds.size() > 0) ? createInTerm(patientLocalIds)
        : null;

  }

  private List<String> getProjectIdTypesForProjectType(String projectType)
      throws SerializableConverterException {
    try {
      return projectDirectoryUtils.getProjectIdsForProjectType(projectType);
    } catch (ProjectDirectoryException e) {
      throw new SerializableConverterException(e);
    }
  }

}
