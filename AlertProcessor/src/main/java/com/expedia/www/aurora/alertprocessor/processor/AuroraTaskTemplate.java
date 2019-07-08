package com.expedia.www.aurora.alertprocessor.processor;

public interface AuroraTaskTemplate {
    void executeTask() throws Exception;
    void logException(Exception e);
}
