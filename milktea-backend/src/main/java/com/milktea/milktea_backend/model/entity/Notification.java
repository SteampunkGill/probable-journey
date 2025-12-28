package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "type", length = 20)
    private String type; // SYSTEM, ORDER, PROMOTION

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "target_type", length = 20)
    private String targetType; // ALL, MEMBER_LEVEL, INDIVIDUAL

    @Column(name = "target_value", length = 100)
    private String targetValue;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Column(name = "status", length = 20)
    private String status; // SENT, PENDING

    @Column(name = "push_type", length = 20)
    private String pushType; // MARKETING, ACTIVITY

    @Column(name = "trigger_type", length = 20)
    private String triggerType; // IMMEDIATE, SCHEDULED, BEHAVIOR_TRIGGER

    @Column(name = "trigger_condition")
    private String triggerCondition;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
}