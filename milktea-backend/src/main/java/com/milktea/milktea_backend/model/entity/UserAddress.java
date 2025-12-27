package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    @Column(name = "province", nullable = false, length = 50)
    private String province;
    
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    
    @Column(name = "district", nullable = false, length = 50)
    private String district;
    
    @Column(name = "detail", nullable = false, length = 200)
    private String detail;
    
    @Column(name = "longitude", precision = 10, scale = 7)
    private java.math.BigDecimal longitude;
    
    @Column(name = "latitude", precision = 10, scale = 7)
    private java.math.BigDecimal latitude;
    
    @Column(name = "tag", length = 20)
    private String tag;

    @Column(name = "is_history")
    private Boolean isHistory = false;

    @Column(name = "type", length = 20)
    private String type = "RECEIVE";
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Column(name = "used_count")
    private Integer usedCount = 0;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}