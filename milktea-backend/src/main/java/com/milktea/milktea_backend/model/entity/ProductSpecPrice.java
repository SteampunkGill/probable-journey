package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_spec_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductSpecPrice {

    @EmbeddedId
    private ProductSpecPriceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("specItemId")
    @JoinColumn(name = "spec_item_id", nullable = false)
    private ProductSpecItem specItem;

    @Column(name = "price_adjustment", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal priceAdjustment;
}
