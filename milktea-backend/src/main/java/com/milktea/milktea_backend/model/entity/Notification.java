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

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "target_type", length = 20)
    private String targetType; // ALL, MEMBER_LEVEL, INDIVIDUAL

    @Column(name = "target_value", length = 100)
    private String targetValue;

    @CreationTimestamp
    @Column(name = "sent_time", updatable = false)
    private LocalDateTime sentTime;

    @Column(name = "status", length = 20)
    private String status; // SENT

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