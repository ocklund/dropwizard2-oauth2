package com.example.demo.dw2oauth2.auth;

import com.example.demo.dw2oauth2.core.AccessToken;
import com.example.demo.dw2oauth2.core.User;
import com.example.demo.dw2oauth2.db.AccessTokenDao;
import com.example.demo.dw2oauth2.db.UserDao;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.UUID;

@AllArgsConstructor
public class SimpleAuthenticator implements Authenticator<String, User> {
	public static final int ACCESS_TOKEN_EXPIRE_TIME_MIN = 30;
	private final AccessTokenDao accessTokenDAO;
	private final UserDao userDAO;

	@Override
	public Optional<User> authenticate(String accessTokenId) throws AuthenticationException {
		UUID accessTokenUUID;
		try {
			accessTokenUUID = UUID.fromString(accessTokenId);
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}

		Optional<AccessToken> accessToken = accessTokenDAO.findAccessTokenById(accessTokenUUID);
		if (!accessToken.isPresent()) {
			return Optional.empty();
		}

		Period period = new Period(accessToken.get().getLastAccessUTC(), new DateTime());
		if (period.getMinutes() > ACCESS_TOKEN_EXPIRE_TIME_MIN) {
			return Optional.empty();
		}

		accessTokenDAO.setLastAccessTime(accessTokenUUID, new DateTime());

		return userDAO.findUserById(accessToken.get().getUserId());
	}
}
