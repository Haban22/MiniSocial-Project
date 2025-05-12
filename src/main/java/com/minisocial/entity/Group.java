package com.minisocial.entity;

import jakarta.persistence.*;
import jakarta.json.bind.annotation.JsonbTransient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private boolean isOpen;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<GroupMembership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<GroupPost> posts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<GroupMembership> getMemberships() { return memberships; }
    public void setMemberships(List<GroupMembership> memberships) { this.memberships = memberships; }
    public List<GroupPost> getPosts() { return posts; }
    public void setPosts(List<GroupPost> posts) { this.posts = posts; }
}