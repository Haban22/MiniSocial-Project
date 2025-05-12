package com.minisocial.service;

import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class PostService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void createPost(Long userId, Post post) {
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        User author = em.find(User.class, userId);
        if (author == null) {
            throw new IllegalArgumentException("Author not found");
        }
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        em.persist(post);
    }

    public List<Post> getFeed(Long userId) {
        return em.createQuery(
                        "SELECT p FROM Post p LEFT JOIN FETCH p.comments ORDER BY p.createdAt DESC", Post.class)
                .getResultList();
    }

    @Transactional
    public void updatePost(Long postId, Long userId, String newContent, String newImageUrl, String newLink) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the author can update this post");
        }
        post.setContent(newContent);
        post.setImageUrl(newImageUrl);
        post.setLink(newLink);
        em.merge(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the author can delete this post");
        }
        em.remove(post);
    }
}