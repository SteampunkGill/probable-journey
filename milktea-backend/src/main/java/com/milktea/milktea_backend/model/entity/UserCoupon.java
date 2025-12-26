package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id", nullable = false)
    private CouponTemplate couponTemplate;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "is_used", nullable = false)
    private boolean used; // 是否已使用

    @Column(name = "status", length = 20)
    private String status; // UNUSED, USED, EXPIRED

    @Column(name = "receive_time", updatable = false)
    private LocalDateTime receiveTime; // 领取时间

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // 有效期开始时间

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // 有效期结束时间

    @Column(name = "used_order_id")
    private Long usedOrderId;
}