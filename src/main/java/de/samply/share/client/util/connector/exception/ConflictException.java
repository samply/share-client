package de.samply.share.client.util.connector.exception;

/**
 * ConflictException to throw data conflicts.
 */
public class ConflictException extends Exception {

  public ConflictException(String message) {
    super(message);
  }
}
