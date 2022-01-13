package de.samply.share.client.rest;

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

//TODO: Change class name
@Path("/")
public class Api2 {

  /**
   * Forward data to other connector.
   *
   * @param connectorUrl      data to be forwarded.
   * @param data      data to be forwarded.
   * @param httpHeaders httpHeader with mediaType and basicAuth
   * @return if the POST was successful
   */
  @POST
  //JSON or XML? (data format is not already defined)
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/forwardToOtherConnector")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "ok"),
      @APIResponse(responseCode = "400", description = "Bad Request"),
      @APIResponse(responseCode = "401", description = "Unauthorized"),
      @APIResponse(responseCode = "403", description = "Forbidden"),
      @APIResponse(responseCode = "404", description = "Not Found"),
      @APIResponse(responseCode = "500", description = "Internal Server Error")
  })
  @Operation(summary = "Send data to second connector")
  public Response forwardToOtherConnector(String connectorUrl, String data, @Context HttpHeaders httpHeaders) {

    //TODO
    return null;
  }

  /**
   * Forward data to other connector.
   *
   * @param data      data to be forwarded.
   * @param httpHeaders httpHeader with mediaType and basicAuth
   * @return if the POST was successful
   */
  @POST
  //JSON or XML? (data format is not already defined)
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/saveLocallyInDirectory")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "ok"),
      @APIResponse(responseCode = "400", description = "Bad Request"),
      @APIResponse(responseCode = "401", description = "Unauthorized"),
      @APIResponse(responseCode = "403", description = "Forbidden"),
      @APIResponse(responseCode = "404", description = "Not Found"),
      @APIResponse(responseCode = "500", description = "Internal Server Error")
  })
  @Operation(summary = "Save file in local directory")
  public Response saveLocallyInDirectory(String data, @Context HttpHeaders httpHeaders) {

    //TODO
    return null;
  }

}
