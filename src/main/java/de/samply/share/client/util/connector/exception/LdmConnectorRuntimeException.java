package de.samply.share.client.util.connector.exception;

public class LdmConnectorRuntimeException extends RuntimeException {

  public LdmConnectorRuntimeException(String message) {
    super(message);
  }

  public LdmConnectorRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public LdmConnectorRuntimeException(Throwable cause) {
    super(cause);
  }


}
