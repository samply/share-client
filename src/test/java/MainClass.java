import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.model.Bundle;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainClass {

    private static FhirContext ctx = FhirContext.forR4();
    private static IGenericClient client;
    private static IParser parser = ctx.newXmlParser().setPrettyPrint(true);
    private static final List<Bundle> bundles = new ArrayList<>();

    public static void main(String[]args) throws Exception{

        client = ctx.newRestfulGenericClient("http://localhost:18080/Teiler2/postcts");
        client.registerInterceptor(new BasicAuthInterceptor("admin", "adminpass"));
        /*
        if("TU".equals(args[0])) {
            client = ctx.newRestfulGenericClient("http://localhost:18080/Teiler2/postcts");
        } else {
            ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
            ctx.getRestfulClientFactory().setSocketTimeout(250 * 2000);
            ctx.getRestfulClientFactory().setConnectTimeout(250 * 2000);
            client = ctx.newRestfulGenericClient("http://mitcentrt.srv.med.uni-muenchen.de:8180/DKTK-Teiler/rest/postcts");
            client.registerInterceptor(new BasicAuthInterceptor("admin", "adminpass"));
//            ctx.getRestfulClientFactory().setProxy("medwww.med.uni-muenchen.de", 8080);
//            client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        }
        */
        /*
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogRequestSummary(true);
        loggingInterceptor.setLogRequestBody(true);
        client.registerInterceptor(loggingInterceptor);
        */


        //loadBundles();
        //postBundles();
    }

    private static void postBundles(){
        for(Bundle b: bundles){
//            Bundle response = client.transaction().withBundle(b).execute();
//            System.out.println(parser.encodeResourceToString(response));
            String response = client.transaction().withBundle(parser.encodeResourceToString(b))
                    .withAdditionalHeader("", "")
                    .withAdditionalHeader("Content-Type", "application/xml").withAdditionalHeader("Authorization", "Basic YWRtaW46YWRtaW5wYXNz").execute();
//                    .execute();
            System.out.println(response);
        }
    }

    private static void loadBundles() throws Exception{
        File dir = new File("C:\\home\\Projekte\\nNGM\\26_Standorte\\München\\bundles");
        for(File entry: dir.listFiles()){
            try{
                bundles.add(parser.parseResource(Bundle.class, FileUtils.readFileToString(entry, StandardCharsets.UTF_8)));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
