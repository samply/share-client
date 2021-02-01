package de.samply.share.client.util.connector.idmanagement.results;

public class QueryResultsBuilderException extends Exception {

  public QueryResultsBuilderException() {
  }

  public QueryResultsBuilderException(String message) {
    super(message);
  }

  public QueryResultsBuilderException(String message, Throwable cause) {
    super(message, cause);
  }

  public QueryResultsBuilderException(Throwable cause) {
    super(cause);
  }

  public QueryResultsBuilderException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
