package com.minisocial.service;

import com.minisocial.entity.Group;
import com.minisocial.entity.GroupMembership;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
public class GroupMembershipService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public GroupMembership requestJoinGroup(Long groupId, Long userId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);
        if (group == null || user == null) {
            throw new IllegalArgumentException("Group or user not found");
        }

        try {
            GroupMembership existingMembership = em.createQuery(
                            "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.status IN (:pending, :approved)",
                            GroupMembership.class)
                    .setParameter("groupId", groupId)
                    .setParameter("userId", userId)
                    .setParameter("pending", GroupMembership.MembershipStatus.PENDING)
                    .setParameter("approved", GroupMembership.MembershipStatus.APPROVED)
                    .getSingleResult();
            throw new IllegalStateException("User already has a pending or approved request for this group");
        } catch (jakarta.persistence.NoResultException e) {
            // No existing request found
        }

        if (isGroupMember(groupId, userId)) {
            throw new IllegalStateException("User is already a member");
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setRole(GroupMembership.GroupRole.MEMBER);
        membership.setStatus(group.isOpen() ? GroupMembership.MembershipStatus.APPROVED : GroupMembership.MembershipStatus.PENDING);
        em.persist(membership);
        return membership;
    }

    @Transactional
    public GroupMembership approveMembership(Long membershipId, Long adminId) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership not found");
        }
        if (!isGroupAdmin(membership.getGroup().getId(), adminId)) {
            throw new SecurityException("User is not an admin");
        }
        if (membership.getStatus() == GroupMembership.MembershipStatus.APPROVED) {
            throw new IllegalStateException("Membership is already approved");
        }

        membership.setStatus(GroupMembership.MembershipStatus.APPROVED);
        em.merge(membership);
        return membership;
    }

    @Transactional
    public GroupMembership rejectMembership(Long membershipId, Long adminId) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership not found");
        }
        if (!isGroupAdmin(membership.getGroup().getId(), adminId)) {
            throw new SecurityException("User is not an admin");
        }
        membership.setStatus(GroupMembership.MembershipStatus.REJECTED);
        em.merge(membership);
        return membership;
    }

    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        GroupMembership membership = em.createQuery(
                        "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId",
                        GroupMembership.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult();
        if (membership == null) {
            throw new IllegalArgumentException("Membership not found");
        }
        if (membership.getStatus() != GroupMembership.MembershipStatus.APPROVED && membership.getStatus() != GroupMembership.MembershipStatus.PENDING) {
            throw new IllegalStateException("User is not an approved or pending member of this group");
        }
        if (membership.getRole() == GroupMembership.GroupRole.ADMIN) {
            long adminCount = em.createQuery(
                            "SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.role = :role AND gm.status = :status",
                            Long.class)
                    .setParameter("groupId", groupId)
                    .setParameter("role", GroupMembership.GroupRole.ADMIN)
                    .setParameter("status", GroupMembership.MembershipStatus.APPROVED)
                    .getSingleResult();
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot leave group: You are the last admin");
            }
        }
        em.remove(membership);
    }

    @Transactional
    public GroupMembership promoteToAdmin(Long membershipId, Long adminId) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership not found");
        }
        if (!isGroupAdmin(membership.getGroup().getId(), adminId)) {
            throw new SecurityException("User is not an admin");
        }
        membership.setRole(GroupMembership.GroupRole.ADMIN);
        em.merge(membership);
        return membership;
    }

    @Transactional
    public void removeMember(Long membershipId, Long adminId) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership not found");
        }
        if (!isGroupAdmin(membership.getGroup().getId(), adminId)) {
            throw new SecurityException("User is not an admin");
        }
        em.remove(membership);
    }

    public boolean isGroupAdmin(Long groupId, Long userId) {
        try {
            GroupMembership membership = em.createQuery(
                            "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.role = :role",
                            GroupMembership.class)
                    .setParameter("groupId", groupId)
                    .setParameter("userId", userId)
                    .setParameter("role", GroupMembership.GroupRole.ADMIN)
                    .getSingleResult();
            return membership != null && membership.getStatus() == GroupMembership.MembershipStatus.APPROVED;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean isGroupMember(Long groupId, Long userId) {
        try {
            GroupMembership membership = em.createQuery(
                            "SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.status = :status",
                            GroupMembership.class)
                    .setParameter("groupId", groupId)
                    .setParameter("userId", userId)
                    .setParameter("status", GroupMembership.MembershipStatus.APPROVED)
                    .getSingleResult();
            return membership != null;
        } catch (NoResultException e) {
            return false;
        }
    }
}