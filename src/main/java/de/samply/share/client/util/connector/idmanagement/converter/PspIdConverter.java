package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.project.directory.client.ProjectDirectoryException;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnector;
import de.samply.share.client.util.connector.idmanagement.connector.IdManagementConnectorException;
import de.samply.share.client.util.connector.idmanagement.utils.IdManagementUtils;
import de.samply.share.client.util.connector.idmanagement.utils.ProjectDirectoryUtils;
import de.samply.share.client.util.connector.idmanagement.utils.TermFactory;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.In;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PspIdConverter extends PatientInConverter {

  private final IdManagementConnector idManagementConnector;
  private final ProjectDirectoryUtils projectDirectoryUtils;


  public PspIdConverter(IdManagementConnector idManagementConnector,
      ProjectDirectoryUtils projectDirectoryUtils) {
    this.idManagementConnector = idManagementConnector;
    this.projectDirectoryUtils = projectDirectoryUtils;
  }

  @Override
  public Serializable convert(Serializable serializable) throws SerializableConverterException {

    String pspId = getPspId(serializable);

    return (pspId != null && pspId.length() > 0) ? createInTerm(pspId) : null;
  }

  private String getPspId(Serializable serializable) {

    Attribute attribute = TermFactory.getAttribute(serializable);
    return (attribute != null) ? attribute.getValue().getValue() : null;

  }

  private In createInTerm(String pspId) throws SerializableConverterException {

    In inTerm = null;

    if (pspId != null) {

      Set<String> localPatientIds = getLocalPatientIds(pspId);
      if (!localPatientIds.isEmpty()) {
        inTerm = createInTerm(localPatientIds);
      }

    }

    return inTerm;
  }


  private Set<String> getLocalPatientIds(String pspId) throws SerializableConverterException {

    Set<String> localPatientIds = new HashSet<>();

    List<IdObject> searchIds = getSearchIds(pspId);
    List<String> resultIds = getResultIds();

    return getLocalPatientIds(searchIds, resultIds);

  }

  private Set<String> getLocalPatientIds(List<IdObject> searchIds, List<String> resultIds)
      throws SerializableConverterException {
    try {
      return getLocalPatientIds_WithoutManagementException(searchIds, resultIds);
    } catch (IdManagementConnectorException e) {
      throw new SerializableConverterException(e);
    }
  }

  private Set<String> getLocalPatientIds_WithoutManagementException(List<IdObject> searchIds,
      List<String> resultIds) throws IdManagementConnectorException {

    Map<IdObject, List<IdObject>> ids = idManagementConnector.getIds(searchIds, resultIds);

    Set<String> localPatientIds = new HashSet<>();

    for (List<IdObject> idObjectList : ids.values()) {
      for (IdObject idObject : idObjectList) {

        String localPatientId = idObject.getIdString();
        localPatientIds.add(localPatientId);

      }
    }

    return localPatientIds;

  }

  private List<IdObject> getSearchIds(String pspId) throws SerializableConverterException {

    List<IdObject> searchIds = new ArrayList<>();

    for (String projectIdType : getProjectIdTypes()) {

      IdObject idObject = new IdObject(projectIdType, pspId);
      searchIds.add(idObject);

    }

    return searchIds;
  }

  private List<String> getProjectIdTypes() throws SerializableConverterException {
    try {
      return projectDirectoryUtils.getAllProjectIds();
    } catch (ProjectDirectoryException e) {
      throw new SerializableConverterException(e);
    }
  }

  private List<String> getResultIds() {

    List<String> patientLocalIdType = new ArrayList<>();

    String defaultPatientLocalIdType = IdManagementUtils.getDefaultPatientLocalIdType();
    patientLocalIdType.add(defaultPatientLocalIdType);

    return patientLocalIdType;

  }


}
