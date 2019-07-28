package com.ab.example.testerapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("PMD")
public class OutlierMessage {
    private Map<String, Object> modelDefinition;
    private AuroraMessage auroraMessage;
    private String message;
    private String alertType;
    private long alertTimestamp;
    private Map<String, String> modelState;
}


