package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "images_json", columnDefinition = "json")
    private String imagesJson;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, PROCESSING, RESOLVED

    @Column(name = "solution", length = 500)
    private String solution;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "compensation", precision = 10, scale = 2)
    private BigDecimal compensation;

    @Column(name = "compensation_type", length = 20)
    private String compensationType; // COUPON, CASH

    @Column(name = "compensation_coupon_id")
    private Long compensationCouponId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}