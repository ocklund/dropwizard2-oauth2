package com.example.demo.dw2oauth2.auth;

import com.example.demo.dw2oauth2.core.User;
import io.dropwizard.auth.Authorizer;

public class SimpleAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user.getName().equals("alice") && role.equals("ADMIN");
    }
}
