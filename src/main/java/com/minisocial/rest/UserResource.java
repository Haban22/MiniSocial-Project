package com.minisocial.rest;

import com.minisocial.entity.User;
import com.minisocial.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
public class UserResource {
    @EJB
    private UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        try {
            User registeredUser = userService.register(user);
            Long userId = registeredUser.getId();
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"User registered successfully with id " + userId + ".\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user) {
        try {
            User authenticatedUser = userService.login(user.getEmail(), user.getPassword());
            return Response.ok()
                    .entity("{\"message\": \"Login successful.\", \"token\": \"JWT-TOKEN\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{userId}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(@PathParam("userId") Long userId, User updatedUser) {
        try {
            userService.updateProfile(userId, updatedUser);
            return Response.ok()
                    .entity("{\"message\": \"Profile updated successfully.\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}