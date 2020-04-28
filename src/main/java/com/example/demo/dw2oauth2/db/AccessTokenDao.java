package com.example.demo.dw2oauth2.db;

import com.example.demo.dw2oauth2.core.User;
import com.example.demo.dw2oauth2.core.AccessToken;
import java.util.Optional;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccessTokenDao {
	private static final Map<UUID, AccessToken> DB = new HashMap<>();

	public Optional<AccessToken> findAccessTokenById(final UUID accessTokenId) {
		AccessToken accessToken = DB.get(accessTokenId);
		if (accessToken == null) {
			return Optional.empty();
		}
		return Optional.of(accessToken);
	}

	public AccessToken generateNewAccessToken(final User user, final DateTime dateTime) {
		AccessToken accessToken = new AccessToken(UUID.randomUUID(), user.getId(), dateTime);
		DB.put(accessToken.getAccessTokenId(), accessToken);
		return accessToken;
	}

	public void setLastAccessTime(final UUID accessTokenUUID, final DateTime dateTime) {
		AccessToken accessToken = DB.get(accessTokenUUID);
		AccessToken updatedAccessToken = accessToken.withLastAccessUTC(dateTime);
		DB.put(accessTokenUUID, updatedAccessToken);
	}
}
