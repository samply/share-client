package de.samply.share.client.util.connector;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class FhirTest {

    @Test
    public void test2() throws IOException, InterruptedException {
// Create an incomplete encounter (status is required)
        String jar = new File(FhirTest.class.getClassLoader().getResource("org.hl7.fhir.validator.jar").getPath()).getAbsolutePath();
        String patient = FhirTest.class.getClassLoader().getResource("patient.xml").getPath();
       // String profile = FhirTest.class.getClassLoader().getResource("profile/patient-nngm-0.2-duplicate-2.xml").getPath();
        Process proc = Runtime.getRuntime().exec("java -Dhttps.proxyHost=193.174.53.221 -Dhttps.proxyPort=3128 -Dhttp.proxyHost=193.174.53.221 -Dhttp.proxyPort=3128 -jar " + jar +" "+patient+ " -version 3.0 -ig hl7.fhir.us.core#3.0.1 -tx n/a -profile https://fhir.simplifier.net/r3/StructureDefinition/2927b343-3c35-4541-a684-b485ae348985");
        proc.waitFor();
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();

        byte b[]=new byte[in.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));

        byte c[]=new byte[err.available()];
        err.read(c,0,c.length);
        System.out.println(new String(c));
    }
}
