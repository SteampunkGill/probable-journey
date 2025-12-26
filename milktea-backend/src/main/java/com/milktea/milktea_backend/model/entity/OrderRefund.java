package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal amount;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "status", length = 20)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "reply", length = 200)
    private String reply;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}