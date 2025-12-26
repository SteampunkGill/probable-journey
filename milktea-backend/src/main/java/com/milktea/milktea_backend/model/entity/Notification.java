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
}