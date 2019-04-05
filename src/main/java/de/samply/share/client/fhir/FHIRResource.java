package de.samply.share.client.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Bundle;

public class FHIRResource {
    private FhirContext ctx= FhirContext.forDstu3();

    public Bundle convertToBundleResource(String bundleString){
        return (Bundle)ctx.newXmlParser().parseResource(bundleString);
    }

}
