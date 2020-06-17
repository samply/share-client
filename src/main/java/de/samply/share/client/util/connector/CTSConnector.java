package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FHIRResource;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;

import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class CTSConnector {

    private static final Logger logger = LogManager.getLogger(CTSConnector.class);

    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String ctsBaseUrl;
    private HttpHost ctsHost;


    public CTSConnector() {
        init();
    }

    private void init() {
        try {
            this.ctsBaseUrl = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL));
            httpConnector = ApplicationBean.createHttpConnector();
            this.ctsHost = SamplyShareUtils.getAsHttpHost(ctsBaseUrl);
            httpClient = httpConnector.getHttpClient(ctsHost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a stringified FHIR Bundle, assumed to be containing identifying patient data (IDAT),
     * replaces the IDAT with a pseudonym supplied by the Mainzelliste, and then sends the
     * pseudonymized bundle to the CTS data upload endpoint.
     *
     * @param bundleString
     * @throws IOException
     * @throws ConfigurationException
     * @throws DataFormatException
     * @throws IllegalArgumentException
     */
    public void postPseudonmToCTS(String bundleString, String mediaType) throws IOException, ConfigurationException, DataFormatException, IllegalArgumentException {
        // Make a call to the PL, and replace patient identifying information in the
        // bundle with a pseudonym.
        Bundle pseudonymBundle = pseudonymiseBundle(bundleString, mediaType);

        // Make sure that the bundle contains the correct CTS profile
        insertProfile(pseudonymBundle, "http://uk-koeln.de/fhir/StructureDefinition/Bundle/nNGM/registration-form");

        String pseudonymBundleXml = serializeToXml(pseudonymBundle);

        postStringToCTS(pseudonymBundleXml);
    }

    private String serializeToXml(Bundle pseudonymBundle) {
        FhirContext ctx = FhirContext.forR4();
        String string = ctx.newXmlParser().encodeResourceToString(pseudonymBundle);
        return string;
    }

    /**
     * Posts the supplied string directly to the CTS data upload endpoint. No checking is performed
     * on the string format.
     *
     * @param string
     * @throws IOException
     * @throws ConfigurationException
     * @throws DataFormatException
     * @throws IllegalArgumentException
     */
    public void postStringToCTS(String string) throws IOException, ConfigurationException, DataFormatException, IllegalArgumentException {
        HttpEntity entity = new StringEntity(string, Consts.UTF_8);
        String ctsUri = SamplyShareUtils.addTrailingSlash(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL));
        HttpPost httpPost = new HttpPost(ctsUri);
        List<String> ctsInfo = getCtsInfo();
        httpPost.setHeader("Cookie", "SDMS_code=" + ctsInfo.get(0) + "; SDMS_user=" + ctsInfo.get(1));
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/fhir+xml; fhirVersion=4.0");
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/fhir+xml; fhirVersion=4.0");
        httpPost.setEntity(entity);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 500 && statusCode < 600) {
                logger.error("CTS server error, statusCode: " + statusCode + ", status: " + response.toString());
                throw new IOException("CTS server error, statusCode: " + statusCode + ", status: " + response.toString());
            }
            if (statusCode >= 400 && statusCode < 500) {
                logger.error("CTS permission problem, statusCode: " + statusCode + ", status: " + response.toString());
                throw new IllegalArgumentException("CTS permission problem, statusCode: " + statusCode + ", status: " + response.toString());
            }
        } catch (IOException e) {
            logger.error("IOException, URI: " + httpPost.getURI() + ", e: " + e);
            throw new IOException("IOException, URI: " + httpPost.getURI() + ", e: " + e);
        }
    }

    private Bundle pseudonymiseBundle(String bundleString, String mediaType) throws IOException, ConfigurationException, DataFormatException {
        FHIRResource fhirResource = new FHIRResource();
        Bundle bundle = fhirResource.convertToBundleResource(bundleString, mediaType);
        MainzellisteConnector mainzellisteConnector = new MainzellisteConnector();
        Bundle pseudonymizedBundle = mainzellisteConnector.getPatientPseudonym(bundle);
        return pseudonymizedBundle;
    }

    private static void insertProfile(Resource resource, String profile) {
        Meta m = resource.getMeta();
        m.setProfile(null); // wipe existing profiles
        m.addProfile(profile);
        resource.setMeta(m);
    }

    /**
     * Returns CTS code and CTS user as elements 0 and 1 of a list, respectively.
     *
     * @return
     */
    private List<String> getCtsInfo() {
        List<String> ctsInfo = new ArrayList<String>();

        ctsInfo.add("g845upw5zyji1w90n45pw0z38ijgmtttxxqzio6e_1591624687_ceb068b41cda018bf993a25d5b84795f");
        ctsInfo.add("admin");

        return ctsInfo;
    }
}
