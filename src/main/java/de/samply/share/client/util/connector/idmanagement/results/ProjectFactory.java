package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Identification;
import de.samply.project.directory.client.Project;
import de.samply.project.directory.client.ProjectDirectory;
import de.samply.project.directory.client.ProjectDirectoryException;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.connector.idmanagement.utils.IdManagementUtils;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Container;
import de.samply.share.model.ccp.ObjectFactory;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import javax.xml.bind.JAXBElement;

public class ProjectFactory {

  private final String projectContainerDesignation = "Project";
  private final ProjectDirectoryUtils projectDirectoryUtils;
  private final ObjectFactory objectFactory = new ObjectFactory();


  private final String projectIdMdrId;
  private final String localPspIdMdrId;
  private final String globalPspIdMdrId;
  private final String projectParticipationMdrId;
  private final String projectNameMdrId;
  private final String projectTypeMdrId;


  /**
   * Todo David.
   * @param projectDirectoryUtils Todo David
   * @param mdrClient Todo David
   * @throws ProjectFactoryException ProjectFactoryException
   */
  public ProjectFactory(ProjectDirectoryUtils projectDirectoryUtils, MdrClient mdrClient)
      throws ProjectFactoryException {

    this.projectDirectoryUtils = projectDirectoryUtils;

    projectIdMdrId = getLatest(ProjectDirectoryUtils.PROJECT_ID_MDR_ID, mdrClient);
    localPspIdMdrId = getLatest(ProjectDirectoryUtils.LOCAL_PSP_ID_MDR_ID, mdrClient);
    globalPspIdMdrId = getLatest(ProjectDirectoryUtils.GLOBAL_PSP_ID_MDR_ID, mdrClient);
    projectParticipationMdrId = getLatest(ProjectDirectoryUtils.PROJECT_PARTICIPATION_MDR_ID,
        mdrClient);
    projectNameMdrId = getLatest(ProjectDirectoryUtils.PROJECT_NAME, mdrClient);
    projectTypeMdrId = getLatest(ProjectDirectoryUtils.PROJECT_TYPE, mdrClient);

  }


  /**
   * Todo David.
   * @param projectIds Todo David
   * @param projectContainerId Todo David
   * @return Todo David
   * @throws ProjectFactoryException ProjectFactoryException
   */
  public Container createProject(List<IdObject> projectIds, int projectContainerId)
      throws ProjectFactoryException {

    Container project = objectFactory.createContainer();

    List<Attribute> attributes = project.getAttribute();

    Attribute projectIdAttribute = createProjectIdAttribute(projectIds.get(0));
    Attribute projectParticipationAttribute = createProjectParticipationAttribute();
    Attribute projectNameAttribute = createProjectNameAttribute(projectIds.get(0));
    Attribute projectType = createProjectType(projectIds.get(0));

    attributes.add(projectIdAttribute);

    attributes.add(projectParticipationAttribute);
    attributes.add(projectNameAttribute);
    attributes.add(projectType);

    for (IdObject projectId : projectIds) {
      Attribute pspIdAttribute = createPspIdAttribute(projectId);
      attributes.add(pspIdAttribute);
    }


    attributes.removeAll(Collections.singleton(null));

    project.setId("" + projectContainerId);
    project.setDesignation(projectContainerDesignation);

    return project;

  }


  private Attribute createProjectParticipationAttribute() {
    return createAttribute(projectParticipationMdrId, "true");
  }
  /*
    private String getProjectContainerId (IdObject projectId){
        return projectId.getIdString();
    }
   */

  private Attribute createProjectIdAttribute(IdObject projectId) {

    String projectDirectoryId = getProjectDirectoryId(projectId);
    return createAttribute(projectIdMdrId, projectDirectoryId);

  }

  private Attribute createPspIdAttribute(IdObject projectId) {

    String pspIdMdrId =
        IdManagementUtils.isLocalIdType(projectId.getIdType()) ? localPspIdMdrId : globalPspIdMdrId;
    return createAttribute(pspIdMdrId, projectId.getIdString());

  }

  private Attribute createProjectType(IdObject projectId) throws ProjectFactoryException {

    String projectType = getProjectType(projectId);
    return (projectType != null) ? createAttribute(projectTypeMdrId, projectType) : null;

  }

  private Attribute createProjectNameAttribute(IdObject projectId) throws ProjectFactoryException {

    String projectName = getProjectName(projectId);
    return (projectName != null) ? createAttribute(projectNameMdrId, projectName) : null;

  }

  private String getProjectDirectoryId(IdObject projectId) {

    String idType = projectId.getIdType();
    return IdManagementUtils.getProjectDirectoryId(idType);

  }

  private String getProjectType(IdObject projectId) throws ProjectFactoryException {
    return getProjectElement(projectId, p -> p.getType());
  }

  private String getProjectName(IdObject projectId) throws ProjectFactoryException {
    return getProjectElement(projectId, p -> p.getName());
  }

  private <T> T getProjectElement(IdObject projectId, Function<Project, T> projectGetter)
      throws ProjectFactoryException {

    Project project = getProject(projectId);
    return (project != null) ? projectGetter.apply(project) : null;

  }

  private Project getProject(IdObject projectId) throws ProjectFactoryException {
    try {
      return getProject_WithoutManagementException(projectId);
    } catch (ProjectDirectoryException e) {
      throw new ProjectFactoryException(e);
    }
  }

  private Project getProject_WithoutManagementException(IdObject projectId)
      throws ProjectDirectoryException {

    String projectDirectoryId = getProjectDirectoryId(projectId);
    ProjectDirectory projectDirectory = projectDirectoryUtils.getProjectDirectory();

    return projectDirectory.getProject(projectDirectoryId);

  }

  private String getLatest(String mdrIdS, MdrClient mdrClient) throws ProjectFactoryException {

    MdrIdDatatype mdrId = new MdrIdDatatype(mdrIdS);
    String latestMdr = mdrId.getLatestMdr();

    DataElement dataElement = getDataElement(latestMdr, mdrClient);
    if (dataElement != null) {
      Identification identification = dataElement.getIdentification();
      if (identification != null && identification.getUrn() != null) {
        latestMdr = identification.getUrn();
      }

    }

    return latestMdr;

  }

  private DataElement getDataElement(String latestMdr, MdrClient mdrClient)
      throws ProjectFactoryException {
    try {
      return mdrClient.getDataElement(latestMdr, "de");
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      throw new ProjectFactoryException(e);
    }
  }

  private Attribute createAttribute(String mdrKey, String value) {

    Attribute attribute = objectFactory.createAttribute();

    JAXBElement<String> jaxbValue = objectFactory.createValue(value);

    attribute.setMdrKey(mdrKey);
    attribute.setValue(jaxbValue);

    return attribute;

  }


}
