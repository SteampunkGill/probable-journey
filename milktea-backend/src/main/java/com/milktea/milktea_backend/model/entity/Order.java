package com.milktea.milktea_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // 导入List

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, length = 32, unique = true)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems; // 订单项

    @Column(name = "status", nullable = false, length = 20)
    private String status; // PAID, MAKING, READY, DELIVERED, REFUNDING...

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal totalAmount;

    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2) // 实际支付金额
    private java.math.BigDecimal actualAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private java.math.BigDecimal discountAmount;
    @Column(name = "balance_discount_amount", precision = 10, scale = 2)
    private java.math.BigDecimal balanceDiscountAmount; // 余额抵扣金额


    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private java.math.BigDecimal deliveryFee;

    @Column(name = "pay_method", length = 20)
    private String payMethod; // ALIPAY, WECHAT

    @Column(name = "transaction_id", length = 64)
    private String transactionId;

    @Column(name = "order_time", updatable = false) // 订单创建时间，设为可空以符合非必须约束原则，且不可更新
    private LocalDateTime orderTime;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "delivery_type", nullable = false, length = 20)
    private String deliveryType; // PICKUP, DELIVERY

    @Column(name = "address_json", columnDefinition = "json")
    private String addressJson;

    @Column(name = "pickup_code", length = 10)
    private String pickupCode;

    @Column(name = "queue_number")
    private Integer queueNumber;

    @Column(name = "estimated_ready_time")
    private LocalDateTime estimatedReadyTime;

    @Column(name = "actual_ready_time")
    private LocalDateTime actualReadyTime;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "remind_count")
    private Integer remindCount;

    @Column(name = "last_remind_time")
    private LocalDateTime lastRemindTime;

    @Column(name = "is_commented")
    private Boolean isCommented;

    @OneToOne(mappedBy = "order")
    private OrderReview review;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}