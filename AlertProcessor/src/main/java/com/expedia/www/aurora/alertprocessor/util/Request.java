package com.expedia.www.aurora.alertprocessor.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * @author - _amal
 *
 * Entity representing /api/alerts/report Http request body.
 */

@Data
public class Request {

    @NotEmpty
    @JsonProperty("anomaly_template_alias")
    private String templateAliasName;

    @JsonProperty("key_set")
    private Map<String, List<String>> keySet;
}
