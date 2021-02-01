package de.samply.share.client.util.connector.idmanagement.query;

import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;

public class LdmQueryConverterFactoryParameters {

  private IdManagementConnector idManagementConnector;
  private ProjectDirectoryUtils projectDirectoryUtils;

  public IdManagementConnector getIdManagementConnector() {
    return idManagementConnector;
  }

  public void setIdManagementConnector(IdManagementConnector idManagementConnector) {
    this.idManagementConnector = idManagementConnector;
  }

  public ProjectDirectoryUtils getProjectDirectoryUtils() {
    return projectDirectoryUtils;
  }

  public void setProjectDirectoryUtils(ProjectDirectoryUtils projectDirectoryUtils) {
    this.projectDirectoryUtils = projectDirectoryUtils;
  }

}
