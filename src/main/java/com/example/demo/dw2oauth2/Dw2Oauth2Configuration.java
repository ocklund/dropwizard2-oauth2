package com.example.demo.dw2oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;

@Getter
public class Dw2Oauth2Configuration extends Configuration {

    @JsonProperty
    private List<String> allowedGrantTypes;

    @Valid
    @JsonProperty
    private String bearerRealm;
}
