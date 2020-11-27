package de.samply.share.client.util.connector.exception;

/**
 * Created by michael on 15.03.17.
 */
public class BrokerConnectorException extends Exception {

  public BrokerConnectorException(String message) {
    super(message);
  }

  public BrokerConnectorException(String message, Throwable cause) {
    super(message, cause);
  }

  public BrokerConnectorException(Throwable cause) {
    super(cause);
  }


}
