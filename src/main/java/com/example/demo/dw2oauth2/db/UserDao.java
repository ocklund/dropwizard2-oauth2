package com.example.demo.dw2oauth2.db;

import com.example.demo.dw2oauth2.core.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserDao {
	private static final Map<Long, User> DB = new HashMap<>();

	public UserDao() {
		DB.put(1L, new User(1L, "alice", "secret", "ADMIN"));
		DB.put(2L, new User(2L, "bob", "secret", "PARIAH"));
	}

	public Optional<User> findUserByUsernameAndPassword(final String username, final String password) {
		for (Map.Entry<Long, User> entry : DB.entrySet()) {
			User user = entry.getValue();
			if (user.getPassword().equals(password) && user.getUsername().equals(username)) {
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}

	public Optional<User> findUserById(final Long id) {
		if (DB.containsKey(id)) {
			return Optional.of(DB.get(id));
		} else {
			return Optional.empty();
		}
	}
}
