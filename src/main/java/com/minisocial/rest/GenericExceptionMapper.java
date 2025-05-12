package com.minisocial.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        // Log the exception for debugging
        exception.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"An unexpected error occurred: " + exception.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}