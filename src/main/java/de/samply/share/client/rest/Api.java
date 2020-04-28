package de.samply.share.client.rest;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.parser.DataFormatException;
import de.samply.share.client.util.connector.CTSConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/")
public class Api {
    private static Logger logger = LogManager.getLogger(Api.class);

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/postCTS")
    public Response postToCTS(String bundle, @Context HttpHeaders httpHeaders) {
        String mediaType = httpHeaders.getMediaType().getSubtype();
        try {
            CTSConnector ctsConnector = new CTSConnector();
            ctsConnector.postPseudonmToCTS(bundle, mediaType);
            return Response.ok().build();
        } catch (NullPointerException | ConfigurationException | DataFormatException | IllegalArgumentException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
