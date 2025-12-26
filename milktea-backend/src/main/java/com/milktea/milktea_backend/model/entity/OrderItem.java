package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_image", length = 200)
    private String productImage;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 10, scale = 2) // 单价
    private java.math.BigDecimal price;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal totalPrice;

    @Column(name = "sweetness", length = 20)
    private String sweetness;

    @Column(name = "temperature", length = 20)
    private String temperature;

    @Column(name = "spec_json", columnDefinition = "json")
    private String specJson;

    @Column(name = "remark", length = 100)
    private String remark;
}