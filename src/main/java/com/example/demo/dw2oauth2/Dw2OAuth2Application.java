package com.example.demo.dw2oauth2;

import com.example.demo.dw2oauth2.auth.SimpleAuthenticator;
import com.example.demo.dw2oauth2.auth.SimpleAuthorizer;
import com.example.demo.dw2oauth2.core.User;
import com.example.demo.dw2oauth2.db.AccessTokenDao;
import com.example.demo.dw2oauth2.db.UserDao;
import com.example.demo.dw2oauth2.health.PingHealthCheck;
import com.example.demo.dw2oauth2.resources.OAuth2Resource;
import com.example.demo.dw2oauth2.resources.PingResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.joda.time.DateTimeZone;

public class Dw2OAuth2Application extends Application<Dw2Oauth2Configuration> {

	public static void main(String[] args) throws Exception {
		new Dw2OAuth2Application().run(args);
	}

	@Override
	public String getName() {
		return "Dw2Oauth2";
	}

	@Override
	public void initialize(Bootstrap<Dw2Oauth2Configuration> oauth2ConfigurationBootstrap) {
		DateTimeZone.setDefault(DateTimeZone.UTC);
	}

	@Override
	public void run(Dw2Oauth2Configuration configuration, Environment environment) throws Exception {
		AccessTokenDao accessTokenDAO = new AccessTokenDao();
		UserDao userDAO = new UserDao();

		environment.jersey().register(new PingResource());
		environment.jersey().register(new OAuth2Resource(configuration.getAllowedGrantTypes(), accessTokenDAO, userDAO));
		environment.healthChecks().register("Ping health check", new PingHealthCheck(new PingResource()));

		// Use Oauth for protecting endpoint.
		environment.jersey().register(new AuthDynamicFeature(
			new OAuthCredentialAuthFilter.Builder<User>()
				.setAuthenticator(new SimpleAuthenticator(accessTokenDAO, userDAO))
				.setAuthorizer(new SimpleAuthorizer())
				.setPrefix("Bearer")
				.buildAuthFilter()));
		// Use @RolesAllowed annotation to only allow access to specific roles.
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		// Use @Auth annotation to inject a custom Principal type into the endpoint call.
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
	}
}
