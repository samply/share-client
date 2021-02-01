package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.project.directory.client.ProjectDirectoryException;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnectorException;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.client.util.connector.idmanagement.utils.TermFactory;
import de.samply.share.model.common.Attribute;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectParticipationConverter extends PatientInConverter {


  protected IdManagementConnector idManagementConnector;
  protected ProjectDirectoryUtils projectDirectoryUtils;


  public ProjectParticipationConverter(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils) {
    this.idManagementConnector = idManagementConnector;
    this.projectDirectoryUtils = projectDirectoryUtils;
  }

  @Override
  public Serializable convert(Serializable serializable) throws SerializableConverterException {

    Attribute attribute = TermFactory.getAttribute(serializable);

    return createInSerializableTerm(attribute);
  }


  private Set<String> fetchPatientLocalIds() throws SerializableConverterException {
    return fetchAllPatientLocalIdsFromIdManagement(getProjectIds());
  }

  private List<String> getProjectIds() throws SerializableConverterException {

    try {
      return projectDirectoryUtils.getAllProjectIds();
    } catch (ProjectDirectoryException e) {
      throw new SerializableConverterException(e);
    }

  }

  protected Set<String> fetchAllPatientLocalIdsFromIdManagement(String projectIdType)
      throws SerializableConverterException {

    List<String> projectIdTypes = new ArrayList<>();
    if (projectIdType != null) {
      projectIdTypes.add(projectIdType);
    }

    return fetchAllPatientLocalIdsFromIdManagement(projectIdTypes);

  }

  protected Set<String> fetchAllPatientLocalIdsFromIdManagement(List<String> projectIdTypes)
      throws SerializableConverterException {

    try {

      return fetchAllPatientLocalIdsFromIdManagementWithoutManagementException(projectIdTypes);

    } catch (IdManagementConnectorException e) {
      throw new SerializableConverterException(e);
    }
  }

  private Set<String> fetchAllPatientLocalIdsFromIdManagementWithoutManagementException(
      List<String> projectIdTypes) throws IdManagementConnectorException {

    Set<String> patientLocalIds = new HashSet<>();

    List<IdObject> patientLocalIdObjects = new ArrayList<>();
    for (String projectIdType : projectIdTypes) {

      List<IdObject> tempPatientLocalIdObjects = idManagementConnector
          .getAllLocalIds(projectIdType);
      if (tempPatientLocalIdObjects != null && !tempPatientLocalIdObjects.isEmpty()) {
        patientLocalIdObjects.addAll(tempPatientLocalIdObjects);
      }

    }

    for (IdObject idObject : patientLocalIdObjects) {

      String localPatientId = idObject.getIdString();
      patientLocalIds.add(localPatientId);

    }

    return patientLocalIds;

  }

  protected Serializable createInSerializableTerm(Attribute attribute)
      throws SerializableConverterException {

    Serializable serializable = null;

    if (participatesInProject(attribute)) {

      Set<String> patientLocalIds = fetchPatientLocalIds();
      serializable =
          (patientLocalIds != null && patientLocalIds.size() > 0) ? createInTerm(patientLocalIds)
              : null;

    }
    //TODO : if "ProjektTeilnhame" is false, then, "NotIn" should be implemented
    // Currently, the term will disappear

    return serializable;
  }

  private boolean participatesInProject(Attribute attribute) {

    String booleanValue = attribute.getValue().getValue();
    return booleanValue.equals("true");

  }


}
