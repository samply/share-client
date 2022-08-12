package de.samply.share.client.rest;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

import com.mchange.rmi.NotAuthorizedException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.feature.ClientFeature;
import de.samply.share.client.fhir.FhirParseException;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.connector.CtsConnector;
import de.samply.share.client.util.connector.exception.CtsConnectorException;
import de.samply.share.client.util.connector.exception.MainzellisteConnectorException;
import de.samply.share.client.util.connector.exception.XmlPareException;
import de.samply.share.client.util.db.UserUtil;
import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rest API endpoints.
 */
@Path("/")
public class Api {

  private static final Logger logger = LoggerFactory.getLogger(Api.class);

  /**
   * Send a patient bundle to the CTS.
   *
   * @param bundle      patient bundle as string
   * @param httpHeaders httpHeader with mediaType and basicAuth
   * @return if the POST to the CTS was successful
   */
  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/postcts")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "ok"),
      @APIResponse(responseCode = "400", description = "Bad Request"),
      @APIResponse(responseCode = "401", description = "Unauthorized"),
      @APIResponse(responseCode = "403", description = "Forbidden"),
      @APIResponse(responseCode = "404", description = "Not Found"),
      @APIResponse(responseCode = "500", description = "Internal Server Error")
  })
  @Operation(summary = "Send a patient bundle to the CTS")
  public Response postFhirToCts(String bundle, @Context HttpHeaders httpHeaders) {
    if (!ApplicationBean.getFeatureManager().getFeatureState(ClientFeature.NNGM_CTS).isEnabled()) {
      return Response.status(403).build();
    }
    try {
      if (!checkUser(httpHeaders.getRequestHeader(AUTHORIZATION).get(0))) {
        return Response.status(401).entity("Basic Auth credentials not correct").build();
      }
      MediaType mediaType = httpHeaders.getMediaType();
      CtsConnector ctsConnector = ApplicationBean.getCtsConnector();
      return ctsConnector.postFhirToCts(bundle, mediaType);
    } catch (FhirParseException | CtsConnectorException | MainzellisteConnectorException e) {
      return Response.status(400).entity(e.getMessage()).build();
    } catch (NotAuthorizedException e) {
      return Response.status(401).entity(e.getMessage()).build();
    } catch (NotFoundException e) {
      return Response.status(404).entity(e.getResponse().getEntity().toString()).build();
    } catch (IOException e) {
      return Response.status(500).entity(e.getMessage()).build();
    }
  }

  /**
   * Send a xml transaction to the CTS.
   *
   * @param xmlString      patient bundle as string
   * @param httpHeaders httpHeader with mediaType and basicAuth
   * @return if the POST to the CTS was successful
   */
  @POST
  @Consumes({MediaType.APPLICATION_XML})
  @Path("/postxml")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "ok"),
      @APIResponse(responseCode = "400", description = "Bad Request"),
      @APIResponse(responseCode = "401", description = "Unauthorized"),
      @APIResponse(responseCode = "403", description = "Forbidden"),
      @APIResponse(responseCode = "404", description = "Not Found"),
      @APIResponse(responseCode = "500", description = "Internal Server Error")
  })
  @Operation(summary = "Send a xml transaction to the CTS")
  public Response postXmlToCts(String xmlString, @Context HttpHeaders httpHeaders) {
    if (!ApplicationBean.getFeatureManager().getFeatureState(ClientFeature.NNGM_CTS).isEnabled()) {
      return Response.status(403).build();
    }
    try {
      if (!checkUser(httpHeaders.getRequestHeader(AUTHORIZATION).get(0))) {
        return Response.status(401).entity("Basic Auth credentials not correct").build();
      }
      CtsConnector ctsConnector = ApplicationBean.getCtsConnector();
      return ctsConnector.postXmlToCts(xmlString);
    } catch (XmlPareException | CtsConnectorException | MainzellisteConnectorException e) {
      return Response.status(400).entity(e.getMessage()).build();
    } catch (NotAuthorizedException e) {
      return Response.status(401).entity(e.getMessage()).build();
    } catch (NotFoundException e) {
      return Response.status(404).entity(e.getResponse().getEntity().toString()).build();
    } catch (IOException e) {
      return Response.status(500).entity(e.getMessage()).build();
    }
  }


  private boolean checkUser(String basicAuth) {
    String base64Credentials = basicAuth.substring("Basic".length()).trim();
    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    final String[] values = credentials.split(":", 2);
    logger.info("trying to access api with user: " + values[0]);
    User user = UserUtil.fetchUserByName(values[0]);
    if (user == null) {
      return false;
    }
    return BCrypt.checkpw(values[1], user.getPasswordHash());
  }

}
