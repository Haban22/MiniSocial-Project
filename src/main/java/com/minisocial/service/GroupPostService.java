package com.minisocial.service;

import com.minisocial.entity.Group;
import com.minisocial.entity.GroupPost;
import com.minisocial.entity.User;
import com.minisocial.entity.GroupMembership;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class GroupPostService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public GroupPost createGroupPost(GroupPost post, Long userId, Long groupId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);
        if (group == null || user == null) {
            throw new IllegalArgumentException("Group or user not found");
        }
        if (!isGroupMember(groupId, userId)) {
            throw new SecurityException("User is not a group member");
        }
        post.setGroup(group);
        post.setAuthor(user);
        em.persist(post);
        return post;
    }

    @Transactional
    public void deleteGroupPost(Long postId, Long userId) {
        GroupPost post = em.find(GroupPost.class, postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        if (!post.getAuthor().getId().equals(userId) && !isGroupAdmin(post.getGroup().getId(), userId)) {
            throw new SecurityException("User is not authorized to delete this post");
        }
        em.remove(post);
    }

    public List<GroupPost> getGroupPosts(Long groupId, Long userId) {
        if (!isGroupMember(groupId, userId)) {
            throw new SecurityException("User is not a group member");
        }
        return em.createQuery("SELECT gp FROM GroupPost gp WHERE gp.group.id = :groupId ORDER BY gp.createdAt DESC", GroupPost.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    private boolean isGroupAdmin(Long groupId, Long userId) {
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

    private boolean isGroupMember(Long groupId, Long userId) {
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