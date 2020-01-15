package de.samply.share.client.fhir;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import org.hl7.fhir.r4.model.Bundle;

public class FHIRResource {
    private FhirContext ctx= FhirContext.forR4();

    public Bundle convertToBundleResource(String bundleString) throws ConfigurationException, DataFormatException{
        return (Bundle)ctx.newXmlParser().parseResource(bundleString);
    }
}
