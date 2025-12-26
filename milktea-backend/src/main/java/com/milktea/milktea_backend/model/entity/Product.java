package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sku", length = 50, unique = true)
    private String sku;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "main_image_url", length = 255) // 商品主图URL
    private String imageUrl;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal price;
    
    @Column(name = "is_member_exclusive")
    private Boolean isMemberExclusive = false;
    
    @Column(name = "member_price", precision = 10, scale = 2)
    private java.math.BigDecimal memberPrice;
    
    @Column(name = "stock")
    private Integer stock = 0;
    
    @Column(name = "alert_threshold")
    private Integer alertThreshold = 10;
    
    @Column(name = "sales")
    private Integer sales = 0;
    
    @Column(name = "status")
    private Integer status = 1; // 1:上架 0:下架

    @Column(name = "cost_price", precision = 10, scale = 2)
    private java.math.BigDecimal costPrice;

    @Column(name = "is_active", nullable = false) // 是否活跃/上架
    private Boolean isActive = true;

    @Column(name = "is_recommended")
    private Boolean isRecommended = false;
    
    @Column(name = "sugar_content", length = 50)
    private String sugarContent;
    
    @Column(name = "calories", length = 50)
    private String calories;
    
    @Column(name = "default_sweetness", length = 20)
    private String defaultSweetness = "NORMAL";
    
    @Column(name = "default_temperature", length = 20)
    private String defaultTemperature = "ICE";
    
    @Column(name = "support_sweetness", columnDefinition = "json")
    private String supportSweetness;
    
    @Column(name = "support_temperature", columnDefinition = "json")
    private String supportTemperature;

    @Column(name = "tags", columnDefinition = "json")
    private String tags;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}