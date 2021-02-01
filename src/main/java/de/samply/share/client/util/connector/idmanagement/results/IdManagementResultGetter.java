package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.common.mdrclient.MdrClient;
import de.samply.project.directory.client.ProjectDirectoryException;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnectorException;
import de.samply.share.client.util.connector.idmanagement.utils.IdManagementUtils;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.model.ccp.Container;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdManagementResultGetter {

  private final IdManagementConnector idManagementConnector;
  private final ProjectDirectoryUtils projectDirectoryUtils;
  private final ProjectFactory projectFactory;
  private final ObjectFactory objectFactory = new ObjectFactory();


  /**
   * Todo David.
   *
   * @param idManagementConnector Todo David
   * @param projectDirectoryUtils Todo David
   * @param mdrClient             Todo David
   * @throws IdManagementResultGetterException IdManagementResultGetterException
   */
  public IdManagementResultGetter(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils, MdrClient mdrClient)
      throws IdManagementResultGetterException {

    this.idManagementConnector = idManagementConnector;
    this.projectDirectoryUtils = projectDirectoryUtils;
    this.projectFactory = createProjectFactory(projectDirectoryUtils, mdrClient);

  }

  private ProjectFactory createProjectFactory(ProjectDirectoryUtils projectDirectoryUtils,
      MdrClient mdrClient) throws IdManagementResultGetterException {
    try {
      return new ProjectFactory(projectDirectoryUtils, mdrClient);
    } catch (ProjectFactoryException e) {
      throw new IdManagementResultGetterException(e);
    }
  }

  /**
   * Todo David.
   *
   * @param patientIds Todo David
   * @return Todo David
   * @throws IdManagementResultGetterException IdManagementResultGetterException
   */
  public QueryResult getResult(List<String> patientIds) throws IdManagementResultGetterException {

    List<IdObject> searchIds = getSearchIds(patientIds);
    List<String> resultIdTypes = getResultIdTypes();

    return createQueryResult(searchIds, resultIdTypes);

  }

  private QueryResult createQueryResult(List<IdObject> searchIds, List<String> resultIdTypes)
      throws IdManagementResultGetterException {

    try {
      return createQueryResult_WithoutManagementException(searchIds, resultIdTypes);
    } catch (IdManagementConnectorException e) {
      throw new IdManagementResultGetterException(e);
    }

  }

  private QueryResult createQueryResult_WithoutManagementException(List<IdObject> searchIds,
      List<String> resultIdTypes)
      throws IdManagementConnectorException, IdManagementResultGetterException {

    QueryResult queryResult = new QueryResult();
    List<Patient> patients = queryResult.getPatient();

    Map<IdObject, List<IdObject>> ids = getIdsFromIdManagementConnector(searchIds, resultIdTypes);

    for (IdObject localPatientId : ids.keySet()) {

      List<IdObject> projectIds = ids.get(localPatientId);
      Patient patient = createPatient(localPatientId, projectIds);
      patients.add(patient);

    }

    return queryResult;

  }
  // Use this for production

  private Map<IdObject, List<IdObject>> getIdsFromIdManagementConnector(List<IdObject> searchIds,
      List<String> resultIdTypes) throws IdManagementConnectorException {
    return idManagementConnector.getIds(searchIds, resultIdTypes);
  }

  /*
    // Use this for testing
    private Map<IdObject, List<IdObject>> getIdsFromIdManagementConnector(List<IdObject> searchIds,
     List<String> resultIdTypes) throws IdManagementConnectorException {
        return getIdsFromIdManagementConnectorMockup(searchIds, resultIdTypes);
    }

    */

  // Do not remove this method: Use for testing when magic pl cannot return ids
  private Map<IdObject, List<IdObject>> getIdsFromIdManagementConnectorMockup(
      List<IdObject> searchIds, List<String> resultIdTypes) {

    Map<IdObject, List<IdObject>> myMap = new HashMap<>();

    int counter = 100;

    for (IdObject searchId : searchIds) {

      if (counter == 0) {
        break;
      } else {
        counter--;
      }
      List<IdObject> myIdObjects = new ArrayList<>();

      for (String resultIdType : resultIdTypes) {

        IdObject idObject = new IdObject(resultIdType, resultIdType + "-test-id-" + counter);
        myIdObjects.add(idObject);

      }

      myMap.put(searchId, myIdObjects);


    }

    return myMap;


  }


  private Patient createPatient(IdObject localPatientId, List<IdObject> projectIds)
      throws IdManagementResultGetterException {

    Patient patient = objectFactory.createPatient();

    patient.setId(localPatientId.getIdString());
    patient.setIdType(localPatientId.getIdType());

    List<Container> patientProjects = patient.getContainer();

    int counter = 0;
    List<List<IdObject>> projectIdsGroupedByProjectDirectoryId = groupByProjectDirectoryId(
        projectIds);
    for (List<IdObject> idObjects : projectIdsGroupedByProjectDirectoryId) {
      Container project = createProject(idObjects, ++counter);
      patientProjects.add(project);
    }

    return patient;

  }


  private List<List<IdObject>> groupByProjectDirectoryId(List<IdObject> projectIds) {

    Map<String, List<IdObject>> projectDirectoryIdProjectIds = new HashMap<>();

    for (IdObject projectId : projectIds) {

      String projectDirectoryId = IdManagementUtils.getProjectDirectoryId(projectId.getIdType());
      List<IdObject> idObjects = projectDirectoryIdProjectIds.get(projectDirectoryId);
      if (idObjects == null) {
        idObjects = new ArrayList<>();
        projectDirectoryIdProjectIds.put(projectDirectoryId, idObjects);
      }

      idObjects.add(projectId);

    }

    List<List<IdObject>> result = new ArrayList<>();

    for (List<IdObject> idObjects : projectDirectoryIdProjectIds.values()) {
      result.add(idObjects);
    }

    return result;

  }

  private Container createProject(List<IdObject> projectId, int projectContainerId)
      throws IdManagementResultGetterException {
    try {
      return projectFactory.createProject(projectId, projectContainerId);
    } catch (ProjectFactoryException e) {
      throw new IdManagementResultGetterException(e);
    }
  }


  private List<String> getResultIdTypes() throws IdManagementResultGetterException {
    try {
      return projectDirectoryUtils.getAllProjectIds();
    } catch (ProjectDirectoryException e) {
      throw new IdManagementResultGetterException(e);
    }
  }

  private List<IdObject> getSearchIds(List<String> patientIds) {

    List<IdObject> searchIds = new ArrayList<>();
    String idType = IdManagementUtils.getDefaultPatientLocalIdType();

    for (String idString : patientIds) {
      IdObject idObject = new IdObject(idType, idString);
      searchIds.add(idObject);
    }

    return searchIds;

  }


}
