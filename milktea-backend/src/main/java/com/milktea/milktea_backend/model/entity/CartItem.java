package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id")
    private ProductSpec spec;

    private String sweetness;
    private String temperature;
    private Integer quantity;

    @Column(name = "is_selected", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean isSelected = true;

    @Column(name = "is_valid", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean isValid = true;

    @Column(name = "price_at_add", nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal priceAtAdd = BigDecimal.ZERO;

    @Column(name = "original_price_at_add")
    private BigDecimal originalPriceAtAdd;

    @Column(name = "invalid_reason")
    private String invalidReason;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemCustomization> customizations;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}