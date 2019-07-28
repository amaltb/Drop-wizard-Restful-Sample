package com.expedia.www.aurora.alertprocessor.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author - _amal
 *
 * class to wrap exception details with out leaking system vulnerabilities.
 */

@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage", "suppressed"})
public class CustomException extends Throwable implements Serializable {
    private int code;
    private String message;
    private String cause;


    public CustomException(String message) {
        this.message = message;
    }

    public CustomException(int status, String message) {
        this.message = message;
        this.code = status;
    }

    public CustomException(int status, String message, Exception e) {
        this.code = status;
        this.message = message;
        this.cause = e.getMessage();
    }

    public int getStatus() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
