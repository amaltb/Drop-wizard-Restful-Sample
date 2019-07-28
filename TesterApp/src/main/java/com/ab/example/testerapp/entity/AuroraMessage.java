package com.ab.example.testerapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuroraMessage {
    Map<String,String> keys;
    String metricName;
    Map<String, Object> dataPoint;
}
