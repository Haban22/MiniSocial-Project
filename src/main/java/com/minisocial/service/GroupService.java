package com.minisocial.service;

import com.minisocial.entity.Group;
import com.minisocial.entity.User;
import com.minisocial.entity.GroupMembership;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
public class GroupService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Group createGroup(Group group, Long creatorId) {
        User creator = em.find(User.class, creatorId);
        if (creator == null) {
            throw new IllegalArgumentException("Creator not found");
        }
        group.setCreator(creator);
        em.persist(group);

        // Add creator as admin
        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(creator);
        membership.setStatus(GroupMembership.MembershipStatus.APPROVED);
        membership.setRole(GroupMembership.GroupRole.ADMIN);
        em.persist(membership);

        return group;
    }

    @Transactional
    public Group updateGroup(Long groupId, Group updatedGroup, Long userId) {
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }
        if (!isGroupAdmin(groupId, userId)) {
            throw new SecurityException("User is not an admin");
        }
        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        group.setOpen(updatedGroup.isOpen());
        em.merge(group);
        return group;
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }
        if (!isGroupAdmin(groupId, userId)) {
            throw new SecurityException("User is not an admin");
        }
        User admin = em.find(User.class, userId);
        System.out.println("Deleting group " + group.getName() + " by admin " + admin.getName());
        em.remove(group);
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
        } catch (Exception e) {
            return false;
        }
    }
}