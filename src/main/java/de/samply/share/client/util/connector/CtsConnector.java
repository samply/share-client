package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.util.ResourceReferenceInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.mchange.rmi.NotAuthorizedException;
import com.sun.jersey.api.NotFoundException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.crypt.Crypt;
import de.samply.share.client.feature.ClientFeature;
import de.samply.share.client.fhir.FhirParseException;
import de.samply.share.client.fhir.FhirUtil;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.connector.exception.ConflictException;
import de.samply.share.client.util.connector.exception.CtsConnectorException;
import de.samply.share.client.util.connector.exception.MainzellisteConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.UriType;

/**
 * A connector that handles all communication with the EDC system.
 */
public class CtsConnector {

  private static final Logger logger = LogManager.getLogger(CtsConnector.class);
  private static final String CONTENT_TYPE_CTS_FHIR_JSON = "application/fhir+json; fhirVersion=4.0";
  private static final String X_BK_PSEUDONYM_JSONPATHS = "X-BK-pseudonym-jsonpaths";
  private static final String X_BK_TARGET_URL = "X-BK-target-url";

  private final FhirUtil fhirUtil;
  private final CloseableHttpClient httpClient;
  private final String ctsBaseUrl;
  private final HttpHost ctsHost;
  private final String username;
  private final String password;
  private final FhirContext fhirContext;

  /**
   * Create a CtsConnector object.
   */
  public CtsConnector() throws MalformedURLException {
    try {
      // Pull various pieces of information from the database and store them
      // in memory.
      ctsBaseUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL);
      ctsHost = SamplyShareUtils.getAsHttpHost(ctsBaseUrl);
      httpClient = ApplicationBean.createHttpConnector().getHttpClient(ctsHost);
      username = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_USERNAME);
      password = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PASSWORD);
    } catch (MalformedURLException e) {
      logger.error("URL problem while initializing CTS uploader, e: " + e);
      throw e;
    }
    fhirContext = FhirContext.forR4();
    fhirUtil = new FhirUtil(fhirContext);
  }

  /**
   * Takes a stringified FHIR Bundle, assumed to be containing identifying patient data (IDAT),
   * replaces the IDAT with a pseudonym, and then sends the pseudonymized bundle to the CTS data
   * upload endpoint.
   *
   * @param bundleString the patient bundle as String.
   * @throws IOException              IOException
   * @throws NotFoundException        NotFoundException
   * @throws NotAuthorizedException   NotAuthorizedException
   * @throws MainzellisteConnectorException MainzellisteConnectorException
   * @throws CtsConnectorException CtsConnectorException
   */
  public Response postPseudonmToCts(String bundleString, MediaType mediaType)
      throws IOException,
      NotFoundException, NotAuthorizedException, FhirParseException,
      MainzellisteConnectorException, CtsConnectorException {
    // Make a call to the PL, and replace patient identifying information in the
    // bundle with a pseudonym.
    Bundle pseudonymBundle = pseudonymiseBundle(bundleString, mediaType);
    // Serialize into a JSON String
    String pseudonymBundleAsString;
    if (ApplicationBean.getFeatureManager().getFeatureState(ClientFeature.NNGM_ENCRYPT_ID)
        .isEnabled()) {
      pseudonymBundleAsString = cryptIds(pseudonymBundle, true);
    } else {
      pseudonymBundleAsString = fhirUtil.encodeResourceToJson(pseudonymBundle);
    }

    // Set up the API call
    HttpEntity entity = new StringEntity(pseudonymBundleAsString, Consts.UTF_8);
    HttpPost httpPost = new HttpPost(ctsBaseUrl);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_CTS_FHIR_JSON);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      HttpContext ctsContext = createCtsContext();
      response = httpClient.execute(httpPost, ctsContext);
      int statusCode = response.getStatusLine().getStatusCode();
      String message =
          "CTS server response: statusCode:" + statusCode + "; response: " + response.toString();
      String responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      if (responseBody != null && !responseBody.isEmpty()) {
        message += ";body: " + responseBody;
      }
      return Response.status(statusCode).entity(message).build();
    } catch (IOException e) {
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
  }

  /**
   * Post a local CTS patient to the central CTS.
   *
   * @param patient the local patient
   * @return if the post was successfull
   * @throws IOException              IOException
   * @throws NotFoundException        NotFoundException
   * @throws NotAuthorizedException   NotAuthorizedException
   * @throws MainzellisteConnectorException   MainzellisteConnectorException
   */
  public Response postLocalPatientToCentralCts(String patient)
          throws IOException, NotFoundException, NotAuthorizedException,
          MainzellisteConnectorException, CtsConnectorException {
    MainzellisteConnector mainzellisteConnector = ApplicationBean.getMainzellisteConnector();
    JsonObject pseudonimisedPatient = mainzellisteConnector.requestEncryptedIdForPatient(patient);
    // Set up the API call
    HttpEntity entity = new StringEntity(pseudonimisedPatient.toString(), Consts.UTF_8);
    HttpPost httpPost = new HttpPost(ctsBaseUrl);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      HttpContext ctsContext = createCtsContext();
      response = httpClient.execute(httpPost, ctsContext);
      int statusCode = response.getStatusLine().getStatusCode();
      String message =
          "CTS server response: statusCode:" + statusCode + "; response: " + response.toString();
      String responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      if (responseBody != null && !responseBody.isEmpty()) {
        message += ";body: " + responseBody;
      }
      return Response.status(statusCode).entity(message).build();
    } catch (IOException e) {
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
  }


  /**
   * Post a local CTS patient to the central CTS.
   *
   * @param patient         the local patient
   * @param headerMapToSend the headers from the incoming request which should be send
   * @return if the post was successfull
   * @throws IOException              IOException
   * @throws CtsConnectorException CtsConnectorException
   * @throws NotAuthorizedException   NotAuthorizedException
   */
  public Response postLocalPatientToCentralCts(String patient,
      javax.ws.rs.core.HttpHeaders httpHeaders,
      HashMap<String, Object> headerMapToSend)
      throws IOException,
      NotFoundException, NotAuthorizedException, CtsConnectorException {
    List<String> urlTargetHeaders = httpHeaders.getRequestHeader(X_BK_TARGET_URL);
    String encryptedIds = patient; //or a empty string
    String urlTarget;
    if (urlTargetHeaders != null && !urlTargetHeaders.isEmpty()) {
      urlTarget = urlTargetHeaders.get(0);
    } else {
      logger.error("PostLocalPatientToCentralCts: X-BK-target-url is empty");
      return Response.status(400).entity("X-BK-target-url is empty").build();
    }
    List<String> jsonpathsHeaders = httpHeaders.getRequestHeader(X_BK_PSEUDONYM_JSONPATHS);
    if (jsonpathsHeaders != null && !jsonpathsHeaders.isEmpty()) {
      encryptedIds = readIds(patient, jsonpathsHeaders.get(0), false);
    }
    // Set up the API call
    HttpEntity entity = new StringEntity(encryptedIds, Consts.UTF_8);
    HttpPost httpPost = new HttpPost(urlTarget);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    for (Entry<String, Object> entry : headerMapToSend.entrySet()) {
      httpPost.setHeader(entry.getKey(), entry.getValue().toString());
    }
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200 || statusCode == 201) {
        String responseAsString = EntityUtils.toString(response.getEntity());
        Header responseJsonpaths = response.getFirstHeader(X_BK_PSEUDONYM_JSONPATHS);
        if (responseJsonpaths != null) {
          responseAsString = readIds(responseAsString, responseJsonpaths.getValue(), true);
        }
        return getResponse(response, statusCode, responseAsString);
      }
      String message =
          "CTS server response: statusCode:" + statusCode + "; response: " + response.toString();
      String responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      if (responseBody != null && !responseBody.isEmpty()) {
        message += ";body: " + responseBody;
      }
      logger.error("PostLocalPatientToCentralCts response: " + message);
      return Response.status(statusCode).entity(message).build();
    } catch (IOException e) {
      throw new IOException(e);
    } catch (StringIndexOutOfBoundsException | PathNotFoundException e) {
      throw new CtsConnectorException(e.getMessage());
    } finally {
      closeResponse(response);
    }
  }

  /**
   * build a response from the a original response, headers and status code.
   *
   * @param response CloseableHttpResponse
   * @param statusCode status code from the response
   * @param responseAsString response body
   * @return http response
   */
  private Response getResponse(CloseableHttpResponse response, int statusCode,
                               String responseAsString) {
    Response.ResponseBuilder builder = Response.status(statusCode).entity(responseAsString);
    //headers
    Header[] responseHeaders = response.getAllHeaders();
    for (Header header : headersToFilter(responseHeaders)) {
      builder.header(header.getName(), header.getValue());
    }
    return builder.build();
  }

  /**
   * Create a BasicHttpContext for CTS upload, with the cookies needed for authorization.
   *
   * @return HttpContext.
   * @throws IOException IOException
   */
  private HttpContext createCtsContext() throws IOException, CtsConnectorException,
          NotAuthorizedException {
    CtsAuthorization ctsAuthorization = getCtsAuthorization();

    BasicCookieStore cookieStore = new BasicCookieStore();
    // Recycle the authorization cookies that we received from the CTS
    cookieStore.addCookie(ctsAuthorization.codeCookie);
    cookieStore.addCookie(ctsAuthorization.userCookie);
    HttpContext ctsContext = new BasicHttpContext();
    ctsContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

    return ctsContext;
  }

  /**
   * Pseudonymise any patient data in the bundle.
   *
   * @param bundleString the patient bundle which should be pseudonimised
   * @param mediaType    mediaType of the bundle (JSON or XML)
   * @return the pseudonimised bundle
   * @throws IOException            IOException
   * @throws ConfigurationException ConfigurationException
   * @throws DataFormatException    DataFormatException
   */
  private Bundle pseudonymiseBundle(String bundleString, MediaType mediaType)
      throws IOException, NotFoundException,
      NotAuthorizedException, FhirParseException, MainzellisteConnectorException {
    Bundle bundle = fhirUtil.parseBundleResource(bundleString, mediaType);
    MainzellisteConnector mainzellisteConnector = ApplicationBean.getMainzellisteConnector();
    Bundle pseudonymizedBundle = null;
    pseudonymizedBundle = mainzellisteConnector.getPatientPseudonym(bundle);
    return pseudonymizedBundle;
  }


  /**
   * Returns CTS code and CTS user.
   *
   * @return CtsAuthorization
   */
  private CtsAuthorization getCtsAuthorization() throws IOException, CtsConnectorException,
          NotAuthorizedException {
    logger.debug("getCtsInfo: entered");

    // Build a form-based entity to realize the login
    List<NameValuePair> formElements = new ArrayList<NameValuePair>();
    formElements.add(new BasicNameValuePair("username", username));
    formElements.add(new BasicNameValuePair("password", password));
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
      StatusLine statusLine = response.getStatusLine();
      String reasonPhrase = statusLine.getReasonPhrase();
      logger.info("CTS authorization status code: " + statusCode);
      if (statusCode >= 500 && statusCode < 600) {
        String bodyResponse = EntityUtils.toString(response.getEntity());
        logger.error(
            getMessage("Authorization: CTS server error", statusCode, reasonPhrase, bodyResponse));
        throw new IOException(
            getMessage("Authorization: CTS server error", statusCode, reasonPhrase, bodyResponse));
      }
      if (statusCode == 401) {
        String bodyResponse = EntityUtils.toString(response.getEntity());
        logger.error(getMessage("Authorization: CTS permission problem", statusCode, reasonPhrase,
                bodyResponse));
        throw new NotAuthorizedException(
                getMessage("Authorization: CTS permission problem", statusCode, reasonPhrase,
                        bodyResponse));
      }
      if (statusCode >= 400 && statusCode < 500) {
        String bodyResponse = EntityUtils.toString(response.getEntity());
        logger.error(getMessage("Authorization: CTS permission problem", statusCode, reasonPhrase,
            bodyResponse));
        throw new IllegalArgumentException(
            getMessage("Authorization: CTS permission problem", statusCode, reasonPhrase,
                bodyResponse));
      }
      CookieStore cookieStore = context.getCookieStore();
      List<Cookie> cookies = cookieStore.getCookies();
      for (Cookie cookie : cookies) {
        String cookieName = cookie.getName();
        if (cookieName.equals("SDMS_code")) {
          ctsAuthorization.codeCookie = cookie;
        } else if (cookieName.equals("SDMS_user")) {
          ctsAuthorization.userCookie = cookie;
        }
      }
      if (ctsAuthorization.codeCookie == null || ctsAuthorization.userCookie == null) {
        logger.error("Authorization: missing cookie, SDMS_code or SDMS_user could not be found");
        throw new IllegalArgumentException(
            "Authorization: missing cookie, SDMS_code or SDMS_user could not be found");
      }
    } catch (IOException e) {
      logger.error("Authorization: IOException, URI: " + httpPost.getURI() + ", e: " + e);
      throw new IOException("Authorization: IOException, URI: " + httpPost.getURI() + ", e: " + e);
    } catch (IllegalArgumentException e) {
      throw new CtsConnectorException(e.getMessage());
    } finally {
      closeResponse(response);
    }

    logger.debug("getCtsInfo: done");
    return ctsAuthorization;
  }

  private String readIds(String json, String headerIdKey, boolean response)
      throws IOException, NotAuthorizedException, StringIndexOutOfBoundsException,
      PathNotFoundException, CtsConnectorException {
    String headerIdKeyString = new String(Base64.getDecoder().decode(headerIdKey));
    String patientJson = null;
    try {
      headerIdKeyString = headerIdKeyString.substring(headerIdKeyString.indexOf("$"),
          headerIdKeyString.indexOf("\"]"));
      patientJson = json;
      Configuration conf = Configuration.defaultConfiguration()
          .addOptions(Option.ALWAYS_RETURN_LIST);
      List<String> ids = JsonPath.using(conf).parse(patientJson).read(headerIdKeyString);
      patientJson = replaceIdsWithEncryptedIds(patientJson, ids, response);

    } catch (StringIndexOutOfBoundsException e) {
      logger.error("jsonpath does not match the expected format ($ and [] are expected)", e);
      throw new StringIndexOutOfBoundsException(
          "Jsonpath does not match the expected format ($ and [] are expected):  " + e);
    } catch (PathNotFoundException e) {
      logger.error("could not found isonpath matches", e);
      throw new PathNotFoundException("could not find jsonpath matches:  " + e);
    } catch (IllegalArgumentException | ConflictException e) {
      throw new CtsConnectorException(e);
    }

    return patientJson;
  }

  private String replaceIdsWithEncryptedIds(String patientJson, List<String> ids, boolean response)
          throws IOException, NotAuthorizedException, ConflictException {
    MainzellisteConnector mainzellisteConnector = ApplicationBean.getMainzellisteConnector();
    if (!response) {
      for (String id : ids) {

        patientJson = patientJson
            .replace(id, mainzellisteConnector.requestEncryptedIdWithPatientId(id));
      }
    } else {
      JsonArray localIds = mainzellisteConnector.getLocalId(ids);
      for (int i = 0; i < localIds.size(); i++) {
        patientJson = patientJson.replace(ids.get(i),
            localIds.get(i).getAsJsonObject().get("ids").getAsJsonArray().get(0).getAsJsonObject()
                .get("idString").getAsString());
      }
    }
    return patientJson;
  }

  /**
   * Search for the resource ids inside the bundle and encrypt or decrypt it.
   *
   * @param bundle  the patient bundle
   * @param encrypt if the ids should encrypted or decrypted
   * @return the encrypted/decrypted bundle
   * @throws GeneralSecurityException GeneralSecurityException
   */
  private String cryptIds(Bundle bundle, boolean encrypt) throws CtsConnectorException {
    Crypt crypt = ApplicationBean.getCrypt();
    List<Bundle.BundleEntryComponent> bundleEntryComponentList = bundle.getEntry();
    try {
      for (int i = 0; i < bundleEntryComponentList.size(); i++) {
        Bundle.BundleEntryComponent bundleEntryComponent = bundleEntryComponentList.get(i);
        if (!bundleEntryComponent.hasResource() || !bundleEntryComponent.getResource().hasId()) {
          logger.error("bundle entry without an id: entry-index " + i + 1);
        }
        IIdType bundleIdType = bundleEntryComponent.getResource().getIdElement();
        bundleIdType = getEncryptedId(bundleIdType, encrypt, crypt);
        bundleEntryComponent.getResource().setId(bundleIdType.getValue());
        if (bundleEntryComponent.hasFullUrl()) {
          UriType fullUrl = bundleEntryComponent.getFullUrlElement();
          IdType fullUrlType = new IdType(fullUrl);
          IIdType fullUrlIidType = getEncryptedId(fullUrlType, encrypt, crypt);
          bundleEntryComponent.setFullUrl(fullUrlIidType.getValue());
        }
        if (bundleEntryComponent.hasRequest() && bundleEntryComponent.getRequest().hasUrl()) {
          UriType urlUriType = bundleEntryComponent.getRequest().getUrlElement();
          IdType urlIdType = new IdType(urlUriType);
          IIdType urlIidType = getEncryptedId(urlIdType, encrypt, crypt);
          bundleEntryComponent.getRequest().setUrl(urlIidType.getValue());
        }
        List<ResourceReferenceInfo> resourceReferenceInfoList = fhirContext.newTerser()
            .getAllResourceReferences(bundleEntryComponent.getResource());
        for (ResourceReferenceInfo resourceReferenceInfo : resourceReferenceInfoList) {
          IIdType idType = resourceReferenceInfo.getResourceReference().getReferenceElement();
          idType = getEncryptedId(idType, encrypt, crypt);
          // TODO: Validate FHIR URL?
          resourceReferenceInfo.getResourceReference().setReference(idType.getValue());
        }
        bundleEntryComponentList.set(i, bundleEntryComponent);
      }
      bundle.setEntry(bundleEntryComponentList);
    } catch (GeneralSecurityException e) {
      throw new CtsConnectorException(e);
    }
    return fhirUtil.encodeResourceToJson(bundle);
  }

  /**
   * decrypt and encrypt Ids, fullUrls, URLs and URNs.
   *
   * @param idType  idType to encrypt or decrypt
   * @param encrypt encrypt or decrypt
   * @param crypt   Crypt
   * @return encrypted or decrypted id
   * @throws GeneralSecurityException GeneralSecurityException
   */
  public static IIdType getEncryptedId(IIdType idType, boolean encrypt, Crypt crypt)
          throws GeneralSecurityException {
    String id = idType.getIdPart();
    if (id == null || "".equals(id)) {
      logger.error("Reference or URL does not contain an ID of a resource " + idType.getValue());
    } else {
      if (encrypt) {
        id = crypt.encrypt(id);
      } else {
        id = crypt.decrypt(id);
      }
      String baseUrl = idType.getBaseUrl();
      String resourceTyp = idType.getResourceType();
      String versionIdPart = idType.getVersionIdPart();
      idType.setParts(baseUrl, resourceTyp, id, versionIdPart);
    }
    return idType;
  }

  /**
   * Print the message from the extern service.
   *
   * @param message      the message from the extern service
   * @param statusCode   the statusCode from the extern service
   * @param reasonPhrase the reasonPhrase from the extern service
   * @param bodyResponse the bodyResponse from the extern service
   * @return convert all the information to a String
   */
  private String getMessage(String message, int statusCode, String reasonPhrase,
      String bodyResponse) {
    return message + "; statusCode: " + statusCode + "; reason: " + reasonPhrase + ";body: "
        + bodyResponse;
  }

  /**
   * Filter Headers to forward to local CTS.
   *
   * @param headers headers from response
   * @return all headers with defined suffixes
   */
  private Header[] headersToFilter(Header[] headers) {
    String[] headersToPropagate = {"x-cds-", "x-bk-"};
    List<Header> headersToSend = new ArrayList<>();
    for (Header header : headers) {
      for (String headerToPropagate : headersToPropagate) {
        if (header.getName().toLowerCase().startsWith(headerToPropagate)) {
          headersToSend.add(header);
        }
      }
    }
    return headersToSend.toArray(new Header[headersToSend.size()]);
  }

  /**
   * Close a response.
   *
   * @param response CloseableHttpResponse
   */
  public void closeResponse(CloseableHttpResponse response) {
    if (response != null) {
      try {
        EntityUtils.consumeQuietly(response.getEntity());
        response.close();
      } catch (IOException e) {
        logger.error("Get Pseudonym from Mainzelliste: Exception when closing response", e);
      }
    }
  }

  /**
   * Class for transporting CTS-authorization parameters.
   */
  public class CtsAuthorization {
    Cookie codeCookie;
    Cookie userCookie;
  }
}
