package com.minisocial.rest;

import com.minisocial.entity.GroupMembership;
import com.minisocial.service.GroupMembershipService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/group-memberships")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupMembershipResource {

    @Inject
    private GroupMembershipService membershipService;

    @POST
    @Path("/request")
    public Response requestJoinGroup(@QueryParam("groupId") Long groupId, @QueryParam("userId") Long userId) {
        try {
            if (groupId == null || userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Group ID and User ID must be provided\"}")
                        .build();
            }
            GroupMembership membership = membershipService.requestJoinGroup(groupId, userId);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Membership request created\", \"membershipId\": " + membership.getId() + "}")
                    .build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/{membershipId}/approve")
    public Response approveMembership(@PathParam("membershipId") Long membershipId, @QueryParam("adminId") Long adminId) {
        try {
            if (adminId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Admin ID must be provided\"}")
                        .build();
            }
            GroupMembership membership = membershipService.approveMembership(membershipId, adminId);
            return Response.ok("{\"message\": \"Membership approved\", \"membershipId\": " + membership.getId() + "}").build();
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

    @POST
    @Path("/{membershipId}/reject")
    public Response rejectMembership(@PathParam("membershipId") Long membershipId, @QueryParam("adminId") Long adminId) {
        try {
            if (adminId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Admin ID must be provided\"}")
                        .build();
            }
            GroupMembership membership = membershipService.rejectMembership(membershipId, adminId);
            return Response.ok("{\"message\": \"Membership rejected\", \"membershipId\": " + membership.getId() + "}").build();
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
    @Path("/leave")
    public Response leaveGroup(@QueryParam("groupId") Long groupId, @QueryParam("userId") Long userId) {
        try {
            if (groupId == null || userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Group ID and User ID must be provided\"}")
                        .build();
            }
            membershipService.leaveGroup(groupId, userId);
            return Response.ok("{\"message\": \"User left group\"}").build();
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

    @POST
    @Path("/{membershipId}/promote")
    public Response promoteToAdmin(@PathParam("membershipId") Long membershipId, @QueryParam("adminId") Long adminId) {
        try {
            if (adminId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Admin ID must be provided\"}")
                        .build();
            }
            GroupMembership membership = membershipService.promoteToAdmin(membershipId, adminId);
            return Response.ok("{\"message\": \"User promoted to admin\", \"membershipId\": " + membership.getId() + "}").build();
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
    @Path("/{membershipId}/remove")
    public Response removeMember(@PathParam("membershipId") Long membershipId, @QueryParam("adminId") Long adminId) {
        try {
            if (adminId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Admin ID must be provided\"}")
                        .build();
            }
            membershipService.removeMember(membershipId, adminId);
            return Response.ok("{\"message\": \"Member removed\"}").build();
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