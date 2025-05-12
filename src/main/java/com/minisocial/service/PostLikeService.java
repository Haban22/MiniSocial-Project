package com.minisocial.service;

import com.minisocial.entity.PostLike;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Stateless
public class PostLikeService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private NotificationService notificationService;

    @Transactional
    public void addLike(Long userId, Long postId) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);
        if (post == null || user == null) {
            throw new IllegalArgumentException("Post or user not found");
        }
        // Removed friendship validation
        boolean alreadyLiked = em.createQuery(
                        "SELECT COUNT(l) FROM PostLike l WHERE l.user.id = :userId AND l.post.id = :postId", Long.class)
                .setParameter("userId", userId)
                .setParameter("postId", postId)
                .getSingleResult() > 0;
        if (alreadyLiked) {
            throw new IllegalArgumentException("User already liked this post");
        }
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postLike.setCreatedAt(LocalDateTime.now());
        em.persist(postLike);

        // Send notification to post owner if not self
        if (!post.getAuthor().getId().equals(userId)) {
            notificationService.sendNotification(
                    "POST_LIKED",
                    userId,
                    post.getAuthor().getId(),
                    postId,
                    user.getName() + " liked your post"
            );
        }

    }
}