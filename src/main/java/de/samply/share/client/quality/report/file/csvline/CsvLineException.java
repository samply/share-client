package de.samply.share.client.quality.report.file.csvline;

public class CsvLineException extends Exception {

  public CsvLineException(String message) {
    super(message);
  }

  public CsvLineException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvLineException(Throwable cause) {
    super(cause);
  }

}
