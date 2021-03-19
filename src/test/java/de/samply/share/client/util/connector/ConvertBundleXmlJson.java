package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import de.samply.share.client.fhir.FhirResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;



public class ConvertBundleXmlJson {
    private final FhirContext ctx = FhirContext.forR4();

    private static final FhirResource fhirResource = new FhirResource();

    public String convertToBundleResource(String bundleString, String mediaType)
            throws ConfigurationException, DataFormatException {
        if (mediaType.equals("json")) {

            return  ctx.newXmlParser().encodeResourceToString(ctx.newJsonParser().parseResource(bundleString));
        } else {
           // return (Bundle) ctx.newXmlParser().parseResource(bundleString);
            return  ctx.newJsonParser().encodeResourceToString(ctx.newXmlParser().parseResource(bundleString));
        }
    }

    public static void main(String[] args) {

        ConvertBundleXmlJson  convertBundleXmlJson = new ConvertBundleXmlJson();
       //String file = "C:\\tmp\\nngmprofile\\nngm2021\\Vergleiche\\option1\\standorte\\antrag_Bonn.json";
        String file = "C:\\tmp\\nngmprofile\\nngm2021\\Vergleiche\\option1\\standorte\\Antrag_bonn_korrigiert.xml";
        try {
        String contentToConvert = new String(Files.readAllBytes(Paths.get(file)));
        String zielBundle=     convertBundleXmlJson.convertToBundleResource(contentToConvert, "xml");
        System.out.println(zielBundle);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

