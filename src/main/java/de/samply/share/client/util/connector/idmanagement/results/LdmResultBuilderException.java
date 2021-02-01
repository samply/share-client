package de.samply.share.client.util.connector.idmanagement.results;

public class LdmResultBuilderException extends Exception {

  public LdmResultBuilderException() {
  }

  public LdmResultBuilderException(String message) {
    super(message);
  }

  public LdmResultBuilderException(String message, Throwable cause) {
    super(message, cause);
  }

  public LdmResultBuilderException(Throwable cause) {
    super(cause);
  }

  public LdmResultBuilderException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
