package com.minisocial.rest;

import com.minisocial.entity.GroupPost;
import com.minisocial.service.GroupPostService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/group-posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupPostResource {

    @Inject
    private GroupPostService groupPostService;

    @POST
    public Response createGroupPost(GroupPost post, @QueryParam("userId") Long userId, @QueryParam("groupId") Long groupId) {
        try {
            if (userId == null || groupId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID and Group ID must be provided\"}")
                        .build();
            }
            if (post.getContent() == null || post.getContent().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Post content cannot be empty\"}")
                        .build();
            }
            GroupPost createdPost = groupPostService.createGroupPost(post, userId, groupId);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Group post created\", \"postId\": " + createdPost.getId() + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/group/{groupId}")
    public Response getGroupPosts(@PathParam("groupId") Long groupId, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            List<GroupPost> posts = groupPostService.getGroupPosts(groupId, userId);
            return Response.ok(posts).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{postId}")
    public Response deleteGroupPost(@PathParam("postId") Long postId, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            groupPostService.deleteGroupPost(postId, userId);
            return Response.ok("{\"message\": \"Group post deleted\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}