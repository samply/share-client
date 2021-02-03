package de.samply.share.client.util.connector;

import de.samply.share.client.model.ComponentInfo;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.util.connector.exception.ComponentConnectorException;

/**
 * Interface for receive basic information of component connectors.
 */
public interface IcomponentBasicInfoConnector {
  
  /**
   * Gets component info.
   *
   * @return the component info
   * @throws ComponentConnectorException the component connector exception
   */
  ComponentInfo getComponentInfo() throws ComponentConnectorException;
  
  /**
   * Gets component info string.
   *
   * @return the component info string
   * @throws ComponentConnectorException the component connector exception
   */
  String getComponentInfoString() throws ComponentConnectorException;
  
  /**
   * Check connection check result.
   *
   * @return the check result
   */
  CheckResult checkConnection();
}
