package com.minisocial.rest;

import com.minisocial.entity.Comment;
import com.minisocial.service.CommentService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/comments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

    @EJB
    private CommentService commentService;

    @POST
    public Response addComment(@QueryParam("userId") Long userId, @QueryParam("postId") Long postId, Comment comment) {
        try {
            if (userId == null || postId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID and Post ID must be provided\"}")
                        .build();
            }
            commentService.addComment(userId, postId, comment.getContent());
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Comment added\", \"commentId\": " + comment.getId() + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/post/{postId}")
    public List<Comment> getCommentsByPost(@PathParam("postId") Long postId) {
        if (postId == null) {
            throw new WebApplicationException("Post ID must be provided", Response.Status.BAD_REQUEST);
        }
        return commentService.getCommentsForPost(postId);
    }
}