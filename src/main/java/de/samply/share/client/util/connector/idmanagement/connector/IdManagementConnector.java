package de.samply.share.client.util.connector.idmanagement.connector;

import de.samply.share.client.model.IdObject;
import java.util.List;
import java.util.Map;

public interface IdManagementConnector {

  /** Todo David.
   * @param searchIds     List of IDObjects with IdString and IdType
   * @param resultIdTypes List of IdTypes which should be returned in exchange of searchIds
   * @return searchIds mapped to list of result Id Objects
   * @throws IdManagementConnectorException IdManagementConnectorException
   */
  Map<IdObject, List<IdObject>> getIds(List<IdObject> searchIds, List<String> resultIdTypes)
      throws IdManagementConnectorException;

  /**
   * Find all patients which have at least one id with the given searchIdType and return there id
   * with the specified resultIdType. (DKTK000002016_Teststandort_L-ID, BK_Teststandort_L-ID)
   * List of IdObject(BK_Teststandort_L-ID).
   *
   * @param searchIdType IdType for which all patients with ids should be requested
   * @param resultIdType the type of patient ids which should be returned
   * @return a list of IdObject with the given resultIdType
   * @throws IdManagementConnectorException IdManagementConnectorException
   */
  List<IdObject> getAllIds(String searchIdType, String resultIdType)
      throws IdManagementConnectorException;

  /**
   * DKTK000002016_Teststandort_L-ID -- List { IDs (BK_Teststandort_L-ID) } .
   * Todo David umformulieren.
   *
   * @param searchIdType Todo David
   * @return Todo David
   */
  List<IdObject> getAllLocalIds(String searchIdType) throws IdManagementConnectorException;

  /**
   * find all patients which have at least one id with the given searchIdType and return there
   * export ids.
   *
   * @param searchIds IdType for which all patients with ids should be requested
   * @return Todo David
   * @throws IdManagementConnectorException IdManagementConnectorException
   */
  Map<IdObject, IdObject> getExportIds(List<IdObject> searchIds)
      throws IdManagementConnectorException;

  /**
   * Generate random export ids.
   *
   * @param searchIds Todo David
   * @return Todo David
   * @throws IdManagementConnectorException IdManagementConnectorException
   */
  Map<IdObject, IdObject> getRandomExportIds(List<IdObject> searchIds)
      throws IdManagementConnectorException;

}
