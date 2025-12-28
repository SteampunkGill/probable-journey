package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // REDUCTION:满减, DISCOUNT:折扣

    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal value;

    @Column(name = "min_amount", precision = 10, scale = 2)
    private java.math.BigDecimal minAmount;

    @Column(name = "total_quantity")
    private Integer totalQuantity; // 优惠券总数量，-1表示不限制

    @Column(name = "issued_quantity")
    private Integer issuedQuantity; // 已发行数量

    @Enumerated(EnumType.STRING)
    @Column(name = "validity_type", nullable = false, length = 20)
    private ValidityType validityType; // 有效期类型：FIXED_DAYS:固定天数, FIXED_PERIOD:固定日期范围

    @Column(name = "validity_days")
    private Integer validityDays; // 有效天数，当validity_type为FIXED_DAYS时有效

    @Column(name = "start_time")
    private LocalDateTime startTime; // 有效期开始时间，当validity_type为FIXED_PERIOD时有效

    @Column(name = "end_time")
    private LocalDateTime endTime; // 有效期结束时间，当validity_type为FIXED_PERIOD时有效

    @Column(name = "rule_json", columnDefinition = "json")
    private String ruleJson;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser = 1;

    @Column(name = "acquire_limit")
    private Integer acquireLimit = 1;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "remaining_quantity")
    private Integer remainingQuantity = 0;

    @Column(name = "usage_scope")
    private String usageScope = "ALL";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (acquireLimit == null) acquireLimit = usageLimitPerUser != null ? usageLimitPerUser : 1;
        if (isActive == null) isActive = true;
        if (remainingQuantity == null) remainingQuantity = (totalQuantity != null && totalQuantity != -1) ? totalQuantity : 999999;
        if (usageScope == null) usageScope = "ALL";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ValidityType {
        FIXED_DAYS,
        FIXED_PERIOD
    }
}