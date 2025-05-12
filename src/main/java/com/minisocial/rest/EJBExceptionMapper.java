package com.minisocial.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<jakarta.ejb.EJBException> {

    @Override
    public Response toResponse(jakarta.ejb.EJBException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof SecurityException) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"" + cause.getMessage() + "\"}")
                    .type("application/json")
                    .build();
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"An unexpected server error occurred: " + exception.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}