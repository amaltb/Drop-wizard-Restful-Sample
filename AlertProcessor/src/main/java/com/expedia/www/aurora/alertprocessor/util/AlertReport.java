package com.expedia.www.aurora.alertprocessor.util;


import lombok.Data;

import java.sql.Timestamp;
/**
 * @author - _amal
 *
 * Entity representing Alert-report. For de-serialisation. Response to /api/alerts/report API.
 */

@Data
public class AlertReport {
    private Timestamp generatedAt;
    private String report;

    public AlertReport(String report) {
        this.generatedAt = new Timestamp(System.currentTimeMillis());
        this.report = !report.isEmpty() ? report : "No alerts raised for given template during this period.";
    }
}
