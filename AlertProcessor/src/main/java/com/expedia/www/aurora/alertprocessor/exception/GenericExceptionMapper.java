package com.expedia.www.aurora.alertprocessor.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author - _amal
 *
 * exception mapper to return meaningful http response in case of an exception.
 */

public class GenericExceptionMapper implements ExceptionMapper<CustomException> {

    @Override
    public Response toResponse(CustomException e) {
        return Response.status(e.getStatus()).entity(e).build();
    }
}
