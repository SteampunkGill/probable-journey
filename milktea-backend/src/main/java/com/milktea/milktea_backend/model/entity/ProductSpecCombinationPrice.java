package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_spec_combination_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductSpecCombinationPrice {

    @EmbeddedId
    private ProductSpecCombinationPriceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("combinationId")
    @JoinColumn(name = "combination_id", nullable = false)
    private ProductSpecCombination combination;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal price;
}
