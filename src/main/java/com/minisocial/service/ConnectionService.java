package com.minisocial.service;

import com.minisocial.entity.FriendRequest;
import com.minisocial.entity.Friendship;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ConnectionService {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private NotificationService notificationService;

    @Transactional
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to self");
        }
        User sender = em.find(User.class, senderId);
        User receiver = em.find(User.class, receiverId);
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<FriendRequest> existingRequests = em.createQuery(
                        "SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId AND fr.receiver.id = :receiverId AND fr.status = 'PENDING'", FriendRequest.class)
                .setParameter("senderId", senderId)
                .setParameter("receiverId", receiverId)
                .getResultList();
        if (!existingRequests.isEmpty()) {
            throw new IllegalArgumentException("Friend request already sent");
        }
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        em.persist(request);

        // Send notification to receiver
        notificationService.sendNotification(
                "FRIEND_REQUEST",
                senderId,
                receiverId,
                request.getId(),
                sender.getName() + " sent you a friend request"
        );

        return request;
    }

    @Transactional
    public Friendship acceptFriendRequest(Long requestId, Long userId) {
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request == null || !request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Invalid friend request");
        }
        if (!request.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Friend request already processed");
        }
        request.setStatus("ACCEPTED");
        em.merge(request);

        Friendship friendship = new Friendship();
        friendship.setUser1(request.getSender());
        friendship.setUser2(request.getReceiver());
        friendship.setCreatedAt(LocalDateTime.now());
        em.persist(friendship);
        return friendship;
    }

    @Transactional
    public void rejectFriendRequest(Long requestId, Long userId) {
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request == null || !request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Invalid friend request");
        }
        if (!request.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Friend request already processed");
        }
        request.setStatus("REJECTED");
        em.merge(request);
    }

    public List<FriendRequest> getPendingRequests(Long userId) {
        return em.createQuery("SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :userId AND fr.status = 'PENDING'", FriendRequest.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Friendship> getFriends(Long userId) {
        return em.createQuery("SELECT f FROM Friendship f WHERE f.user1.id = :userId OR f.user2.id = :userId", Friendship.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}