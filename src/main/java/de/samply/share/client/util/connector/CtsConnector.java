package de.samply.share.client.util.connector;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.util.ResourceReferenceInfo;
import com.mchange.rmi.NotAuthorizedException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.crypt.Crypt;
import de.samply.share.client.feature.ClientFeature;
import de.samply.share.client.fhir.FhirParseException;
import de.samply.share.client.fhir.FhirUtil;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.connector.exception.CtsConnectorException;
import de.samply.share.client.util.connector.exception.MainzellisteConnectorException;
import de.samply.share.client.util.connector.exception.XmlPareException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.xml.XmlUtils;
import de.samply.share.common.utils.SamplyShareUtils;
import jakarta.ws.rs.NotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.UriType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


/**
 * A connector that handles all communication with the EDC system.
 */
public class CtsConnector {

  private static final Logger logger = LoggerFactory.getLogger(CtsConnector.class);
  private static final String CONTENT_TYPE_CTS_FHIR_JSON = "application/fhir+json";

  private final FhirUtil fhirUtil;
  private final XmlUtils xmlUtils;
  private final CloseableHttpClient httpClient;
  private final String ctsBaseUrl;
  private final HttpHost ctsHost;
  private final String apiKey;
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
      apiKey = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CTS_APIKEY);
    } catch (MalformedURLException e) {
      logger.error("URL problem while initializing CTS uploader, e: " + e);
      throw e;
    }
    fhirContext = FhirContext.forR4();
    fhirUtil = new FhirUtil(fhirContext);
    xmlUtils = new XmlUtils();
  }

  /**
   * Takes a stringified FHIR Bundle, assumed to be containing identifying patient data (IDAT),
   * replaces the IDAT with a pseudonym, and then sends the pseudonymized bundle to the CTS data
   * upload endpoint.
   *
   * @param bundleString the patient bundle as String.
   * @throws IOException                    IOException
   * @throws NotFoundException              NotFoundException
   * @throws NotAuthorizedException         NotAuthorizedException
   * @throws MainzellisteConnectorException MainzellisteConnectorException
   * @throws CtsConnectorException          CtsConnectorException
   */
  public Response postFhirToCts(String bundleString, MediaType mediaType)
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
    httpPost.setHeader(HttpHeaders.AUTHORIZATION, apiKey);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      String message =
          "CTS server response: statusCode:" + statusCode + "; response: " + response.toString();
      String responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      if (responseBody != null && !responseBody.isEmpty()) {
        message += ";body: " + responseBody;
      }
      return Response.status(statusCode).entity(message).build();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
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
    pseudonymizedBundle = mainzellisteConnector.getPatientFhirPseudonym(bundle);
    return pseudonymizedBundle;
  }

  /**
   * Pseudonymize any patient data in the bundle.
   *
   * @param xmlString the patient bundle which should be pseudonimised
   * @return the pseudonimised xml
   * @throws IOException            IOException
   * @throws ConfigurationException ConfigurationException
   * @throws DataFormatException    DataFormatException
   */
  private Document pseudonymiseXml(String xmlString)
      throws IOException, NotFoundException,
      NotAuthorizedException, MainzellisteConnectorException, XmlPareException {
    byte[] bytes = xmlString.getBytes(StandardCharsets.UTF_8);
    InputStream xmlInputStream = new ByteArrayInputStream(bytes);

    Document xmlDocument;
    xmlDocument = xmlUtils.domBuilder(xmlInputStream);
    MainzellisteConnector mainzellisteConnector = ApplicationBean.getMainzellisteConnector();
    Document pseudonymizedXml = null;
    pseudonymizedXml = mainzellisteConnector.getPatientXmlPseudonym(xmlDocument);
    return pseudonymizedXml;
  }

  /**
   * Search for the resource ids inside the bundle and encrypt or decrypt it.
   *
   * @param bundle  the patient bundle
   * @param encrypt if the ids should encrypted or decrypted
   * @return the encrypted/decrypted bundle
   * @throws CtsConnectorException CtsConnectorException
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
   * Takes a stringified XML file, assumed to be containing identifying patient data (IDAT),
   * replaces the IDAT with a pseudonym, and then sends the pseudonymized xml to the CTS data upload
   * endpoint.
   *
   * @param xmlString the patient bundle as String.
   * @throws IOException                    IOException
   * @throws NotFoundException              NotFoundException
   * @throws NotAuthorizedException         NotAuthorizedException
   * @throws MainzellisteConnectorException MainzellisteConnectorException
   * @throws CtsConnectorException          CtsConnectorException
   */
  public Response postXmlToCts(String xmlString)
      throws IOException,
      NotFoundException, NotAuthorizedException, XmlPareException,
      MainzellisteConnectorException, CtsConnectorException {
    Document pseudonymXmlDocument = pseudonymiseXml(xmlString);
    String pseudonymXmlAsString = xmlUtils.xmlDocToString(pseudonymXmlDocument);
    HttpEntity entity = new StringEntity(pseudonymXmlAsString, Consts.UTF_8);
    HttpPost httpPost = new HttpPost(ctsBaseUrl);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
    httpPost.setHeader(HttpHeaders.AUTHORIZATION, apiKey);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      String message =
          "CTS server response: statusCode:" + statusCode + "; response: " + response;
      String responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
      if (responseBody != null && !responseBody.isEmpty()) {
        message += ";body: " + responseBody;
      }
      return Response.status(statusCode).entity(message).build();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new IOException(e);
    } finally {
      closeResponse(response);
    }
  }

}
