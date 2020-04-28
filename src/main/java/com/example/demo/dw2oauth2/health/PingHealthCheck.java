package com.example.demo.dw2oauth2.health;

import com.codahale.metrics.health.HealthCheck;
import com.example.demo.dw2oauth2.core.Answer;
import com.example.demo.dw2oauth2.core.User;
import com.example.demo.dw2oauth2.resources.PingResource;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PingHealthCheck extends HealthCheck {
	private final PingResource pingResource;

	@Override
	protected Result check() throws Exception {
		Answer message = pingResource.pongAuthenticated(new User(1L, "alice", null, null));
		if (message.getMessage().contains("alice")) {
			return Result.healthy();
		}
		return Result.unhealthy("Auth ping should contain username");
	}
}
