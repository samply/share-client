package de.samply.share.client.fhir;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import java.util.Objects;
import javax.ws.rs.core.MediaType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FhirUtil {

  private static final Logger logger = LoggerFactory.getLogger(FhirUtil.class);

  private final FhirContext ctx;

  public FhirUtil(FhirContext ctx) {
    this.ctx = Objects.requireNonNull(ctx);
  }

  /**
   * Parse a bundle String to a bundle object.
   *
   * @param bundleString the bundle as String
   * @param mediaType    JSON or XML
   * @return bundle String as bundle object
   * @throws FhirParseException wrong configuration or wrong data format
   */
  public Bundle parseBundleResource(String bundleString, MediaType mediaType)
      throws FhirParseException {
    Objects.requireNonNull(bundleString);
    Objects.requireNonNull(mediaType);
    try {
      if ("json".equals(mediaType.getSubtype())) {
        return (Bundle) ctx.newJsonParser().parseResource(bundleString);
      } else {
        return (Bundle) ctx.newXmlParser().parseResource(bundleString);
      }
    } catch (ConfigurationException | DataFormatException e) {
      logger.error(e.getMessage(),e);
      throw new FhirParseException("Error while parsing a " + mediaType.getSubtype() + " bundle.",
          e);
    }
  }

  /**
   * Encode a resource to JSON String.
   *
   * @param resource the fhir resource
   * @return the resource as JSON String
   * @throws FhirEncodeException if resource can not be encoded
   */
  public String encodeResourceToJson(Resource resource) throws FhirEncodeException {
    try {
      return ctx.newJsonParser().encodeResourceToString(resource);
    } catch (DataFormatException e) {
      logger.error(e.getMessage(),e);
      throw new FhirEncodeException("Error while encoding a bundle to json.", e);
    }
  }
}
