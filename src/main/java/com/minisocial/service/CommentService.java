package com.minisocial.service;

import com.minisocial.entity.Comment;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class CommentService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void addComment(Long userId, Long postId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);
        if (post == null || user == null) {
            throw new IllegalArgumentException("Post or user not found");
        }
        // Removed friendship validation
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());
        em.persist(comment);
    }

    public List<Comment> getCommentsForPost(Long postId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}