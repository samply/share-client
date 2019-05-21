package de.samply.share.client.rest;

import de.samply.share.client.fhir.FHIRResource;
import de.samply.share.client.util.connector.CTSConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
        }catch (NullPointerException e){
            return Response.status(400).entity(e.getMessage()).build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }

}
