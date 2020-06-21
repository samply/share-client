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
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CTSConnector {

    private static final Logger logger = LogManager.getLogger(CTSConnector.class);

    private transient HttpConnector httpConnector;
    private CloseableHttpClient httpClient;
    private String ctsBaseUrl;
    private HttpHost ctsHost;
    private Credentials credentials;

    public CTSConnector() {
        init();
    }

    private void init() {
        try {
            // Pull various pieces of information from property files and store them
            // in memory.
            ctsBaseUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL);
            httpConnector = ApplicationBean.createHttpConnector();
            ctsHost = SamplyShareUtils.getAsHttpHost(ctsBaseUrl);
            httpClient = httpConnector.getHttpClient(ctsHost);
            credentials = getCtsCredentials();
        } catch (MalformedURLException e) {
            logger.error("URL problem while initializing CTS uploader, e: " + e);
        } catch (IOException e) {
            logger.error("Credentials problem while initializing CTS uploader, e: " + e);
        }
    }

    /**
     * Takes a stringified FHIR Bundle, assumed to be containing identifying patient data (IDAT),
     * replaces the IDAT with a pseudonym, and then sends the pseudonymized bundle to the CTS data
     * upload endpoint.
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

        String pseudonymBundleXml = serializeToJsonString(pseudonymBundle);

        postStringToCTS(pseudonymBundleXml);
    }

    /**
     * Take the supplied FHIR-Bundle object, and return a stringified version of it.
     *
     * @param bundle
     * @return
     */
    private String serializeToJsonString(Bundle bundle) {
        FhirContext ctx = FhirContext.forR4();
        String string = ctx.newJsonParser().encodeResourceToString(bundle);
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
        logger.debug("postStringToCTS: entered");

        HttpEntity entity = new StringEntity(string, Consts.UTF_8);
        HttpPost httpPost = new HttpPost(ctsBaseUrl);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/fhir-json; fhirVersion=4.0");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            HttpContext ctsContext = createCtsContext();
            response = httpClient.execute(httpPost, ctsContext);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("CTS upload status code: " + statusCode);
            if (statusCode >= 500 && statusCode < 600) {
                logger.error("Upload: CTS server error, statusCode: " + statusCode + ", status: " + response.toString());
                throw new IOException("Upload: CTS server error, statusCode: " + statusCode + ", status: " + response.toString());
            }
            if (statusCode >= 400 && statusCode < 500) {
                logger.error("Upload: CTS permission problem, statusCode: " + statusCode + ", status: " + response.toString());
                throw new IllegalArgumentException("Upload: CTS permission problem, statusCode: " + statusCode + ", status: " + response.toString());
            }
        } catch (IOException e) {
            logger.error("Upload: IOException, URI: " + httpPost.getURI() + ", e: " + e);
            throw new IOException("Upload: IOException, URI: " + httpPost.getURI() + ", e: " + e);
        } catch (Exception e) {
            logger.error("Upload: miscellaneous Exception, e: " + e);
            throw new IOException("Upload: miscellaneous Exception, e: " + e);
        } finally {
            response.close();
        }

        logger.debug("postStringToCTS: done");
    }

    /**
     * Create a BasicHttpContext for CTS upload, with the cookies needed for authorization.
     *
     * @return
     */
    private HttpContext createCtsContext() throws IOException {
        CtsAuthorization ctsAuthorization = getCtsAuthorization();

        BasicCookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(createCtsCookie("SDMS_code", ctsAuthorization.code));
        cookieStore.addCookie(createCtsCookie("SDMS_user", ctsAuthorization.user));

        HttpContext ctsContext = new BasicHttpContext();
        ctsContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        return ctsContext;
    }

    /**
     * Create a cookie with the given name and value.
     *
     * Suitable domain and path values will be appended to the cookie.
     *
     * @param name
     * @param value
     * @return
     */
    private BasicClientCookie createCtsCookie(String name, String value) {
        String domain = null;
        try {
            URL url = new URL(ctsBaseUrl);
            domain = url.getHost();
        } catch (MalformedURLException e) {
            logger.warn("Problem finding Healex domain, e: " + e);
        }

        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");

        return cookie;
    }

    /**
     * Pseudonymize any patient data in the bundle.
     *
     * @param bundleString
     * @param mediaType
     * @return
     * @throws IOException
     * @throws ConfigurationException
     * @throws DataFormatException
     */
    private Bundle pseudonymiseBundle(String bundleString, String mediaType) throws IOException, ConfigurationException, DataFormatException {
        FHIRResource fhirResource = new FHIRResource();
        Bundle bundle = fhirResource.convertToBundleResource(bundleString, mediaType);
        MainzellisteConnector mainzellisteConnector = new MainzellisteConnector();
        Bundle pseudonymizedBundle = mainzellisteConnector.getPatientPseudonym(bundle);
        return pseudonymizedBundle;
    }

    /**
     * Place the supplied profile into the Meta-element of the FHIR-resource, wiping
     * any existing profiles.
     *
     * @param resource
     * @param profile
     */
    private static void insertProfile(Resource resource, String profile) {
        Meta m = resource.getMeta();
        m.setProfile(null); // wipe existing profiles
        m.addProfile(profile);
        resource.setMeta(m);
    }

    /**
     * Class for transporting CTS-authorization parameters.
     */
    public class CtsAuthorization {
        String code;
        String user;
    }

    /**
     * Returns CTS code and CTS user.
     *
     * @return
     */
    private CtsAuthorization getCtsAuthorization() throws IOException, IllegalArgumentException {
        logger.debug("getCtsInfo: entered");

        // Get username and password for an SMS login
        Credentials credentials = getCtsCredentials();

        // Build a form-based entity to realize the login
        List<NameValuePair> formElements = new ArrayList<NameValuePair>();
        formElements.add(new BasicNameValuePair("username", credentials.username));
        formElements.add(new BasicNameValuePair("password", credentials.password));
        formElements.add(new BasicNameValuePair("login", "")); // seems to be required, not sure why
        HttpEntity entity = new UrlEncodedFormEntity(formElements, Consts.UTF_8);

        // Build the HttpPost object that specifies the request.
        HttpPost httpPost = new HttpPost(ctsHost.toURI());
        httpPost.setEntity(entity);

        // Run the request and gather the CTS authorization parameters.
        CloseableHttpResponse response = null;
        CtsAuthorization ctsAuthorization = new CtsAuthorization();
        try {
            HttpClientContext context = new HttpClientContext();
            response = httpClient.execute(httpPost, context);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("CTS authorization status code: " + statusCode);
            if (statusCode >= 500 && statusCode < 600) {
                logger.error("Authorization: CTS server error, statusCode: " + statusCode + ", status: " + response.toString());
                throw new IOException("Authorization: CTS server error, statusCode: " + statusCode + ", status: " + response.toString());
            }
            if (statusCode >= 400 && statusCode < 500) {
                logger.error("Authorization: CTS permission problem, statusCode: " + statusCode + ", status: " + response.toString());
                throw new IllegalArgumentException("Authorization: CTS permission problem, statusCode: " + statusCode + ", status: " + response.toString());
            }
            CookieStore cookieStore = context.getCookieStore();
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie: cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals("SDMS_code"))
                    ctsAuthorization.code = cookie.getValue();
                else if (cookieName.equals("SDMS_user"))
                    ctsAuthorization.user = cookie.getValue();
            }
            if (ctsAuthorization.code == null || ctsAuthorization.user == null) {
                logger.error("Authorization: missing cookie, SDMS_code or SDMS_user could not be found");
                throw new IllegalArgumentException("Authorization: missing cookie, SDMS_code or SDMS_user could not be found");
            }
        } catch (IOException e) {
            logger.error("Authorization: IOException, URI: " + httpPost.getURI() + ", e: " + e);
            throw new IOException("Authorization: IOException, URI: " + httpPost.getURI() + ", e: " + e);
        } finally {
            response.close();
        }

        logger.debug("getCtsInfo: done");
        return ctsAuthorization;
    }

    /**
     * Class for transporting CTS credential parameters.
     */
    public class Credentials {
        public String username;
        public String password;
    }

    /**
     * Returns CTS login and CTS password.
     *
     * @return
     */
    private Credentials getCtsCredentials() throws IOException {
        Credentials credentials = new Credentials();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("cts_credentials.properties");
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            logger.error("Problem reading CTS credentials, e: " + e);
            throw new IOException("Problem reading CTS credentials, e: " + e);
        } finally {
            input.close();
        }

        credentials.username = properties.getProperty("cts.credentials.username");
        credentials.password = properties.getProperty("cts.credentials.password");

        return credentials;
    }
}
