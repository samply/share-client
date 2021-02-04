package de.samply.share.client.util.connector.exception;

/**
 * Exception while CCP component connection.
 */
public class ComponentConnectorException extends Exception {
  
  /**
   * Instantiates a new Component connector exception.
   *
   * @param message the message
   */
  public ComponentConnectorException(String message) {
    super(message);
  }
  
  /**
   * Instantiates a new Component connector exception.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ComponentConnectorException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Instantiates a new Component connector exception.
   *
   * @param cause the cause
   */
  public ComponentConnectorException(Throwable cause) {
    super(cause);
  }
}
