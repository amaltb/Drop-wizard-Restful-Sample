package com.ab.example.testerapp.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class TesterAppConfiguration extends Configuration {

    @JsonProperty("alertserviceURI")
    private String alertProcessorServiceURI;

    @Valid
    @NotNull
    @JsonProperty("httpClientConfig")
    private final JerseyClientConfiguration httpClientConfiguration = new JerseyClientConfiguration();
}
