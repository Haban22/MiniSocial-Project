package com.minisocial.rest;

import com.minisocial.service.PostLikeService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostLikeResource {

    @EJB
    private PostLikeService postLikeService;

    @POST
    @Path("/{id}/like")
    public Response likePost(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            postLikeService.addLike(userId, id);
            return Response.ok("{\"message\": \"Post liked\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}