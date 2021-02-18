package de.samply.share.client.util.connector.idmanagement.utils;

import de.samply.project.directory.client.Project;
import de.samply.project.directory.client.ProjectDirectory;
import de.samply.project.directory.client.ProjectDirectoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ProjectDirectoryUtils {

  public static final String PROJECT_ID_MDR_ID = "urn:dktk:dataelement:112";
  public static final String PROJECT_PARTICIPATION_MDR_ID = "urn:dktk:dataelement:111";
  public static final String LOCAL_PSP_ID_MDR_ID = "urn:dktk:dataelement:113";
  public static final String GLOBAL_PSP_ID_MDR_ID = "urn:dktk:dataelement:117";
  public static final String PROJECT_NAME = "urn:dktk:dataelement:114";
  public static final String PROJECT_TYPE = "urn:dktk:dataelement:115";
  public static final String DATA_CATEGORY = "urn:dktk:dataelement:116";
  private final ProjectDirectory projectDirectory;
  private Map<String, List<String>> projectIdIdTypesMap;
  private Map<String, List<String>> projectTypeIdTypesMap;
  private Map<String, List<String>> projectNameIdTypesMap;
  private Map<String, List<String>> datacategoryIdTypesMap;
  private List<String> projectIdTypes;


  public ProjectDirectoryUtils(ProjectDirectory projectDirectory) {
    this.projectDirectory = projectDirectory;
  }

  /**
   * Todo David.
   * @return Todo David
   * @throws ProjectDirectoryException ProjectDirectoryException
   */
  public List<String> getAllProjectIds() throws ProjectDirectoryException {

    if (projectIdTypes == null) {

      projectIdTypes = new ArrayList<>();

      for (List<String> tempProjectIdTypes : getProjectIdIdTypesMap().values()) {
        projectIdTypes.addAll(tempProjectIdTypes);
      }

    }

    return projectIdTypes;

  }

  private Map<String, List<String>> getProjectIdIdTypesMap() throws ProjectDirectoryException {

    if (projectIdIdTypesMap == null) {
      projectIdIdTypesMap = createElement_idTypeMap(p -> p.getId());
    }

    return projectIdIdTypesMap;
  }

  private Map<String, List<String>> createElement_idTypeMap(Function<Project, String> projectGetter)
      throws ProjectDirectoryException {

    Map<String, List<String>> elementIdTypeMap = new HashMap<>();

    for (Project project : projectDirectory.getAllProjects()) {

      String projectId = project.getId();
      String localIdType = IdManagementUtils.getLocalIdManagementProjectId(projectId);
      String globalIdType = IdManagementUtils.getGlobalIdManagementProjectId(projectId);

      String element = projectGetter.apply(project);

      if (element != null) {

        List<String> idTypes = new ArrayList<>();
        if (localIdType != null) {
          idTypes.add(localIdType);
        }
        if (globalIdType != null) {
          idTypes.add(globalIdType);
        }
        if (!idTypes.isEmpty()) {
          elementIdTypeMap.put(element, idTypes);
        }

      }

    }

    return elementIdTypeMap;

  }

  private Map<String, List<String>> getProjectTypeIdTypesMap() throws ProjectDirectoryException {

    if (projectTypeIdTypesMap == null) {

      projectTypeIdTypesMap = new HashMap<>();

      for (Project project : projectDirectory.getAllProjects()) {

        String projectId = project.getId();
        String idType = IdManagementUtils.getLocalIdManagementProjectId(projectId);
        String projectType = project.getType();

        if (idType != null && projectType != null && !projectType.trim().isEmpty()) {

          List<String> idTypes = projectTypeIdTypesMap.get(projectType);

          if (idTypes == null) {

            idTypes = new ArrayList<>();
            projectTypeIdTypesMap.put(projectType, idTypes);

          }

          idTypes.add(idType);

        }

      }

    }

    return projectTypeIdTypesMap;

  }

  private Map<String, List<String>> getProjectNameIdTypesMap() throws ProjectDirectoryException {

    if (projectNameIdTypesMap == null) {
      projectNameIdTypesMap = createElement_idTypeMap(p -> p.getName());
    }

    return projectNameIdTypesMap;

  }

  public ProjectDirectory getProjectDirectory() {
    return this.projectDirectory;
  }

  public List<String> getProjectIdsForProjectType(String projectType)
      throws ProjectDirectoryException {
    return getProjectTypeIdTypesMap().get(projectType);
  }

  public List<String> getProjectIdForProjectName(String projectName)
      throws ProjectDirectoryException {
    return getProjectNameIdTypesMap().get(projectName);
  }

  private Map<String, List<String>> getDatacategoryIdTypesMap() throws ProjectDirectoryException {

    if (datacategoryIdTypesMap == null) {

      datacategoryIdTypesMap = new HashMap<>();

      for (Project project : projectDirectory.getAllProjects()) {

        String projectId = project.getId();
        String idType = IdManagementUtils.getLocalIdManagementProjectId(projectId);

        for (String dataCategory : project.getDataCategory()) {

          if (dataCategory != null && !dataCategory.trim().isEmpty()) {

            List<String> idTypes = datacategoryIdTypesMap.get(dataCategory);
            if (idTypes == null) {

              idTypes = new ArrayList<>();
              datacategoryIdTypesMap.put(dataCategory, idTypes);

            }

            idTypes.add(idType);

          }

        }


      }

    }

    return datacategoryIdTypesMap;

  }

  public List<String> getProjectIdsForDataCategory(String dataCategory)
      throws ProjectDirectoryException {
    return getDatacategoryIdTypesMap().get(dataCategory);
  }


}
