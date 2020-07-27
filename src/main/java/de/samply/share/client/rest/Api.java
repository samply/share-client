package de.samply.share.client.rest;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.parser.DataFormatException;
import com.mchange.rmi.NotAuthorizedException;
import com.sun.jersey.api.NotFoundException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.db.tables.pojos.User;
import de.samply.share.client.util.connector.CTSConnector;
import de.samply.share.client.util.db.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Path("/")
public class Api {
    private static Logger logger = LogManager.getLogger(Api.class);

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/postcts")
    public Response postToCTS(String bundle, @Context HttpHeaders httpHeaders) {
        try {
            if (!checkUser(httpHeaders.getRequestHeader(AUTHORIZATION).get(0))) {
                return Response.status(401).entity("Basic Auth credentials not correct").build();
            }
            String mediaType = httpHeaders.getMediaType().getSubtype();
            CTSConnector ctsConnector = ApplicationBean.getCtsConnector();
            return ctsConnector.postPseudonmToCTS(bundle, mediaType);
        } catch (NullPointerException | ConfigurationException | DataFormatException | IllegalArgumentException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (NotAuthorizedException e) {
            return Response.status(401).entity(e.getMessage()).build();
        } catch (NotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
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
