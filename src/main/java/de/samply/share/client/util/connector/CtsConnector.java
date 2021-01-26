package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.parser.DataFormatException;
import com.google.gson.JsonObject;
import com.mchange.rmi.NotAuthorizedException;
import com.sun.jersey.api.NotFoundException;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FhirResource;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.http.Consts;
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
import org.hl7.fhir.r4.model.Bundle;

public class CtsConnector {

  private static final Logger logger = LogManager.getLogger(CtsConnector.class);
  private static final String CONTENT_TYPE_CTS_FHIR_JSON = "application/fhir+json; fhirVersion=4.0";
  private static final FhirResource fhirResource = new FhirResource();
  private transient HttpConnector httpConnector;
  private CloseableHttpClient httpClient;
  private String ctsBaseUrl;
  private HttpHost ctsHost;
  private String username;
  private String password;

  /**
   * Create a CtsConnector object.
   */
  public CtsConnector() {
    try {
      // Pull various pieces of information from the database and store them
      // in memory.
      ctsBaseUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_URL);
      httpConnector = ApplicationBean.createHttpConnector();
      ctsHost = SamplyShareUtils.getAsHttpHost(ctsBaseUrl);
      httpClient = httpConnector.getHttpClient(ctsHost);
      username = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_USERNAME);
      password = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_PASSWORD);
    } catch (MalformedURLException e) {
      logger.error("URL problem while initializing CTS uploader, e: " + e);
    }
  }

  /**
   * Takes a stringified FHIR Bundle, assumed to be containing identifying patient data (IDAT),
   * replaces the IDAT with a pseudonym, and then sends the pseudonymized bundle to the CTS data
   * upload endpoint.
   *
   * @param bundleString the patient bundle as String.
   * @throws IOException              IOException
   * @throws ConfigurationException   ConfigurationException
   * @throws DataFormatException      DataFormatException
   * @throws IllegalArgumentException IllegalArgumentException
   */
  public Response postPseudonmToCts(String bundleString, String mediaType)
      throws IOException, ConfigurationException, DataFormatException, IllegalArgumentException,
      NotFoundException, NotAuthorizedException {
    // Make a call to the PL, and replace patient identifying information in the
    // bundle with a pseudonym.
    Bundle pseudonymBundle = pseudonymiseBundle(bundleString, mediaType);
    // Serialize into a JSON String
    String pseudonymBundleJson = fhirResource.convertBundleToJson(pseudonymBundle);

    // Set up the API call
    HttpEntity entity = new StringEntity(pseudonymBundleJson, Consts.UTF_8);
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
   * @throws IllegalArgumentException IllegalArgumentException
   * @throws NotAuthorizedException   NotAuthorizedException
   */
  public Response postLocalPatientToCentralCts(String patient)
      throws IOException, IllegalArgumentException,
      NotFoundException, NotAuthorizedException {
    MainzellisteConnector mainzellisteConnector = ApplicationBean.getMainzellisteConnector();
    JsonObject pseudonimisedPatient = mainzellisteConnector.getPatientWithPseudonymId(patient);
    // Set up the API call
    HttpEntity entity = new StringEntity(pseudonimisedPatient.toString(), Consts.UTF_8);
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
   * Create a BasicHttpContext for CTS upload, with the cookies needed for authorization.
   *
   * @return
   */
  private HttpContext createCtsContext() throws IOException {
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
  private Bundle pseudonymiseBundle(String bundleString, String mediaType)
      throws IOException, ConfigurationException, DataFormatException, NotFoundException,
      NotAuthorizedException {
    Bundle bundle = fhirResource.convertToBundleResource(bundleString, mediaType);
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
  private CtsAuthorization getCtsAuthorization() throws IOException, IllegalArgumentException {
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
    } finally {
      closeResponse(response);
    }

    logger.debug("getCtsInfo: done");
    return ctsAuthorization;
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
