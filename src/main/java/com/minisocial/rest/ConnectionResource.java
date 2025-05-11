package com.minisocial.rest;

import com.minisocial.entity.FriendRequest;
import com.minisocial.entity.Friendship;
import com.minisocial.service.ConnectionService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/connections")
public class ConnectionResource {
    @EJB
    private ConnectionService connectionService;

    @POST
    @Path("/friend-request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendFriendRequest(@QueryParam("senderId") Long senderId, @QueryParam("receiverId") Long receiverId) {
        try {
            FriendRequest request = connectionService.sendFriendRequest(senderId, receiverId);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Friend request sent.\", \"requestId\": " + request.getId() + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/friend-request/{requestId}/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptFriendRequest(@PathParam("requestId") Long requestId, @QueryParam("userId") Long userId) {
        try {
            connectionService.acceptFriendRequest(requestId, userId);
            return Response.ok()
                    .entity("{\"message\": \"Friend request accepted.\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/friend-request/{requestId}/reject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectFriendRequest(@PathParam("requestId") Long requestId, @QueryParam("userId") Long userId) {
        try {
            connectionService.rejectFriendRequest(requestId, userId);
            return Response.ok()
                    .entity("{\"message\": \"Friend request rejected.\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/pending-requests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingRequests(@QueryParam("userId") Long userId) {
        List<FriendRequest> requests = connectionService.getPendingRequests(userId);
        return Response.ok(requests).build();
    }

    @GET
    @Path("/friends")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriends(@QueryParam("userId") Long userId) {
        List<Friendship> friends = connectionService.getFriends(userId);
        return Response.ok(friends).build();
    }
}