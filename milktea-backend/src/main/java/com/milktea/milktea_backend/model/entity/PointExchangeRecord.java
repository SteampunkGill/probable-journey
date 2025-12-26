package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_exchange_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PointExchangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_no", nullable = false, length = 50)
    private String orderNo;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "total_items", nullable = false)
    private Integer totalItems;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private java.math.BigDecimal shippingFee;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "remark", length = 200)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}