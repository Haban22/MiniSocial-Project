package com.minisocial.dto;

import java.time.LocalDateTime;

public class NotificationEvent {

    private String eventType;
    private LocalDateTime timestamp;
    private Long sourceUserId;
    private Long targetUserId;
    private Long relatedEntityId;
    private String details;

    public NotificationEvent() {
    }

    public NotificationEvent(String eventType, Long sourceUserId, Long targetUserId, Long relatedEntityId, String details) {
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.relatedEntityId = relatedEntityId;
        this.details = details;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(Long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}