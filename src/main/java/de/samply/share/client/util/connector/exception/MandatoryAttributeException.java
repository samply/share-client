package de.samply.share.client.util.connector.exception;

/**
 * Exception triggered when a mandatory attribute is missing.
 */
public class MandatoryAttributeException extends Exception {

  public MandatoryAttributeException(String message) {
    super(message);
  }
}
