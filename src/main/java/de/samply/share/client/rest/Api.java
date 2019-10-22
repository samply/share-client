package de.samply.share.client.rest;

import ca.uhn.fhir.parser.DataFormatException;
import de.samply.share.client.util.connector.CTSConnector;
import de.samply.share.client.util.connector.exception.CTSConnectorException;
import de.samply.share.client.util.connector.exception.MainzellisteConnectorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Api {
    private static Logger logger = LogManager.getLogger(Api.class);

    @POST
    @Path("/postCTS")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response postToCTS(String bundle, @HeaderParam("Authorization") String basicAuth) {
        try {
            //@TODO basicAuth
            CTSConnector ctsConnector = new CTSConnector();
            // @TODO just for test
            System.out.println("Api/postToCTS(): bundle received");
            System.out.println(bundle);
            ctsConnector.postPseudonmToCTS(bundle);

            return Response.ok().build();
        }catch (MainzellisteConnectorException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
        }catch (CTSConnectorException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
        }catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (DataFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            // @TODO just for test
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

}
