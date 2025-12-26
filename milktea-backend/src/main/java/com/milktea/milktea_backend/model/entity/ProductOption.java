package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // TOPPING(加料), SIZE(杯型)

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(name = "stock")
    private Integer stock;
}