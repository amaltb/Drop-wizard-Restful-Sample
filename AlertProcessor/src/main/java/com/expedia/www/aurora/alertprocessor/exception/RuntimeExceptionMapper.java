package com.expedia.www.aurora.alertprocessor.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException e) {
        return Response.status(400).entity(new ExceptionEntity("Invalid request", e)).build();
    }
}
