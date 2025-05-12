package com.minisocial.dto;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String role; // Include role to show if the friend is a user or admin

    // Constructor
    public UserProfileDTO(Long id, String name, String email, String bio, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}