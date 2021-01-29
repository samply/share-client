package de.samply.share.client.util.connector;

import de.samply.share.client.model.ComponentInfo;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.util.connector.exception.ComponentConnectorException;

/**
 * Interface for receive basic information of component connectors.
 */
public interface IcomponentBasicInfoConnector {

  ComponentInfo getComponentInfo() throws ComponentConnectorException;

  String getComponentInfoString() throws ComponentConnectorException;

  CheckResult checkConnection();
}
