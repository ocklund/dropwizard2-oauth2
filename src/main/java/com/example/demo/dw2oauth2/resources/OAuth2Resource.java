package com.example.demo.dw2oauth2.resources;

import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import com.example.demo.dw2oauth2.core.AccessToken;
import com.example.demo.dw2oauth2.core.User;
import com.example.demo.dw2oauth2.db.AccessTokenDao;
import com.example.demo.dw2oauth2.db.UserDao;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;

@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class OAuth2Resource {

    private final List<String> allowedGrantTypes;
    private final AccessTokenDao accessTokenDAO;
    private final UserDao userDAO;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String getToken(@FormParam("grant_type") String grantType, @FormParam("username") String username,
        @FormParam("password") String password, @FormParam("client_id") String clientId) {

        if (!allowedGrantTypes.contains(grantType)) {
            throw new WebApplicationException(Response.status(METHOD_NOT_ALLOWED).build());
        }

        Optional<User> user = userDAO.findUserByUsernameAndPassword(username, password);
        if (!user.isPresent()) {
            throw new WebApplicationException(Response.status(UNAUTHORIZED).build());
        }

        AccessToken accessToken = accessTokenDAO.generateNewAccessToken(user.get(), new DateTime());
        return accessToken.getAccessTokenId().toString();
    }
}
