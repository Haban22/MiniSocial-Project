package com.minisocial.rest;

import com.minisocial.entity.Group;
import com.minisocial.service.GroupService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource {

    @Inject
    private GroupService groupService;

    @POST
    public Response createGroup(Group group, @QueryParam("creatorId") Long creatorId) {
        try {
            if (creatorId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Creator ID must be provided\"}")
                        .build();
            }
            if (group.getName() == null || group.getName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Group name cannot be empty\"}")
                        .build();
            }
            Group createdGroup = groupService.createGroup(group, creatorId);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Group created\", \"groupId\": " + createdGroup.getId() + "}")
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

    @PUT
    @Path("/{groupId}")
    public Response updateGroup(@PathParam("groupId") Long groupId, Group group, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            Group updatedGroup = groupService.updateGroup(groupId, group, userId);
            return Response.ok("{\"message\": \"Group updated\", \"groupId\": " + updatedGroup.getId() + "}").build();
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

    @DELETE
    @Path("/{groupId}")
    public Response deleteGroup(@PathParam("groupId") Long groupId, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"User ID must be provided\"}")
                        .build();
            }
            groupService.deleteGroup(groupId, userId);
            return Response.ok("{\"message\": \"Group deleted\"}").build();
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