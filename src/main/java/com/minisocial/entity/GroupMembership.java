package com.minisocial.entity;

import jakarta.persistence.*;
import jakarta.json.bind.annotation.JsonbTransient;

@Entity
@Table(name = "group_memberships")
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonbTransient
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;

    public enum MembershipStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum GroupRole {
        MEMBER, ADMIN
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }
    public GroupRole getRole() { return role; }
    public void setRole(GroupRole role) { this.role = role; }
}