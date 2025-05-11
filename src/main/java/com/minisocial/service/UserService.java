package com.minisocial.service;

import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@Stateless
public class UserService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public User register(User user) {
        if (em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", user.getEmail())
                .getResultList().size() > 0) {
            throw new IllegalArgumentException("Email already exists");
        }
        em.persist(user);
        return user;
    }

    public User login(String email, String password) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList();
        if (users.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return users.get(0);
    }

    @Transactional
    public User updateProfile(Long userId, User updatedUser) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setName(updatedUser.getName());
        user.setBio(updatedUser.getBio());
        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }
        em.merge(user);
        return user;
    }
}