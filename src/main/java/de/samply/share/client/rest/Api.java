package de.samply.share.client.rest;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.parser.DataFormatException;
import de.samply.share.client.util.connector.CTSConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/")
public class Api {
    private static Logger logger = LogManager.getLogger(Api.class);

    @POST
    @Path("/postCTS")
    public Response postToCTS(String bundle, @HeaderParam("Authorization") String basicAuth) {
        try {
            CTSConnector ctsConnector = new CTSConnector();
            ctsConnector.postPseudonmToCTS(bundle);
            return Response.ok().build();
        } catch (NullPointerException | ConfigurationException | DataFormatException | IllegalArgumentException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
