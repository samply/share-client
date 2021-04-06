package de.samply.share.client.util.connector.exception;

/**
 * ConflictException to throw data conflicts.
 */
public class ConflictException extends Exception {
  public ConflictException(String message) {
    super(message);
  }

  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConflictException(Throwable cause) {
    super(cause);
  }
}
