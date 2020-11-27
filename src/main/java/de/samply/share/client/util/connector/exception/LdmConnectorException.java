package de.samply.share.client.util.connector.exception;

/**
 * Created by michael on 15.03.17.
 */
public class LdmConnectorException extends Exception {

  public LdmConnectorException(String message) {
    super(message);
  }

  public LdmConnectorException(String message, Throwable cause) {
    super(message, cause);
  }

  public LdmConnectorException(Throwable cause) {
    super(cause);
  }


}
