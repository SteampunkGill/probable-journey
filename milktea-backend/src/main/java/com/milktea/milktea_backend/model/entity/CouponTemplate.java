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

    public enum ValidityType {
        FIXED_DAYS,
        FIXED_PERIOD
    }
}