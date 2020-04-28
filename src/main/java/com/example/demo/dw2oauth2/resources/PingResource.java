package com.example.demo.dw2oauth2.resources;

import static java.lang.String.format;

import com.codahale.metrics.annotation.Timed;
import com.example.demo.dw2oauth2.core.Answer;
import com.example.demo.dw2oauth2.core.User;
import io.dropwizard.auth.Auth;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

	@GET
	@Timed
	public Answer pong() {
		return new Answer("pong");
	}

	@RolesAllowed("ADMIN")
	@GET
	@Timed
	@Path("/auth")
	public Answer pongAuthenticated(@Auth User user) {
		return new Answer(format("Authenticated pong for user %s", user.getName()));
	}
}
