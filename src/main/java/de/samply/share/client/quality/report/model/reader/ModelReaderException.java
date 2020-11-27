package de.samply.share.client.quality.report.model.reader;

public class ModelReaderException extends Exception {

  ModelReaderException(String message) {
    super(message);
  }

  ModelReaderException(Throwable throwable) {
    super(throwable);
  }

  ModelReaderException(String message, Throwable throwable) {
    super(message, throwable);
  }


}
