package com.milktea.milktea_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", length = 20, unique = true)
    private String code;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "address_json", columnDefinition = "json")
    private String addressJson;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "manager_name", length = 50)
    private String managerName;

    @Column(name = "manager_phone", length = 20)
    private String managerPhone;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN"; // OPEN, CLOSED, MAINTENANCE

    @Column(name = "business_hours_json", columnDefinition = "json")
    private String businessHoursJson;

    @Column(name = "latitude", precision = 10, scale = 8)
    private java.math.BigDecimal latitude = java.math.BigDecimal.ZERO;

    @Column(name = "longitude", precision = 11, scale = 8)
    private java.math.BigDecimal longitude = java.math.BigDecimal.ZERO;

    @Column(name = "delivery_radius")
    private Integer deliveryRadius = 0;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(name = "is_auto_accept")
    private Boolean isAutoAccept = false;

    @Column(name = "is_online_payment")
    private Boolean isOnlinePayment = true;

    @Column(name = "config_json", columnDefinition = "json")
    private String configJson;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating = new BigDecimal("5.00");

    @Column(name = "business_status")
    private Integer businessStatus = 1;

    @Column(name = "business_hours")
    private String businessHours;

    @Column(name = "is_auto_receipt")
    private Boolean isAutoReceipt = true;

    @Column(name = "current_wait_time")
    private Integer currentWaitTime = 0;

    @Column(name = "open_time")
    private String openTime;

    @Column(name = "close_time")
    private String closeTime;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private Double distance;
}