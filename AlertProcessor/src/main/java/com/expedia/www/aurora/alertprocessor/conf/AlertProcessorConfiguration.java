package com.expedia.www.aurora.alertprocessor.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
/**
 * @author - _amal
 *
 * alert processor application configuration.
 */

@Data
public class AlertProcessorConfiguration extends Configuration {
    @JsonProperty("maxRetryCount")
    @NotEmpty
    private String requestRetryCount;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();
}
