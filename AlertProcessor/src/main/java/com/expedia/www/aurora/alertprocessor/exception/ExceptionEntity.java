package com.expedia.www.aurora.alertprocessor.exception;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExceptionEntity implements Serializable {
    private String message;
    private String cause;


    public ExceptionEntity(String message) {
        this.message = message;
    }

    public ExceptionEntity(String message, Exception e) {
        this.message = message;
        this.cause = e.getMessage();
    }
}
