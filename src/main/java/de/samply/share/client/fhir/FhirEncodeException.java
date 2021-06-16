package de.samply.share.client.fhir;

import ca.uhn.fhir.parser.DataFormatException;

public class FhirEncodeException extends RuntimeException {

  public FhirEncodeException(String message, DataFormatException cause) {
    super(message,cause);
  }
}
