package de.samply.share.client.util.connector.exception;

/**
 * Exception while CCP component connection.
 */
public class ComponentConnectorException extends Exception {

  public ComponentConnectorException(String message) {
    super(message);
  }

  public ComponentConnectorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ComponentConnectorException(Throwable cause) {
    super(cause);
  }
}
