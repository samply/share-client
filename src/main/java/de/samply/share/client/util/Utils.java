package de.samply.share.client.util;

import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import de.samply.config.util.FileFinderUtil;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.enums.AuthSchemeType;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static org.omnifaces.util.Faces.getServletContext;

/**
 * A collection of utility methods
 */
public final class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static PublicKey mdsDbPubKey;

    /**
     * Avoid instantiation
     */
    private Utils() {
    }

    /**
     * Prepare the credentials provider by getting all credentials from the database and adding them accordingly.
     *
     * @return the properly filled credentials provider
     */
    public static CredentialsProvider prepareCredentialsProvider() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        for (Credentials credentials : CredentialsUtil.fetchCredentials()) {
            try {
                addCredentials(credentials, credentialsProvider);
            } catch (MalformedURLException e) {
                logger.warn("Caught exception while trying to add credentials: ", e);
            }
        }
        return credentialsProvider;
    }

    /**
     * Adds one set of credentials to the credentials provider.
     *
     * @param credentials the credentials to add
     * @param credentialsProvider the credentials provider to which the credentials will be added
     */
    private static void addCredentials(Credentials credentials, CredentialsProvider credentialsProvider) throws MalformedURLException {
        AuthScope authScope;
        AuthScope authScopeAlt;
        String authSchemeName;
        org.apache.http.auth.Credentials apacheCredentials = null;

        AuthSchemeType authScheme = credentials.getAuthScheme();
        switch (authScheme) {
            case AS_DIGEST:
                authSchemeName = AuthSchemes.DIGEST;
                apacheCredentials = new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPasscode());
                break;
            case AS_KERBEROS:
                authSchemeName = AuthSchemes.KERBEROS;
                logger.fatal("tried to add kerberos credentials. Currently unsupported.");
                return;
//            apacheCredentials = new KerberosCredentials(gssCredential);
//            break;
            case AS_NTLM:
                authSchemeName = AuthSchemes.NTLM;
                apacheCredentials = new NTCredentials(credentials.getUsername(), credentials.getPasscode(), credentials.getWorkstation(), credentials.getDomain());
                break;
            case AS_SPNEGO:
                authSchemeName = AuthSchemes.SPNEGO;
                logger.fatal("tried to add kerberos credentials. Currently unsupported.");
                return;
//          apacheCredentials = new KerberosCredentials(gssCredential);
//            break;
            case AS_BASIC:
                authSchemeName = AuthSchemes.BASIC;
                apacheCredentials = new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPasscode());
                break;
            default:
                logger.info("AuthScheme " + authScheme + ". Don't add to credentials provider.");
                authSchemeName = null;
                break;
        }

        if (authSchemeName == null || apacheCredentials == null) {
            return;
        }


        switch (credentials.getTarget()) {
            case TT_HTTP_PROXY:
                String httpProxyHostString;
                try {
                    httpProxyHostString = ApplicationBean.getConfiguration().getProxy().getHTTP().getUrl().getHost();
                } catch (NullPointerException npe) {
                    httpProxyHostString = null;
                }
                if (SamplyShareUtils.isNullOrEmpty(httpProxyHostString)) {
                    logger.warn("Attempted to add credentials for nonexistent http proxy. Skipping.");
                    return;
                }
                int httpProxyPort = ApplicationBean.getConfiguration().getProxy().getHTTP().getUrl().getPort();
                if (httpProxyPort < 0) {
                    logger.debug("HTTP Proxy port apparently not set. Use default (80)");
                    httpProxyPort = 80;
                }
                authScope = new AuthScope(httpProxyHostString, httpProxyPort, AuthScope.ANY_REALM, authSchemeName);
                break;
            case TT_HTTPS_PROXY:
                String httpsProxyHostString;
                try {
                    httpsProxyHostString = ApplicationBean.getConfiguration().getProxy().getHTTPS().getUrl().getHost();
                } catch (NullPointerException npe) {
                    httpsProxyHostString = null;
                }
                if (SamplyShareUtils.isNullOrEmpty(httpsProxyHostString)) {
                    logger.warn("Attempted to add credentials for nonexistent https proxy. Skipping.");
                    return;
                }
                int httpsProxyPort = ApplicationBean.getConfiguration().getProxy().getHTTPS().getUrl().getPort();
                if (httpsProxyPort < 0) {
                    logger.debug("HTTPS Proxy port apparently not set. Use default (443)");
                    httpsProxyPort = 443;
                }
                authScope = new AuthScope(httpsProxyHostString, httpsProxyPort, AuthScope.ANY_REALM, authSchemeName);
                break;

            case TT_LDM:
                String ldmString = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.LDM_URL);
                URL ldmURL = null;
                int ldmPort;

                try {
                    ldmURL = new URL(ldmString);
                    ldmPort = ldmURL.getPort() < 0  ? ldmURL.getDefaultPort() : ldmURL.getPort();
                } catch (MalformedURLException e) {
                    logger.warn("LDM URL is malformed! Credentials won't be added to credentialsprovider");
                    ldmPort = 443;
                }
                if (ldmURL != null) {
                    // Add credentials for the host with and without "www." prefix
                    authScope = new AuthScope(ldmURL.getHost(), ldmPort, AuthScope.ANY_REALM, authSchemeName);
                    if (ldmURL.getHost() != null && ldmURL.getHost().startsWith("www.")) {
                        authScopeAlt = new AuthScope(ldmURL.getHost().substring(4), ldmPort, AuthScope.ANY_REALM, authSchemeName);
                        credentialsProvider.setCredentials(authScopeAlt, apacheCredentials);
                    } else {
                        authScopeAlt = new AuthScope("www." + ldmURL.getHost(), ldmPort, AuthScope.ANY_REALM, authSchemeName);
                        credentialsProvider.setCredentials(authScopeAlt, apacheCredentials);
                    }
                } else {
                    authScope = null;
                }
                break;
            case TT_CENTRALSEARCH:
            default:
                URL centralSearchURL = getCentralMdsDbURL();

                // Add credentials for the host with and without "www." prefix
                authScope = new AuthScope(centralSearchURL.getHost(), centralSearchURL.getPort(), AuthScope.ANY_REALM, authSchemeName);
                if (centralSearchURL.getHost() != null && centralSearchURL.getHost().startsWith("www.")) {
                    authScopeAlt = new AuthScope(centralSearchURL.getHost().substring(4), centralSearchURL.getPort(), AuthScope.ANY_REALM, authSchemeName);
                    credentialsProvider.setCredentials(authScopeAlt, apacheCredentials);
                } else {
                    authScopeAlt = new AuthScope("www." + centralSearchURL.getHost(), centralSearchURL.getPort(), AuthScope.ANY_REALM, authSchemeName);
                    credentialsProvider.setCredentials(authScopeAlt, apacheCredentials);
                }
                break;
        }

        if (authScope != null) {
            credentialsProvider.setCredentials(authScope, apacheCredentials);
        }
    }

    /**
     * Get the composite URL (base url + path) for the central MDS database
     * @return the URL for the central MDS database
     */
    public static URL getCentralMdsDbURL() throws MalformedURLException {
        String baseUrlString = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CENTRAL_MDS_DATABASE_BASE_URL);
        String path = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CENTRAL_MDS_DATABASE_PATH);
        URL baseUrl = SamplyShareUtils.stringToURL(baseUrlString);
        return SamplyShareUtils.fixURL(new URL(baseUrl, path));
    }

    /**
     * Read all bytes of a file
     *
     * @param filename the filename
     * @return the byte array
     */
    private static byte[] readFileBytes(String filename) throws IOException {
        File file = FileFinderUtil.findFile(ProjectInfo.INSTANCE.getProjectName().toLowerCase() + filename, ProjectInfo.INSTANCE.getProjectName().toLowerCase(), System.getProperty("catalina.base") + File.separator + "conf", getServletContext().getRealPath("/WEB-INF"));
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Read public key.
     *
     * @param filename the filename
     * @return the public key
     */
    private static PublicKey readPublicKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(readFileBytes(filename));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicSpec);
    }

    /**
     * Generates a random export id
     * base64 encoded, url safe, encrypted with mds-pubkey
     *
     * @param prefix
     *          the prefix to use for the anonymized id
     * @param filename
     *          the filename of the public key to use for encryption
     *
     * @return the export id
     */
    public static String getRandomExportid(String prefix, String filename) {
        try {
            if (mdsDbPubKey == null) {
                mdsDbPubKey = Utils.readPublicKey(filename);
            }
            String randomizedId = prefix + UUID.randomUUID().toString();
            byte[] message = randomizedId.getBytes("UTF8");
            byte[] secret = SamplyShareUtils.encrypt(mdsDbPubKey, message);

            return BaseEncoding.base64Url().encode(secret);
        } catch (Exception e) {
            logger.error("Could not encrypt random export id: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert date for communication with the central mds db.
     *
     * @param date
     *            the date
     * @return the string
     */
    public static String convertDate(Date date) {
        DateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }
    public static String convertDate2(Date date) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }
    public static Date convertDate2 (String date) throws ParseException {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX", Locale.ENGLISH);
        return simpleDateFormat.parse(date);
    }

    /**
     * Convert date for communication with the central mds db.
     *
     * @param date
     *            the date
     * @return the string
     */
    public static String convertDate3(Date date) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static Date convertDate3 (String date) throws ParseException {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        return simpleDateFormat.parse(date);
    }


    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTime(date);
        return cal;
    }

    /**
     * Save a file part to a temporary file
     *
     * @param prefix the prefix of the temp file
     * @param part the file part to save
     * @return the resulting temp file
     */
    public static File savePartToTmpFile(String prefix, Part part) throws IOException {
        File file = Files.createTempFile(prefix, getFileName(part)).toFile();
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return file;
    }

    /**
     * Gets the file name of a file part
     *
     * @param filePart the file part
     * @return the file name
     */
    public static String getFileName(Part filePart) {
        String header = filePart.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        for (String headerPart : header.split(";")) {
            if (headerPart.trim().startsWith("filename")) {
                String filepath = headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
                // in case of IE, the part name is the full path of the file
                return FilenameUtils.getName(filepath);
            }

        }
        return null;
    }

    public static void setStatus (InquiryDetails inquiryDetails, InquiryStatusType inquiryStatusType){

        inquiryDetails.setStatus(inquiryStatusType);
        logger.debug ("Change in Inquiry Status ("+inquiryDetails.getInquiryId()+" ): " + inquiryDetails.getStatus());

    }

    public static Long getAsLong (String number){

        try{
            return new Long(number);
        } catch (Exception e){
            return null;
        }

    }

}
