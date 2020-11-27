package de.samply.share.client.quality.report.results.operations;

public class QualityResultsValidatorException extends Exception {

  public QualityResultsValidatorException(String message) {
    super(message);
  }

  public QualityResultsValidatorException(Throwable throwable) {
    super(throwable);
  }

  public QualityResultsValidatorException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
