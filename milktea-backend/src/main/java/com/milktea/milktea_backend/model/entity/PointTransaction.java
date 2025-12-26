package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // EARN, SPEND, EXPIRE

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "balance_after_transaction", nullable = false)
    private Integer balanceAfter;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "remark", length = 200)
    private String remark;
    @Column(name = "description")
    private String description;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}