package com.expedia.www.aurora.alertprocessor.util;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import io.dropwizard.lifecycle.Managed;

/**
 * @author - _amal
 *
 * registering to drop-wizard application metrics.
 */

final public class ServiceMetrics implements Managed {
    private final JmxReporter jmxReporter;
    private static final String METRIC_REGISTRY_KEY = "alert-processor-service";

    public ServiceMetrics() {
        jmxReporter = JmxReporter.forRegistry(SharedMetricRegistries.getOrCreate(METRIC_REGISTRY_KEY))
                .build();
    }

    public static MetricRegistry getRegistry() {
        return SharedMetricRegistries.getOrCreate(METRIC_REGISTRY_KEY);
    }

    @Override
    public void start() throws Exception {
        jmxReporter.start();
    }

    @Override
    public void stop() throws Exception {
        jmxReporter.stop();
    }
}