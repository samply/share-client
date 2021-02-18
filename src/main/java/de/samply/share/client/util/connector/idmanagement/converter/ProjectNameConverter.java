package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.project.directory.client.ProjectDirectoryException;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.model.common.Attribute;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ProjectNameConverter extends ProjectParticipationConverter {


  public ProjectNameConverter(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils) {
    super(idManagementConnector, projectDirectoryUtils);
  }

  @Override
  protected Serializable createInSerializableTerm(Attribute attribute)
      throws SerializableConverterException {

    String projectName = attribute.getValue().getValue();
    List<String> projectIdTypes =
        (projectName != null && !projectName.isEmpty()) ? getProjectIdTypesFromProjectName(
            projectName) : null;
    Set<String> patientLocalIds = (projectIdTypes != null && !projectIdTypes.isEmpty())
        ? fetchAllPatientLocalIdsFromIdManagement(projectIdTypes) : null;

    return (patientLocalIds != null && !patientLocalIds.isEmpty()) ? createInTerm(patientLocalIds)
        : null;


  }

  private List<String> getProjectIdTypesFromProjectName(String projectName)
      throws SerializableConverterException {

    try {
      return projectDirectoryUtils.getProjectIdForProjectName(projectName);
    } catch (ProjectDirectoryException e) {
      throw new SerializableConverterException(e);
    }

  }
}
