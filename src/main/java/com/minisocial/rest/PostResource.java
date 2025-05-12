package com.minisocial.rest;

import com.minisocial.entity.Post;
import com.minisocial.service.PostService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @EJB
    private PostService postService;

    @POST
    public Response createPost(@QueryParam("userId") Long userId, Post post) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            postService.createPost(userId, post);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Post created\", \"postId\": " + post.getId() + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/feed")
    public List<Post> getFeed() {
        return postService.getFeed(null); // userId is ignored in getFeed
    }

    @PUT
    @Path("/{id}")
    public Response updatePost(@PathParam("id") Long id, @QueryParam("userId") Long userId, Post updatedPost) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            postService.updatePost(id, userId, updatedPost.getContent(), updatedPost.getImageUrl(), updatedPost.getLink());
            return Response.ok("{\"message\": \"Post updated\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            postService.deletePost(id, userId);
            return Response.ok("{\"message\": \"Post deleted\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}