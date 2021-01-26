package de.samply.share.client.util.connector.idmanagement.results;

public class QueryResultParserException extends Exception {

  public QueryResultParserException() {
  }

  public QueryResultParserException(String message) {
    super(message);
  }

  public QueryResultParserException(String message, Throwable cause) {
    super(message, cause);
  }

  public QueryResultParserException(Throwable cause) {
    super(cause);
  }

  public QueryResultParserException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
