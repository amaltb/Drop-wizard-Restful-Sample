package com.expedia.www.aurora.alertprocessor.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * @author - _amal
 *
 * class to wrap exception details with out leaking system vulnerabilities.
 */

@Data
public class CustomException extends Throwable implements Serializable {
    private int statusCode;
    private String message;
    private String cause;


    public CustomException(String message) {
        this.message = message;
    }

    public CustomException(int status, String message) {
        this.message = message;
        this.statusCode = status;
    }

    public CustomException(int status, String message, Exception e) {
        this.statusCode = status;
        this.message = message;
        this.cause = e.getMessage();
    }

    public int getStatus() {
        return statusCode;
    }
}
