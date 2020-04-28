package com.example.demo.dw2oauth2;

import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.UUID;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;

public class PingResourceTest {

    @ClassRule
    public static final DropwizardAppRule<Dw2Oauth2Configuration> RULE = new DropwizardAppRule<>(
        Dw2OAuth2Application.class, ResourceHelpers.resourceFilePath("test-config.yml"));

    private static final String ACCEPTED_GRANT_TYPE = "password";
    private static final String ACCEPTED_USERNAME = "alice";
    private static final String ACCEPTED_PASSWORD = "secret";
    private static final String ACCEPTED_CLIENT_ID = "1";
    private static final String CREDENTIALS_REQUIRED_MESSAGE = "Credentials are required to access this resource.";
    private static final String URL_TEMPLATE = "http://localhost:%d%s";
    private static final String PING_AUTH_PATH = "/ping/auth";
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_VALUE_TEMPLATE = "Bearer %s";

    @Test
    public void shouldReturnOKAndPongAnswer() {
        Response response = ClientBuilder.newClient()
            .target(format(URL_TEMPLATE, RULE.getLocalPort(), "/ping"))
            .request()
            .get();

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.readEntity(String.class), is("{\"message\":\"pong\"}"));
    }

    @Test
    public void shouldReturnUnauthorizedForRequestWithoutAccessToken() {
        Response response = ClientBuilder.newClient()
            .target(format(URL_TEMPLATE, RULE.getLocalPort(), PING_AUTH_PATH))
            .request()
            .get();

        assertThat(response.getStatus(), is(UNAUTHORIZED.getStatusCode()));
        assertThat(response.readEntity(String.class), is(CREDENTIALS_REQUIRED_MESSAGE));
    }

    @Test
    public void shouldReturnUnauthorizedForRequestWithNonExistingAccessToken() {
        Response response = ClientBuilder.newClient()
            .target(format(URL_TEMPLATE, RULE.getLocalPort(), PING_AUTH_PATH))
            .request()
            .header(AUTH_HEADER_NAME, format(AUTH_HEADER_VALUE_TEMPLATE, "00000000-0000-0000-0000-000000000000"))
            .get();

        assertThat(response.getStatus(), is(UNAUTHORIZED.getStatusCode()));
        assertThat(response.readEntity(String.class), is(CREDENTIALS_REQUIRED_MESSAGE));
    }

    @Test
    public void shouldReturnUnauthorizedForRequestWithInvalidAccessToken() {
        Response response = ClientBuilder.newClient()
            .target(format(URL_TEMPLATE, RULE.getLocalPort(), PING_AUTH_PATH))
            .request().header(AUTH_HEADER_NAME, format(AUTH_HEADER_VALUE_TEMPLATE, "zzzzz"))
            .get();

        assertThat(response.getStatus(), is(UNAUTHORIZED.getStatusCode()));
        assertThat(response.readEntity(String.class), is(CREDENTIALS_REQUIRED_MESSAGE));
    }

    @Test
    public void shouldReturnOKAndAuthPongWhenCorrectAccessTokenIsProvided() {
        Response response = ClientBuilder.newClient()
            .target(format(URL_TEMPLATE, RULE.getLocalPort(), PING_AUTH_PATH))
            .request()
            .header(AUTH_HEADER_NAME, format(AUTH_HEADER_VALUE_TEMPLATE, getAccessToken()))
            .get();

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.readEntity(String.class), is("{\"message\":\"Authenticated pong for user alice\"}"));
    }

    private String getAccessToken() {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", ACCEPTED_GRANT_TYPE);
        formData.add("username", ACCEPTED_USERNAME);
        formData.add("password", ACCEPTED_PASSWORD);
        formData.add("client_id", ACCEPTED_CLIENT_ID);

        Response response = ClientBuilder.newClient()
            .target(format(URL_TEMPLATE, RULE.getLocalPort(), "/oauth2/token"))
            .request()
            .post(Entity.form(formData));

        String result = response.readEntity(String.class);

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(result, is(UUID.fromString(result).toString()));

        return result;
    }
}
